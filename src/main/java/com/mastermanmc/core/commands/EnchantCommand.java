package com.mastermanmc.core.commands;

import com.mastermanmc.core.MasterManCore;
import com.mastermanmc.core.enchants.EnchantManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantCommand implements CommandExecutor {

    private final MasterManCore plugin;

    public EnchantCommand(MasterManCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            plugin.getMessageUtil().send(sender, "&eCustom Enchants:");
            for (EnchantManager.CustomEnchant ce : plugin.getEnchantManager().getEnchants()) {
                plugin.getMessageUtil().sendRaw(sender, " &7- &f" + ce.getId() + " &8(" + ce.getDisplayName() + "&8) &7Max: " + ce.getMaxLevel());
            }
            return true;
        }
        if (!(sender instanceof Player)) {
            plugin.getMessageUtil().sendPlayerOnly(sender);
            return true;
        }
        Player player = (Player) sender;
        if (args[0].equalsIgnoreCase("apply")) {
            if (!player.hasPermission("mastermancore.admin")) {
                plugin.getMessageUtil().sendNoPerm(player);
                return true;
            }
            if (args.length < 3) {
                plugin.getMessageUtil().send(player, "&cUsage: /ce apply <enchant> <level>");
                return true;
            }
            ItemStack hand = player.getInventory().getItemInMainHand();
            if (hand == null || hand.getType() == Material.AIR) {
                plugin.getMessageUtil().send(player, "&cHold an item!");
                return true;
            }
            int level;
            try {
                level = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                plugin.getMessageUtil().send(player, "&cInvalid level!");
                return true;
            }
            if (plugin.getEnchantManager().applyEnchant(hand, args[1], level)) {
                plugin.getMessageUtil().send(player, "&aApplied " + args[1] + " " + level + "!");
            } else {
                plugin.getMessageUtil().send(player, "&cFailed to apply enchant. Check item type and level.");
            }
            return true;
        }
        plugin.getMessageUtil().send(sender, "&cUsage: /ce list | /ce apply <enchant> <level>");
        return true;
    }
}
