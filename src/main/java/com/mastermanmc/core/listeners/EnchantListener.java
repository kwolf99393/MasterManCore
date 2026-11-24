package com.mastermanmc.core.listeners;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EnchantListener implements Listener {

    private final MasterManCore plugin;

    public EnchantListener(MasterManCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (weapon == null) return;

        int lifesteal = plugin.getEnchantManager().getEnchantLevel(weapon, "lifesteal");
        if (lifesteal > 0 && event.getEntity() instanceof LivingEntity) {
            double heal = event.getFinalDamage() * (0.1 * lifesteal);
            player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + heal));
        }

        int poison = plugin.getEnchantManager().getEnchantLevel(weapon, "poison");
        if (poison > 0 && event.getEntity() instanceof LivingEntity) {
            ((LivingEntity) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40 * poison, poison - 1));
        }

        int thunder = plugin.getEnchantManager().getEnchantLevel(weapon, "thunder");
        if (thunder > 0 && Math.random() < 0.15 * thunder) {
            event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());
            event.setDamage(event.getDamage() + (2 * thunder));
        }
    }
}
