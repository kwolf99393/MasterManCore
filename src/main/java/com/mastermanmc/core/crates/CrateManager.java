package com.mastermanmc.core.crates;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CrateManager {

    private final MasterManCore plugin;
    private final Map<String, Crate> crates = new HashMap<>();
    private final Map<UUID, Map<String, Integer>> keys = new HashMap<>();
    private File dataFile;
    private FileConfiguration data;
    private File cratesFile;
    private FileConfiguration cratesConfig;

    public CrateManager(MasterManCore plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "crate-data.yml");
        this.cratesFile = new File(plugin.getDataFolder(), "crates.yml");
    }

    public void load() {
        if (!cratesFile.exists()) {
            plugin.saveResource("crates.yml", false);
        }
        cratesConfig = YamlConfiguration.loadConfiguration(cratesFile);
        crates.clear();
        ConfigurationSection section = cratesConfig.getConfigurationSection("crates");
        if (section != null) {
            for (String name : section.getKeys(false)) {
                String path = "crates." + name;
                String display = cratesConfig.getString(path + ".display-name", name);
                Material mat = Material.matchMaterial(cratesConfig.getString(path + ".material", "CHEST"));
                if (mat == null) mat = Material.CHEST;
                List<CrateReward> rewards = new ArrayList<>();
                if (cratesConfig.getConfigurationSection(path + ".rewards") != null) {
                    for (String rKey : cratesConfig.getConfigurationSection(path + ".rewards").getKeys(false)) {
                        String rPath = path + ".rewards." + rKey;
                        ItemStack item = cratesConfig.getItemStack(rPath + ".item");
                        if (item == null) {
                            Material rm = Material.matchMaterial(cratesConfig.getString(rPath + ".material", "DIAMOND"));
                            if (rm == null) rm = Material.DIAMOND;
                            item = new ItemStack(rm, cratesConfig.getInt(rPath + ".amount", 1));
                        }
                        double chance = cratesConfig.getDouble(rPath + ".chance", 10.0);
                        rewards.add(new CrateReward(item, chance));
                    }
                }
                crates.put(name.toLowerCase(), new Crate(name, display, mat, rewards));
            }
        }
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
        keys.clear();
        if (data.getConfigurationSection("keys") != null) {
            for (String uuidStr : data.getConfigurationSection("keys").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    Map<String, Integer> playerKeys = new HashMap<>();
                    for (String crate : data.getConfigurationSection("keys." + uuidStr).getKeys(false)) {
                        playerKeys.put(crate, data.getInt("keys." + uuidStr + "." + crate));
                    }
                    keys.put(uuid, playerKeys);
                } catch (Exception ignored) {}
            }
        }
        plugin.getLogger().info("Loaded " + crates.size() + " crates.");
    }

    public void save() {
        if (data == null) return;
        data.set("keys", null);
        for (Map.Entry<UUID, Map<String, Integer>> entry : keys.entrySet()) {
            for (Map.Entry<String, Integer> k : entry.getValue().entrySet()) {
                data.set("keys." + entry.getKey() + "." + k.getKey(), k.getValue());
            }
        }
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void giveKey(Player player, String crateName, int amount) {
        crateName = crateName.toLowerCase();
        if (!crates.containsKey(crateName)) return;
        keys.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        Map<String, Integer> pk = keys.get(player.getUniqueId());
        pk.put(crateName, pk.getOrDefault(crateName, 0) + amount);
        plugin.getMessageUtil().send(player, "&aYou received &e" + amount + "x " + crateName + " &akey(s)!");
        save();
    }

    public int getKeys(Player player, String crateName) {
        Map<String, Integer> pk = keys.get(player.getUniqueId());
        if (pk == null) return 0;
        return pk.getOrDefault(crateName.toLowerCase(), 0);
    }

    public boolean openCrate(Player player, String crateName) {
        crateName = crateName.toLowerCase();
        Crate crate = crates.get(crateName);
        if (crate == null) {
            plugin.getMessageUtil().send(player, "&cUnknown crate!");
            return false;
        }
        if (getKeys(player, crateName) < 1) {
            plugin.getMessageUtil().send(player, "&cYou need a key to open this crate!");
            return false;
        }
        Map<String, Integer> pk = keys.get(player.getUniqueId());
        pk.put(crateName, pk.get(crateName) - 1);
        save();

        CrateReward reward = roll(crate);
        if (reward != null) {
            HashMap<Integer, ItemStack> left = player.getInventory().addItem(reward.getItem().clone());
            if (!left.isEmpty()) {
                for (ItemStack i : left.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), i);
                }
            }
            plugin.getMessageUtil().send(player, "&aYou won: &e" + reward.getItem().getType().name() + " x" + reward.getItem().getAmount());
        } else {
            plugin.getMessageUtil().send(player, "&cNo reward this time... better luck next time!");
        }
        return true;
    }

    private CrateReward roll(Crate crate) {
        double total = crate.getRewards().stream().mapToDouble(CrateReward::getChance).sum();
        double r = Math.random() * total;
        double cumulative = 0;
        for (CrateReward reward : crate.getRewards()) {
            cumulative += reward.getChance();
            if (r <= cumulative) return reward;
        }
        return crate.getRewards().isEmpty() ? null : crate.getRewards().get(0);
    }

    public Collection<Crate> getCrates() {
        return crates.values();
    }

    public Crate getCrate(String name) {
        return crates.get(name.toLowerCase());
    }

    public static class Crate {
        private final String name;
        private final String displayName;
        private final Material material;
        private final List<CrateReward> rewards;

        public Crate(String name, String displayName, Material material, List<CrateReward> rewards) {
            this.name = name;
            this.displayName = displayName;
            this.material = material;
            this.rewards = rewards;
        }

        public String getName() { return name; }
        public String getDisplayName() { return displayName; }
        public Material getMaterial() { return material; }
        public List<CrateReward> getRewards() { return rewards; }
    }

    public static class CrateReward {
        private final ItemStack item;
        private final double chance;

        public CrateReward(ItemStack item, double chance) {
            this.item = item;
            this.chance = chance;
        }

        public ItemStack getItem() { return item; }
        public double getChance() { return chance; }
    }
}
