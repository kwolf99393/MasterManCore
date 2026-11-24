package com.mastermanmc.core.managers;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaManager {

    private final MasterManCore plugin;
    private final Map<UUID, UUID> requests = new HashMap<>(); // target -> requester

    public TpaManager(MasterManCore plugin) {
        this.plugin = plugin;
    }

    public void sendRequest(Player from, Player to) {
        requests.put(to.getUniqueId(), from.getUniqueId());
        plugin.getMessageUtil().send(from, plugin.getMessageUtil().format("tpa-sent", "player", to.getName()));
        plugin.getMessageUtil().send(to, plugin.getMessageUtil().format("tpa-received", "player", from.getName()));

        int timeout = plugin.getConfig().getInt("tpa.timeout-seconds", 60);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (requests.containsKey(to.getUniqueId()) && requests.get(to.getUniqueId()).equals(from.getUniqueId())) {
                    requests.remove(to.getUniqueId());
                    if (from.isOnline()) {
                        plugin.getMessageUtil().send(from, plugin.getMessageUtil().get("tpa-expired"));
                    }
                    if (to.isOnline()) {
                        plugin.getMessageUtil().send(to, plugin.getMessageUtil().get("tpa-expired"));
                    }
                }
            }
        }.runTaskLater(plugin, timeout * 20L);
    }

    public boolean accept(Player target) {
        UUID requesterId = requests.remove(target.getUniqueId());
        if (requesterId == null) {
            plugin.getMessageUtil().send(target, "&cYou have no pending teleport requests.");
            return false;
        }
        Player requester = Bukkit.getPlayer(requesterId);
        if (requester == null || !requester.isOnline()) {
            plugin.getMessageUtil().send(target, "&cThe player is no longer online.");
            return false;
        }
        requester.teleport(target.getLocation());
        plugin.getMessageUtil().send(target, plugin.getMessageUtil().get("tpa-accepted"));
        plugin.getMessageUtil().send(requester, plugin.getMessageUtil().get("tpa-accepted"));
        return true;
    }

    public boolean deny(Player target) {
        UUID requesterId = requests.remove(target.getUniqueId());
        if (requesterId == null) {
            plugin.getMessageUtil().send(target, "&cYou have no pending teleport requests.");
            return false;
        }
        Player requester = Bukkit.getPlayer(requesterId);
        plugin.getMessageUtil().send(target, plugin.getMessageUtil().get("tpa-denied"));
        if (requester != null && requester.isOnline()) {
            plugin.getMessageUtil().send(requester, plugin.getMessageUtil().get("tpa-denied"));
        }
        return true;
    }
}
