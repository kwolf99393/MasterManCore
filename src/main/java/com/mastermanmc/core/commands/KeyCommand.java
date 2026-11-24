package com.mastermanmc.core.commands;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KeyCommand implements CommandExecutor {

    private final MasterManCore plugin;

    public KeyCommand(MasterManCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mastermancore.crate.admin")) {
            plugin.getMessageUtil().sendNoPerm(sender);
            return true;
        }
        if (args.length < 2) {
            plugin.getMessageUtil().send(sender, "&cUsage: /key <player> <crate> [amount]");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.getMessageUtil().send(sender, plugin.getMessageUtil().get("player-not-found"));
            return true;
        }
        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                plugin.getMessageUtil().send(sender, "&cInvalid amount!");
                return true;
            }
        }
        if (plugin.getCrateManager().getCrate(args[1]) == null) {
            plugin.getMessageUtil().send(sender, "&cUnknown crate! Use /crate list");
            return true;
        }
        plugin.getCrateManager().giveKey(target, args[1], amount);
        plugin.getMessageUtil().send(sender, "&aGave " + amount + "x " + args[1] + " key(s) to " + target.getName());
        return true;
    }
}
