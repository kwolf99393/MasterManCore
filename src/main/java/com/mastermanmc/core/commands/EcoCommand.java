package com.mastermanmc.core.commands;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EcoCommand implements CommandExecutor {

    private final MasterManCore plugin;

    public EcoCommand(MasterManCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mastermancore.eco.admin")) {
            plugin.getMessageUtil().sendNoPerm(sender);
            return true;
        }
        if (args.length < 2) {
            plugin.getMessageUtil().send(sender, "&cUsage: /eco <give|take|set|reset> <player> [amount]");
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            plugin.getMessageUtil().send(sender, plugin.getMessageUtil().get("player-not-found"));
            return true;
        }
        String action = args[0].toLowerCase();
        if (action.equals("reset")) {
            plugin.getEconomyManager().setBalance(target, plugin.getConfig().getDouble("economy.starting-balance", 1000));
            plugin.getMessageUtil().send(sender, "&aReset " + target.getName() + "'s balance.");
            return true;
        }
        if (args.length < 3) {
            plugin.getMessageUtil().send(sender, "&cUsage: /eco <give|take|set> <player> <amount>");
            return true;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            plugin.getMessageUtil().send(sender, "&cInvalid amount!");
            return true;
        }
        switch (action) {
            case "give":
                plugin.getEconomyManager().addBalance(target, amount);
                plugin.getMessageUtil().send(sender, "&aGave " + plugin.getEconomyManager().format(amount) + " to " + target.getName());
                break;
            case "take":
                plugin.getEconomyManager().removeBalance(target, amount);
                plugin.getMessageUtil().send(sender, "&aTook " + plugin.getEconomyManager().format(amount) + " from " + target.getName());
                break;
            case "set":
                plugin.getEconomyManager().setBalance(target, amount);
                plugin.getMessageUtil().send(sender, "&aSet " + target.getName() + "'s balance to " + plugin.getEconomyManager().format(amount));
                break;
            default:
                plugin.getMessageUtil().send(sender, "&cUnknown action. Use give/take/set/reset");
        }
        return true;
    }
}
