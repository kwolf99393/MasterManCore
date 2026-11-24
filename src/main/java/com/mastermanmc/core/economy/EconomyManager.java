package com.mastermanmc.core.economy;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyManager {

    private final MasterManCore plugin;
    private final Map<UUID, Double> balances = new HashMap<>();
    private File dataFile;
    private FileConfiguration data;
    private double startingBalance;
    private String currencySymbol;
    private String currencyName;

    public EconomyManager(MasterManCore plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "economy.yml");
        this.startingBalance = plugin.getConfig().getDouble("economy.starting-balance", 1000.0);
        this.currencySymbol = plugin.getConfig().getString("economy.currency-symbol", "$");
        this.currencyName = plugin.getConfig().getString("economy.currency-name", "Coins");
    }

    public void load() {
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);
        balances.clear();
        if (data.getConfigurationSection("balances") != null) {
            for (String key : data.getConfigurationSection("balances").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    balances.put(uuid, data.getDouble("balances." + key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        plugin.getLogger().info("Loaded " + balances.size() + " economy accounts.");
    }

    public void save() {
        if (data == null) return;
        data.set("balances", null);
        for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
            data.set("balances." + entry.getKey().toString(), entry.getValue());
        }
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getBalance(UUID uuid) {
        return balances.getOrDefault(uuid, startingBalance);
    }

    public double getBalance(Player player) {
        return getBalance(player.getUniqueId());
    }

    public void setBalance(UUID uuid, double amount) {
        balances.put(uuid, Math.max(0, amount));
    }

    public void setBalance(Player player, double amount) {
        setBalance(player.getUniqueId(), amount);
    }

    public void addBalance(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public void addBalance(Player player, double amount) {
        addBalance(player.getUniqueId(), amount);
    }

    public boolean removeBalance(UUID uuid, double amount) {
        double bal = getBalance(uuid);
        if (bal < amount) return false;
        setBalance(uuid, bal - amount);
        return true;
    }

    public boolean removeBalance(Player player, double amount) {
        return removeBalance(player.getUniqueId(), amount);
    }

    public boolean has(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }

    public boolean has(Player player, double amount) {
        return has(player.getUniqueId(), amount);
    }

    public String format(double amount) {
        return currencySymbol + String.format("%,.2f", amount);
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void ensureAccount(Player player) {
        if (!balances.containsKey(player.getUniqueId())) {
            balances.put(player.getUniqueId(), startingBalance);
        }
    }
}
