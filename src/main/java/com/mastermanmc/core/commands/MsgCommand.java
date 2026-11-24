package com.mastermanmc.core.commands;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MsgCommand implements CommandExecutor {

    private final MasterManCore plugin;
    private static final Map<UUID, UUID> lastMessaged = new HashMap<>();

    public MsgCommand(MasterManCore plugin) {
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

        if (cmd.equals("reply") || cmd.equals("r")) {
            UUID last = lastMessaged.get(player.getUniqueId());
            if (last == null) {
                plugin.getMessageUtil().send(player, "&cYou have no one to reply to.");
                return true;
            }
            Player target = Bukkit.getPlayer(last);
            if (target == null || !target.isOnline()) {
                plugin.getMessageUtil().send(player, "&cThat player is no longer online.");
                return true;
            }
            if (args.length < 1) {
                plugin.getMessageUtil().send(player, "&cUsage: /reply <message>");
                return true;
            }
            String msg = String.join(" ", args);
            sendMsg(player, target, msg);
            return true;
        }
        if (args.length < 2) {
            plugin.getMessageUtil().send(player, "&cUsage: /msg <player> <message>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.getMessageUtil().send(player, plugin.getMessageUtil().get("player-not-found"));
            return true;
        }
        String msg = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        sendMsg(player, target, msg);
        return true;
    }

    private void sendMsg(Player from, Player to, String msg) {
        plugin.getMessageUtil().sendRaw(from, "&7[&eTo " + to.getName() + "&7] &f" + msg);
        plugin.getMessageUtil().sendRaw(to, "&7[&eFrom " + from.getName() + "&7] &f" + msg);
        lastMessaged.put(to.getUniqueId(), from.getUniqueId());
        lastMessaged.put(from.getUniqueId(), to.getUniqueId());
    }
}
