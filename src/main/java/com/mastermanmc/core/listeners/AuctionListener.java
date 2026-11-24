package com.mastermanmc.core.listeners;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class AuctionListener implements Listener {

    private final MasterManCore plugin;

    public AuctionListener(MasterManCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().contains("Auction House")) {
            event.setCancelled(true);
            if (event.getRawSlot() < 45 && event.getCurrentItem() != null) {
                plugin.getAuctionManager().buyItem(player, event.getRawSlot());
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            plugin.getAuctionManager().getOpenInventories().remove(event.getPlayer().getUniqueId());
        }
    }
}
