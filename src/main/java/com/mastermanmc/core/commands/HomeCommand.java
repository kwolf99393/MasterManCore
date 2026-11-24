package com.mastermanmc.core.commands;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class HomeCommand implements CommandExecutor {

    private final MasterManCore plugin;

    public HomeCommand(MasterManCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            plugin.getMessageUtil().sendPlayerOnly(sender);
            return true;
        }
        Player player = (Player) sender;
        String cmd = label.toLowerCase();

        if (cmd.equals("sethome")) {
            String name = args.length > 0 ? args[0] : "home";
            plugin.getHomeManager().setHome(player, name);
            return true;
        }
        if (cmd.equals("delhome")) {
            if (args.length < 1) {
                plugin.getMessageUtil().send(player, "&cUsage: /delhome <name>");
                return true;
            }
            plugin.getHomeManager().deleteHome(player, args[0]);
            return true;
        }
        if (cmd.equals("homes")) {
            Set<String> homes = plugin.getHomeManager().getHomes(player);
            if (homes.isEmpty()) {
                plugin.getMessageUtil().send(player, "&cYou have no homes set.");
            } else {
                plugin.getMessageUtil().send(player, "&eYour homes: &f" + String.join(", ", homes));
            }
            return true;
        }
        String name = args.length > 0 ? args[0] : "home";
        plugin.getHomeManager().teleportHome(player, name);
        return true;
    }
}
