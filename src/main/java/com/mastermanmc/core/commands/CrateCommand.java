package com.mastermanmc.core.commands;

import com.mastermanmc.core.MasterManCore;
import com.mastermanmc.core.crates.CrateManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrateCommand implements CommandExecutor {

    private final MasterManCore plugin;

    public CrateCommand(MasterManCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            plugin.getMessageUtil().send(sender, "&eAvailable crates:");
            for (CrateManager.Crate c : plugin.getCrateManager().getCrates()) {
                plugin.getMessageUtil().sendRaw(sender, " &7- &f" + c.getName() + " &8(" + c.getDisplayName() + "&8)");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("list")) {
            plugin.getMessageUtil().send(sender, "&eAvailable crates:");
            for (CrateManager.Crate c : plugin.getCrateManager().getCrates()) {
                plugin.getMessageUtil().sendRaw(sender, " &7- &f" + c.getName());
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("open") && sender instanceof Player) {
            if (args.length < 2) {
                plugin.getMessageUtil().send(sender, "&cUsage: /crate open <name>");
                return true;
            }
            plugin.getCrateManager().openCrate((Player) sender, args[1]);
            return true;
        }
        if (!sender.hasPermission("mastermancore.crate.admin")) {
            plugin.getMessageUtil().sendNoPerm(sender);
            return true;
        }
        plugin.getMessageUtil().send(sender, "&cUsage: /crate list | /crate open <name>");
        return true;
    }
}
