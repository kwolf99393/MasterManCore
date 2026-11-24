package com.mastermanmc.core.commands;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitCommand implements CommandExecutor {

    private final MasterManCore plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public KitCommand(MasterManCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageUtil().sendPlayerOnly(sender);
            return true;
        }
        Player player = (Player) sender;

        if (label.equalsIgnoreCase("kits")) {
            plugin.getMessageUtil().send(player, "&eAvailable kits: &fstarter, tools, food");
            return true;
        }

        if (args.length < 1) {
            plugin.getMessageUtil().send(player, "&cUsage: /kit <name>");
            return true;
        }

        String kit = args[0].toLowerCase();
        long now = System.currentTimeMillis();
        if (cooldowns.containsKey(player.getUniqueId()) && now - cooldowns.get(player.getUniqueId()) < 300000) {
            long left = (300000 - (now - cooldowns.get(player.getUniqueId()))) / 1000;
            plugin.getMessageUtil().send(player, "&cKit cooldown: " + left + "s remaining.");
            return true;
        }

        switch (kit) {
            case "starter":
                player.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
                player.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
                player.getInventory().addItem(new ItemStack(Material.STONE_AXE));
                player.getInventory().addItem(new ItemStack(Material.BREAD, 16));
                player.getInventory().addItem(new ItemStack(Material.LEATHER_HELMET));
                player.getInventory().addItem(new ItemStack(Material.LEATHER_CHESTPLATE));
                player.getInventory().addItem(new ItemStack(Material.LEATHER_LEGGINGS));
                player.getInventory().addItem(new ItemStack(Material.LEATHER_BOOTS));
                break;
            case "tools":
                player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
                player.getInventory().addItem(new ItemStack(Material.IRON_AXE));
                player.getInventory().addItem(new ItemStack(Material.IRON_SPADE));
                player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
                break;
            case "food":
                player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 32));
                player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 2));
                player.getInventory().addItem(new ItemStack(Material.BREAD, 16));
                break;
            default:
                plugin.getMessageUtil().send(player, "&cUnknown kit! Use /kits");
                return true;
        }
        cooldowns.put(player.getUniqueId(), now);
        plugin.getMessageUtil().send(player, "&aYou claimed the &e" + kit + " &akit!");
        return true;
    }
}
