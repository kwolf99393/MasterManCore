package com.mastermanmc.core.items;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemManager {

    private final MasterManCore plugin;
    private final Map<String, CustomWeapon> weapons = new HashMap<>();

    public ItemManager(MasterManCore plugin) {
        this.plugin = plugin;
        registerDefaults();
    }

    private void registerDefaults() {
        weapons.put("excalibur", new CustomWeapon("excalibur", "&6&lExcalibur", Material.DIAMOND_SWORD,
                Arrays.asList("&7The legendary sword of kings.", "&e+ Special ability: Lightning Strike"),
                Arrays.asList(new EnchantPair(Enchantment.DAMAGE_ALL, 5), new EnchantPair(Enchantment.FIRE_ASPECT, 2), new EnchantPair(Enchantment.DURABILITY, 3))));
        weapons.put("shadowblade", new CustomWeapon("shadowblade", "&8&lShadow Blade", Material.IRON_SWORD,
                Arrays.asList("&7Strike from the shadows.", "&e+ Poison on hit"),
                Arrays.asList(new EnchantPair(Enchantment.DAMAGE_ALL, 4), new EnchantPair(Enchantment.KNOCKBACK, 1))));
        weapons.put("dragonslayer", new CustomWeapon("dragonslayer", "&c&lDragonslayer", Material.DIAMOND_SWORD,
                Arrays.asList("&7Forged to slay dragons.", "&e+ Extra damage vs bosses"),
                Arrays.asList(new EnchantPair(Enchantment.DAMAGE_ALL, 6), new EnchantPair(Enchantment.FIRE_ASPECT, 3))));
        weapons.put("frostbow", new CustomWeapon("frostbow", "&b&lFrost Bow", Material.BOW,
                Arrays.asList("&7Freezes your enemies.", "&e+ Slowness arrows"),
                Arrays.asList(new EnchantPair(Enchantment.ARROW_DAMAGE, 5), new EnchantPair(Enchantment.ARROW_INFINITE, 1))));
        weapons.put("healstaff", new CustomWeapon("healstaff", "&a&lStaff of Healing", Material.BLAZE_ROD,
                Arrays.asList("&7A mystical healing staff.", "&eRight-click to heal nearby allies"),
                Collections.emptyList()));
    }

    public ItemStack createWeapon(String name) {
        CustomWeapon weapon = weapons.get(name.toLowerCase());
        if (weapon == null) return null;
        ItemStack item = new ItemStack(weapon.getMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', weapon.getDisplayName()));
        List<String> lore = new ArrayList<>();
        for (String line : weapon.getLore()) {
            lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        lore.add("");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&8MasterManMC Custom Weapon"));
        meta.setLore(lore);
        for (EnchantPair ep : weapon.getEnchants()) {
            meta.addEnchant(ep.enchant, ep.level, true);
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    public boolean giveWeapon(Player player, String name) {
        ItemStack weapon = createWeapon(name);
        if (weapon == null) return false;
        HashMap<Integer, ItemStack> left = player.getInventory().addItem(weapon);
        if (!left.isEmpty()) {
            for (ItemStack i : left.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), i);
            }
        }
        return true;
    }

    public Collection<CustomWeapon> getWeapons() {
        return weapons.values();
    }

    public static class CustomWeapon {
        private final String id;
        private final String displayName;
        private final Material material;
        private final List<String> lore;
        private final List<EnchantPair> enchants;

        public CustomWeapon(String id, String displayName, Material material, List<String> lore, List<EnchantPair> enchants) {
            this.id = id;
            this.displayName = displayName;
            this.material = material;
            this.lore = lore;
            this.enchants = enchants;
        }

        public String getId() { return id; }
        public String getDisplayName() { return displayName; }
        public Material getMaterial() { return material; }
        public List<String> getLore() { return lore; }
        public List<EnchantPair> getEnchants() { return enchants; }
    }

    public static class EnchantPair {
        public final Enchantment enchant;
        public final int level;
        public EnchantPair(Enchantment enchant, int level) {
            this.enchant = enchant;
            this.level = level;
        }
    }
}
