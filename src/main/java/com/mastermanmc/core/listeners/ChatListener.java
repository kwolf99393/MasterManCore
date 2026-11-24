package com.mastermanmc.core.listeners;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final MasterManCore plugin;

    public ChatListener(MasterManCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String format = "&7[%1$s&7] &f%2$s";
        event.setFormat(plugin.getMessageUtil().color(format));
    }
}
