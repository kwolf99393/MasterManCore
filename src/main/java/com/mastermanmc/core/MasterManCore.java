package com.mastermanmc.core;

import com.mastermanmc.core.auction.AuctionManager;
import com.mastermanmc.core.commands.*;
import com.mastermanmc.core.crates.CrateManager;
import com.mastermanmc.core.economy.EconomyManager;
import com.mastermanmc.core.enchants.EnchantManager;
import com.mastermanmc.core.items.ItemManager;
import com.mastermanmc.core.listeners.*;
import com.mastermanmc.core.managers.HomeManager;
import com.mastermanmc.core.managers.WarpManager;
import com.mastermanmc.core.managers.TpaManager;
import com.mastermanmc.core.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MasterManCore extends JavaPlugin {

    private static MasterManCore instance;

    private EconomyManager economyManager;
    private AuctionManager auctionManager;
    private CrateManager crateManager;
    private EnchantManager enchantManager;
    private ItemManager itemManager;
    private HomeManager homeManager;
    private WarpManager warpManager;
    private TpaManager tpaManager;
    private MessageUtil messageUtil;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        messageUtil = new MessageUtil(this);
        economyManager = new EconomyManager(this);
        auctionManager = new AuctionManager(this);
        crateManager = new CrateManager(this);
        enchantManager = new EnchantManager(this);
        itemManager = new ItemManager(this);
        homeManager = new HomeManager(this);
        warpManager = new WarpManager(this);
        tpaManager = new TpaManager(this);

        registerCommands();
        registerListeners();

        economyManager.load();
        auctionManager.load();
        crateManager.load();
        homeManager.load();
        warpManager.load();

        getLogger().info("========================================");
        getLogger().info("  MasterManCore v" + getDescription().getVersion());
        getLogger().info("  Complete Core for MasterManMC");
        getLogger().info("  Economy | AH | Crates | Enchants");
        getLogger().info("  Weapons | Homes | Warps | TPA");
        getLogger().info("========================================");
        getLogger().info("Enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (economyManager != null) economyManager.save();
        if (auctionManager != null) auctionManager.save();
        if (crateManager != null) crateManager.save();
        if (homeManager != null) homeManager.save();
        if (warpManager != null) warpManager.save();
        getLogger().info("MasterManCore disabled. Data saved.");
    }

    private void registerCommands() {
        getCommand("balance").setExecutor(new BalanceCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("eco").setExecutor(new EcoCommand(this));
        getCommand("ah").setExecutor(new AuctionCommand(this));
        getCommand("crate").setExecutor(new CrateCommand(this));
        getCommand("key").setExecutor(new KeyCommand(this));
        getCommand("ce").setExecutor(new EnchantCommand(this));
        getCommand("giveweapon").setExecutor(new GiveWeaponCommand(this));
        getCommand("setspawn").setExecutor(new SpawnCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("sethome").setExecutor(new HomeCommand(this));
        getCommand("delhome").setExecutor(new HomeCommand(this));
        getCommand("homes").setExecutor(new HomeCommand(this));
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("setwarp").setExecutor(new WarpCommand(this));
        getCommand("delwarp").setExecutor(new WarpCommand(this));
        getCommand("warps").setExecutor(new WarpCommand(this));
        getCommand("tpa").setExecutor(new TpaCommand(this));
        getCommand("tpaccept").setExecutor(new TpaCommand(this));
        getCommand("tpdeny").setExecutor(new TpaCommand(this));
        getCommand("msg").setExecutor(new MsgCommand(this));
        getCommand("reply").setExecutor(new MsgCommand(this));
        getCommand("heal").setExecutor(new PlayerCommand(this));
        getCommand("feed").setExecutor(new PlayerCommand(this));
        getCommand("fly").setExecutor(new PlayerCommand(this));
        getCommand("gamemode").setExecutor(new PlayerCommand(this));
        getCommand("invsee").setExecutor(new PlayerCommand(this));
        getCommand("clear").setExecutor(new PlayerCommand(this));
        getCommand("kit").setExecutor(new KitCommand(this));
        getCommand("kits").setExecutor(new KitCommand(this));
        getCommand("masterman").setExecutor(new MasterManCommand(this));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AuctionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new CrateListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EnchantListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
    }

    public static MasterManCore getInstance() {
        return instance;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public AuctionManager getAuctionManager() {
        return auctionManager;
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }

    public EnchantManager getEnchantManager() {
        return enchantManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public TpaManager getTpaManager() {
        return tpaManager;
    }

    public MessageUtil getMessageUtil() {
        return messageUtil;
    }

    public void reload() {
        reloadConfig();
        messageUtil.reload();
        getLogger().info("Configuration reloaded.");
    }
}
