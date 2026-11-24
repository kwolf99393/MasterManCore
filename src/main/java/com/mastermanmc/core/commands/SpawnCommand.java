package com.mastermanmc.core.commands;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private final MasterManCore plugin;

    public SpawnCommand(MasterManCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("setspawn")) {
            if (!sender.hasPermission("mastermancore.admin")) {
                plugin.getMessageUtil().sendNoPerm(sender);
                return true;
            }
            if (!(sender instanceof Player)) {
                plugin.getMessageUtil().sendPlayerOnly(sender);
                return true;
            }
            Player player = (Player) sender;
            Location loc = player.getLocation();
            plugin.getConfig().set("spawn.world", loc.getWorld().getName());
            plugin.getConfig().set("spawn.x", loc.getX());
            plugin.getConfig().set("spawn.y", loc.getY());
            plugin.getConfig().set("spawn.z", loc.getZ());
            plugin.getConfig().set("spawn.yaw", loc.getYaw());
            plugin.getConfig().set("spawn.pitch", loc.getPitch());
            plugin.saveConfig();
            plugin.getMessageUtil().send(player, "&aSpawn set!");
            return true;
        }
        if (!(sender instanceof Player)) {
            plugin.getMessageUtil().sendPlayerOnly(sender);
            return true;
        }
        Player player = (Player) sender;
        if (plugin.getConfig().contains("spawn.world")) {
            org.bukkit.World world = org.bukkit.Bukkit.getWorld(plugin.getConfig().getString("spawn.world"));
            if (world != null) {
                Location spawn = new Location(world,
                        plugin.getConfig().getDouble("spawn.x"),
                        plugin.getConfig().getDouble("spawn.y"),
                        plugin.getConfig().getDouble("spawn.z"),
                        (float) plugin.getConfig().getDouble("spawn.yaw"),
                        (float) plugin.getConfig().getDouble("spawn.pitch"));
                player.teleport(spawn);
                plugin.getMessageUtil().send(player, plugin.getMessageUtil().get("teleported"));
                return true;
            }
        }
        player.teleport(player.getWorld().getSpawnLocation());
        plugin.getMessageUtil().send(player, plugin.getMessageUtil().get("teleported"));
        return true;
    }
}
