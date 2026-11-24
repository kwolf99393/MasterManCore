package com.mastermanmc.core.commands;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {

    private final MasterManCore plugin;

    public WarpCommand(MasterManCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = label.toLowerCase();

        if (cmd.equals("setwarp")) {
            if (!sender.hasPermission("mastermancore.admin")) {
                plugin.getMessageUtil().sendNoPerm(sender);
                return true;
            }
            if (!(sender instanceof Player)) {
                plugin.getMessageUtil().sendPlayerOnly(sender);
                return true;
            }
            if (args.length < 1) {
                plugin.getMessageUtil().send(sender, "&cUsage: /setwarp <name>");
                return true;
            }
            plugin.getWarpManager().setWarp(args[0], ((Player) sender).getLocation());
            plugin.getMessageUtil().send(sender, plugin.getMessageUtil().format("warp-set", "name", args[0]));
            return true;
        }
        if (cmd.equals("delwarp")) {
            if (!sender.hasPermission("mastermancore.admin")) {
                plugin.getMessageUtil().sendNoPerm(sender);
                return true;
            }
            if (args.length < 1) {
                plugin.getMessageUtil().send(sender, "&cUsage: /delwarp <name>");
                return true;
            }
            if (plugin.getWarpManager().deleteWarp(args[0])) {
                plugin.getMessageUtil().send(sender, "&aWarp deleted.");
            } else {
                plugin.getMessageUtil().send(sender, "&cWarp not found.");
            }
            return true;
        }
        if (cmd.equals("warps")) {
            plugin.getMessageUtil().send(sender, "&eWarps: &f" + String.join(", ", plugin.getWarpManager().getWarps()));
            return true;
        }
        if (!(sender instanceof Player)) {
            plugin.getMessageUtil().sendPlayerOnly(sender);
            return true;
        }
        if (args.length < 1) {
            plugin.getMessageUtil().send(sender, "&cUsage: /warp <name>");
            return true;
        }
        plugin.getWarpManager().teleport((Player) sender, args[0]);
        return true;
    }
}
