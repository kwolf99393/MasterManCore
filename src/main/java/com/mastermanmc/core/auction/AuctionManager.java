package com.mastermanmc.core.auction;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AuctionManager {

    private final MasterManCore plugin;
    private final List<AuctionItem> listings = new ArrayList<>();
    private File dataFile;
    private FileConfiguration data;
    private final Map<UUID, Inventory> openInventories = new HashMap<>();

    public AuctionManager(MasterManCore plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "auctions.yml");
    }

    public void load() {
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
        listings.clear();
        if (data.getConfigurationSection("listings") != null) {
            for (String key : data.getConfigurationSection("listings").getKeys(false)) {
                try {
                    String path = "listings." + key;
                    UUID seller = UUID.fromString(data.getString(path + ".seller"));
                    String sellerName = data.getString(path + ".sellerName", "Unknown");
                    double price = data.getDouble(path + ".price");
                    long expire = data.getLong(path + ".expire");
                    ItemStack item = data.getItemStack(path + ".item");
                    if (item != null && expire > System.currentTimeMillis()) {
                        listings.add(new AuctionItem(UUID.fromString(key), seller, sellerName, item, price, expire));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        plugin.getLogger().info("Loaded " + listings.size() + " auction listings.");
    }

    public void save() {
        if (data == null) return;
        data.set("listings", null);
        for (AuctionItem item : listings) {
            String path = "listings." + item.getId().toString();
            data.set(path + ".seller", item.getSeller().toString());
            data.set(path + ".sellerName", item.getSellerName());
            data.set(path + ".price", item.getPrice());
            data.set(path + ".expire", item.getExpireTime());
            data.set(path + ".item", item.getItem());
        }
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean createListing(Player player, ItemStack item, double price) {
        int max = plugin.getConfig().getInt("auction-house.max-listings-per-player", 10);
        long count = listings.stream().filter(a -> a.getSeller().equals(player.getUniqueId())).count();
        if (count >= max) {
            plugin.getMessageUtil().send(player, "&cYou have reached the maximum number of listings!");
            return false;
        }
        double min = plugin.getConfig().getDouble("auction-house.min-price", 10);
        double maxPrice = plugin.getConfig().getDouble("auction-house.max-price", 1e7);
        if (price < min || price > maxPrice) {
            plugin.getMessageUtil().send(player, "&cPrice must be between " + plugin.getEconomyManager().format(min) + " and " + plugin.getEconomyManager().format(maxPrice));
            return false;
        }
        int hours = plugin.getConfig().getInt("auction-house.expire-hours", 48);
        long expire = System.currentTimeMillis() + (hours * 3600L * 1000L);
        AuctionItem auction = new AuctionItem(UUID.randomUUID(), player.getUniqueId(), player.getName(), item.clone(), price, expire);
        listings.add(auction);
        player.getInventory().setItemInMainHand(null);
        plugin.getMessageUtil().send(player, "&aItem listed for " + plugin.getEconomyManager().format(price) + "!");
        save();
        return true;
    }

    public void openAuctionHouse(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, plugin.getMessageUtil().color("&8Auction House"));
        int slot = 0;
        for (AuctionItem auction : listings) {
            if (slot >= 45) break;
            if (auction.getExpireTime() < System.currentTimeMillis()) continue;
            ItemStack display = auction.getItem().clone();
            ItemMeta meta = display.getItemMeta();
            List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            lore.add("");
            lore.add(plugin.getMessageUtil().color("&7Seller: &e" + auction.getSellerName()));
            lore.add(plugin.getMessageUtil().color("&7Price: &a" + plugin.getEconomyManager().format(auction.getPrice())));
            lore.add(plugin.getMessageUtil().color("&7Expires: &f" + formatTime(auction.getExpireTime() - System.currentTimeMillis())));
            lore.add("");
            lore.add(plugin.getMessageUtil().color("&eClick to purchase!"));
            meta.setLore(lore);
            display.setItemMeta(meta);
            inv.setItem(slot++, display);
        }
        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta im = info.getItemMeta();
        im.setDisplayName(plugin.getMessageUtil().color("&6Auction House Info"));
        im.setLore(Arrays.asList(
                plugin.getMessageUtil().color("&7Listings: &e" + listings.size()),
                plugin.getMessageUtil().color("&7Tax: &e" + plugin.getConfig().getDouble("auction-house.tax-percent") + "%"),
                plugin.getMessageUtil().color("&7Hold item + /ah sell <price> to list")
        ));
        info.setItemMeta(im);
        inv.setItem(49, info);

        player.openInventory(inv);
        openInventories.put(player.getUniqueId(), inv);
    }

    public boolean buyItem(Player buyer, int slot) {
        List<AuctionItem> active = new ArrayList<>();
        for (AuctionItem a : listings) {
            if (a.getExpireTime() >= System.currentTimeMillis()) active.add(a);
        }
        if (slot >= active.size()) return false;
        AuctionItem auction = active.get(slot);
        if (auction.getSeller().equals(buyer.getUniqueId())) {
            plugin.getMessageUtil().send(buyer, "&cYou cannot buy your own item!");
            return false;
        }
        if (!plugin.getEconomyManager().has(buyer, auction.getPrice())) {
            plugin.getMessageUtil().send(buyer, plugin.getMessageUtil().get("insufficient-funds"));
            return false;
        }
        double tax = plugin.getConfig().getDouble("auction-house.tax-percent", 5.0) / 100.0;
        double sellerGets = auction.getPrice() * (1.0 - tax);

        plugin.getEconomyManager().removeBalance(buyer, auction.getPrice());
        plugin.getEconomyManager().addBalance(auction.getSeller(), sellerGets);

        HashMap<Integer, ItemStack> leftover = buyer.getInventory().addItem(auction.getItem());
        if (!leftover.isEmpty()) {
            for (ItemStack left : leftover.values()) {
                buyer.getWorld().dropItemNaturally(buyer.getLocation(), left);
            }
        }

        listings.remove(auction);
        save();

        plugin.getMessageUtil().send(buyer, plugin.getMessageUtil().format("ah-bought", "symbol", plugin.getEconomyManager().getCurrencySymbol(), "amount", String.format("%.2f", auction.getPrice())));
        Player seller = Bukkit.getPlayer(auction.getSeller());
        if (seller != null && seller.isOnline()) {
            plugin.getMessageUtil().send(seller, plugin.getMessageUtil().format("ah-sold", "symbol", plugin.getEconomyManager().getCurrencySymbol(), "amount", String.format("%.2f", sellerGets)));
        }
        buyer.closeInventory();
        return true;
    }

    public List<AuctionItem> getListings() {
        return listings;
    }

    public Map<UUID, Inventory> getOpenInventories() {
        return openInventories;
    }

    private String formatTime(long ms) {
        long hours = ms / 3600000;
        long mins = (ms % 3600000) / 60000;
        return hours + "h " + mins + "m";
    }

    public static class AuctionItem {
        private final UUID id;
        private final UUID seller;
        private final String sellerName;
        private final ItemStack item;
        private final double price;
        private final long expireTime;

        public AuctionItem(UUID id, UUID seller, String sellerName, ItemStack item, double price, long expireTime) {
            this.id = id;
            this.seller = seller;
            this.sellerName = sellerName;
            this.item = item;
            this.price = price;
            this.expireTime = expireTime;
        }

        public UUID getId() { return id; }
        public UUID getSeller() { return seller; }
        public String getSellerName() { return sellerName; }
        public ItemStack getItem() { return item; }
        public double getPrice() { return price; }
        public long getExpireTime() { return expireTime; }
    }
}
