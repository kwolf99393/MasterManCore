package com.mastermanmc.core.commands;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaCommand implements CommandExecutor {

    private final MasterManCore plugin;

    public TpaCommand(MasterManCore plugin) {
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

        if (cmd.equals("tpaccept") || cmd.equals("tpyes")) {
            plugin.getTpaManager().accept(player);
            return true;
        }
        if (cmd.equals("tpdeny") || cmd.equals("tpno")) {
            plugin.getTpaManager().deny(player);
            return true;
        }
        if (args.length < 1) {
            plugin.getMessageUtil().send(player, "&cUsage: /tpa <player>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.getMessageUtil().send(player, plugin.getMessageUtil().get("player-not-found"));
            return true;
        }
        if (target.equals(player)) {
            plugin.getMessageUtil().send(player, "&cYou cannot teleport to yourself!");
            return true;
        }
        plugin.getTpaManager().sendRequest(player, target);
        return true;
    }
}
