package com.mastermanmc.core.enchants;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EnchantManager {

    private final MasterManCore plugin;
    private final Map<String, CustomEnchant> enchants = new HashMap<>();

    public EnchantManager(MasterManCore plugin) {
        this.plugin = plugin;
        registerDefaults();
    }

    private void registerDefaults() {
        enchants.put("lifesteal", new CustomEnchant("lifesteal", "&cLife Steal", Arrays.asList(Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.STONE_SWORD, Material.WOOD_SWORD), 3));
        enchants.put("thunder", new CustomEnchant("thunder", "&bThunder", Arrays.asList(Material.DIAMOND_SWORD, Material.IRON_SWORD), 2));
        enchants.put("speed", new CustomEnchant("speed", "&aSpeed Boost", Arrays.asList(Material.DIAMOND_BOOTS, Material.IRON_BOOTS, Material.GOLD_BOOTS, Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS), 3));
        enchants.put("poison", new CustomEnchant("poison", "&2Poison", Arrays.asList(Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.BOW), 2));
        enchants.put("explosive", new CustomEnchant("explosive", "&6Explosive", Arrays.asList(Material.BOW, Material.DIAMOND_SWORD), 2));
        enchants.put("autosell", new CustomEnchant("autosell", "&eAuto Sell", Arrays.asList(Material.DIAMOND_PICKAXE, Material.IRON_PICKAXE), 1));
        enchants.put("haste", new CustomEnchant("haste", "&eHaste", Arrays.asList(Material.DIAMOND_PICKAXE, Material.IRON_PICKAXE, Material.DIAMOND_AXE), 3));
    }

    public boolean applyEnchant(ItemStack item, String enchantName, int level) {
        CustomEnchant ce = enchants.get(enchantName.toLowerCase());
        if (ce == null) return false;
        if (!ce.getApplicable().contains(item.getType())) return false;
        if (level < 1 || level > ce.getMaxLevel()) return false;

        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
        // Remove existing
        lore.removeIf(line -> ChatColor.stripColor(line).toLowerCase().startsWith(ChatColor.stripColor(ce.getDisplayName()).toLowerCase()));
        lore.add(0, ChatColor.translateAlternateColorCodes('&', ce.getDisplayName() + " " + toRoman(level)));
        meta.setLore(lore);
        // Glow
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        item.setItemMeta(meta);
        return true;
    }

    public int getEnchantLevel(ItemStack item, String enchantName) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) return 0;
        CustomEnchant ce = enchants.get(enchantName.toLowerCase());
        if (ce == null) return 0;
        String search = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', ce.getDisplayName())).toLowerCase();
        for (String line : item.getItemMeta().getLore()) {
            String stripped = ChatColor.stripColor(line).toLowerCase();
            if (stripped.startsWith(search)) {
                String[] parts = stripped.split(" ");
                if (parts.length > 0) {
                    return fromRoman(parts[parts.length - 1]);
                }
            }
        }
        return 0;
    }

    public Collection<CustomEnchant> getEnchants() {
        return enchants.values();
    }

    public CustomEnchant getEnchant(String name) {
        return enchants.get(name.toLowerCase());
    }

    private String toRoman(int num) {
        String[] romans = {"", "I", "II", "III", "IV", "V"};
        return num >= 0 && num < romans.length ? romans[num] : String.valueOf(num);
    }

    private int fromRoman(String roman) {
        switch (roman.toUpperCase()) {
            case "I": return 1;
            case "II": return 2;
            case "III": return 3;
            case "IV": return 4;
            case "V": return 5;
            default:
                try { return Integer.parseInt(roman); } catch (Exception e) { return 0; }
        }
    }

    public static class CustomEnchant {
        private final String id;
        private final String displayName;
        private final List<Material> applicable;
        private final int maxLevel;

        public CustomEnchant(String id, String displayName, List<Material> applicable, int maxLevel) {
            this.id = id;
            this.displayName = displayName;
            this.applicable = applicable;
            this.maxLevel = maxLevel;
        }

        public String getId() { return id; }
        public String getDisplayName() { return displayName; }
        public List<Material> getApplicable() { return applicable; }
        public int getMaxLevel() { return maxLevel; }
    }
}
