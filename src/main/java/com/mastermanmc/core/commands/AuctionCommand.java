package com.mastermanmc.core.commands;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AuctionCommand implements CommandExecutor {

    private final MasterManCore plugin;

    public AuctionCommand(MasterManCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageUtil().sendPlayerOnly(sender);
            return true;
        }
        Player player = (Player) sender;
        if (!plugin.getConfig().getBoolean("auction-house.enabled", true)) {
            plugin.getMessageUtil().send(player, "&cAuction House is disabled.");
            return true;
        }
        if (args.length == 0) {
            plugin.getAuctionManager().openAuctionHouse(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("sell")) {
            if (!player.hasPermission("mastermancore.ah.sell")) {
                plugin.getMessageUtil().sendNoPerm(player);
                return true;
            }
            if (args.length < 2) {
                plugin.getMessageUtil().send(player, "&cUsage: /ah sell <price>");
                return true;
            }
            ItemStack hand = player.getInventory().getItemInMainHand();
            if (hand == null || hand.getType() == Material.AIR) {
                plugin.getMessageUtil().send(player, "&cYou must hold an item to sell!");
                return true;
            }
            double price;
            try {
                price = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                plugin.getMessageUtil().send(player, "&cInvalid price!");
                return true;
            }
            plugin.getAuctionManager().createListing(player, hand, price);
            return true;
        }
        plugin.getMessageUtil().send(player, "&cUsage: /ah  or  /ah sell <price>");
        return true;
    }
}
