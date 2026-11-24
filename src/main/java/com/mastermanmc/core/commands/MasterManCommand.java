package com.mastermanmc.core.commands;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MasterManCommand implements CommandExecutor {

    private final MasterManCore plugin;

    public MasterManCommand(MasterManCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("mastermancore.admin")) {
                plugin.getMessageUtil().sendNoPerm(sender);
                return true;
            }
            plugin.reload();
            plugin.getMessageUtil().send(sender, "&aMasterManCore reloaded!");
            return true;
        }
        plugin.getMessageUtil().send(sender, "&6&lMasterManCore &ev" + plugin.getDescription().getVersion());
        plugin.getMessageUtil().sendRaw(sender, "&7Complete core plugin for &eMasterManMC");
        plugin.getMessageUtil().sendRaw(sender, "&7Features: Economy, Auction House, Crates,");
        plugin.getMessageUtil().sendRaw(sender, "&7Custom Enchants, Weapons, Homes, Warps, TPA, Kits & more");
        plugin.getMessageUtil().sendRaw(sender, "&7/masterman reload &8- Reload config");
        return true;
    }
}
