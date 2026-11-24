package com.mastermanmc.core.commands;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommand implements CommandExecutor {

    private final MasterManCore plugin;

    public PlayerCommand(MasterManCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = label.toLowerCase();
        Player target = null;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                plugin.getMessageUtil().send(sender, plugin.getMessageUtil().get("player-not-found"));
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            plugin.getMessageUtil().send(sender, "&cSpecify a player!");
            return true;
        }

        switch (cmd) {
            case "heal":
                if (!sender.hasPermission("mastermancore.heal")) {
                    plugin.getMessageUtil().sendNoPerm(sender);
                    return true;
                }
                target.setHealth(target.getMaxHealth());
                target.setFoodLevel(20);
                target.setFireTicks(0);
                plugin.getMessageUtil().send(sender, "&aHealed " + target.getName());
                if (!target.equals(sender)) plugin.getMessageUtil().send(target, "&aYou have been healed!");
                break;
            case "feed":
                if (!sender.hasPermission("mastermancore.feed")) {
                    plugin.getMessageUtil().sendNoPerm(sender);
                    return true;
                }
                target.setFoodLevel(20);
                target.setSaturation(20f);
                plugin.getMessageUtil().send(sender, "&aFed " + target.getName());
                break;
            case "fly":
                if (!sender.hasPermission("mastermancore.fly")) {
                    plugin.getMessageUtil().sendNoPerm(sender);
                    return true;
                }
                boolean flying = !target.getAllowFlight();
                target.setAllowFlight(flying);
                target.setFlying(flying);
                plugin.getMessageUtil().send(sender, "&aFlight " + (flying ? "enabled" : "disabled") + " for " + target.getName());
                break;
            case "gamemode":
            case "gm":
                if (!sender.hasPermission("mastermancore.gamemode")) {
                    plugin.getMessageUtil().sendNoPerm(sender);
                    return true;
                }
                if (args.length < 1) {
                    plugin.getMessageUtil().send(sender, "&cUsage: /gm <0|1|2|3> [player]");
                    return true;
                }
                GameMode mode;
                try {
                    int id = Integer.parseInt(args[0]);
                    mode = GameMode.getByValue(id);
                } catch (Exception e) {
                    try {
                        mode = GameMode.valueOf(args[0].toUpperCase());
                    } catch (Exception e2) {
                        plugin.getMessageUtil().send(sender, "&cInvalid gamemode!");
                        return true;
                    }
                }
                if (args.length > 1) {
                    target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        plugin.getMessageUtil().send(sender, plugin.getMessageUtil().get("player-not-found"));
                        return true;
                    }
                } else if (!(sender instanceof Player)) {
                    plugin.getMessageUtil().send(sender, "&cSpecify a player!");
                    return true;
                } else {
                    target = (Player) sender;
                }
                target.setGameMode(mode);
                plugin.getMessageUtil().send(sender, "&aSet " + target.getName() + "'s gamemode to " + mode.name());
                break;
            case "invsee":
                if (!sender.hasPermission("mastermancore.invsee")) {
                    plugin.getMessageUtil().sendNoPerm(sender);
                    return true;
                }
                if (!(sender instanceof Player)) {
                    plugin.getMessageUtil().sendPlayerOnly(sender);
                    return true;
                }
                if (args.length < 1) {
                    plugin.getMessageUtil().send(sender, "&cUsage: /invsee <player>");
                    return true;
                }
                target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    plugin.getMessageUtil().send(sender, plugin.getMessageUtil().get("player-not-found"));
                    return true;
                }
                ((Player) sender).openInventory(target.getInventory());
                break;
            case "clear":
                if (!sender.hasPermission("mastermancore.clear")) {
                    plugin.getMessageUtil().sendNoPerm(sender);
                    return true;
                }
                target.getInventory().clear();
                plugin.getMessageUtil().send(sender, "&aCleared inventory of " + target.getName());
                break;
        }
        return true;
    }
}
