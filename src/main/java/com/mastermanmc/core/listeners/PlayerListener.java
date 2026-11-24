package com.mastermanmc.core.listeners;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final MasterManCore plugin;

    public PlayerListener(MasterManCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getEconomyManager().ensureAccount(event.getPlayer());
        event.setJoinMessage(plugin.getMessageUtil().color("&8[&a+&8] &e" + event.getPlayer().getName() + " &7joined the server."));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(plugin.getMessageUtil().color("&8[&c-&8] &e" + event.getPlayer().getName() + " &7left the server."));
    }
}
