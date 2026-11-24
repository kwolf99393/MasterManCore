package com.mastermanmc.core.utils;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtil {

    private final MasterManCore plugin;
    private String prefix;

    public MessageUtil(MasterManCore plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        prefix = color(plugin.getConfig().getString("prefix", "&8[&6MasterMan&eMC&8] &r"));
    }

    public String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public void send(CommandSender sender, String message) {
        sender.sendMessage(prefix + color(message));
    }

    public void sendRaw(CommandSender sender, String message) {
        sender.sendMessage(color(message));
    }

    public void sendNoPerm(CommandSender sender) {
        send(sender, plugin.getConfig().getString("messages.no-permission", "&cYou don't have permission!"));
    }

    public void sendPlayerOnly(CommandSender sender) {
        send(sender, plugin.getConfig().getString("messages.player-only", "&cPlayers only!"));
    }

    public String get(String path) {
        return plugin.getConfig().getString("messages." + path, path);
    }

    public String format(String path, String... replacements) {
        String msg = get(path);
        for (int i = 0; i < replacements.length - 1; i += 2) {
            msg = msg.replace("{" + replacements[i] + "}", replacements[i + 1]);
        }
        return msg;
    }
}
