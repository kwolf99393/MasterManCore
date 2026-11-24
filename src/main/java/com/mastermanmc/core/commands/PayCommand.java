package com.mastermanmc.core.commands;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private final MasterManCore plugin;

    public PayCommand(MasterManCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageUtil().sendPlayerOnly(sender);
            return true;
        }
        if (args.length < 2) {
            plugin.getMessageUtil().send(sender, "&cUsage: /pay <player> <amount>");
            return true;
        }
        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.getMessageUtil().send(player, plugin.getMessageUtil().get("player-not-found"));
            return true;
        }
        if (target.equals(player)) {
            plugin.getMessageUtil().send(player, "&cYou cannot pay yourself!");
            return true;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            plugin.getMessageUtil().send(player, "&cInvalid amount!");
            return true;
        }
        double min = plugin.getConfig().getDouble("economy.min-pay", 1.0);
        if (amount < min) {
            plugin.getMessageUtil().send(player, "&cMinimum payment is " + plugin.getEconomyManager().format(min));
            return true;
        }
        if (!plugin.getEconomyManager().removeBalance(player, amount)) {
            plugin.getMessageUtil().send(player, plugin.getMessageUtil().get("insufficient-funds"));
            return true;
        }
        plugin.getEconomyManager().addBalance(target, amount);
        plugin.getMessageUtil().send(player, plugin.getMessageUtil().format("paid",
                "symbol", plugin.getEconomyManager().getCurrencySymbol(),
                "amount", String.format("%.2f", amount),
                "player", target.getName()));
        plugin.getMessageUtil().send(target, plugin.getMessageUtil().format("received",
                "symbol", plugin.getEconomyManager().getCurrencySymbol(),
                "amount", String.format("%.2f", amount),
                "player", player.getName()));
        return true;
    }
}
