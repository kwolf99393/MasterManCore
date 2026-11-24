package com.mastermanmc.core.commands;

import com.mastermanmc.core.MasterManCore;
import com.mastermanmc.core.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveWeaponCommand implements CommandExecutor {

    private final MasterManCore plugin;

    public GiveWeaponCommand(MasterManCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mastermancore.weapon.admin")) {
            plugin.getMessageUtil().sendNoPerm(sender);
            return true;
        }
        if (args.length < 2) {
            plugin.getMessageUtil().send(sender, "&cUsage: /giveweapon <player> <weapon>");
            plugin.getMessageUtil().send(sender, "&7Available: ");
            for (ItemManager.CustomWeapon w : plugin.getItemManager().getWeapons()) {
                plugin.getMessageUtil().sendRaw(sender, "  &e" + w.getId());
            }
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.getMessageUtil().send(sender, plugin.getMessageUtil().get("player-not-found"));
            return true;
        }
        if (plugin.getItemManager().giveWeapon(target, args[1])) {
            plugin.getMessageUtil().send(sender, "&aGave " + args[1] + " to " + target.getName());
            plugin.getMessageUtil().send(target, "&aYou received a custom weapon: &e" + args[1]);
        } else {
            plugin.getMessageUtil().send(sender, "&cUnknown weapon!");
        }
        return true;
    }
}
