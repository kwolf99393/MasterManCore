package com.mastermanmc.core.commands;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand implements CommandExecutor {

    private final MasterManCore plugin;

    public BalanceCommand(MasterManCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                plugin.getMessageUtil().sendPlayerOnly(sender);
                return true;
            }
            Player player = (Player) sender;
            double bal = plugin.getEconomyManager().getBalance(player);
            plugin.getMessageUtil().send(player, plugin.getMessageUtil().format("balance",
                    "symbol", plugin.getEconomyManager().getCurrencySymbol(),
                    "balance", String.format("%,.2f", bal)));
            return true;
        }
        if (!sender.hasPermission("mastermancore.eco.admin")) {
            plugin.getMessageUtil().sendNoPerm(sender);
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            plugin.getMessageUtil().send(sender, plugin.getMessageUtil().get("player-not-found"));
            return true;
        }
        double bal = plugin.getEconomyManager().getBalance(target);
        plugin.getMessageUtil().send(sender, plugin.getMessageUtil().format("balance-other",
                "player", target.getName(),
                "symbol", plugin.getEconomyManager().getCurrencySymbol(),
                "balance", String.format("%,.2f", bal)));
        return true;
    }
}
