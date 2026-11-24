package com.mastermanmc.core.managers;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomeManager {

    private final MasterManCore plugin;
    private final Map<UUID, Map<String, Location>> homes = new HashMap<>();
    private File dataFile;
    private FileConfiguration data;

    public HomeManager(MasterManCore plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "homes.yml");
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
        homes.clear();
        if (data.getConfigurationSection("homes") != null) {
            for (String uuidStr : data.getConfigurationSection("homes").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    Map<String, Location> playerHomes = new HashMap<>();
                    for (String name : data.getConfigurationSection("homes." + uuidStr).getKeys(false)) {
                        String path = "homes." + uuidStr + "." + name;
                        World world = Bukkit.getWorld(data.getString(path + ".world"));
                        if (world != null) {
                            Location loc = new Location(world,
                                    data.getDouble(path + ".x"),
                                    data.getDouble(path + ".y"),
                                    data.getDouble(path + ".z"),
                                    (float) data.getDouble(path + ".yaw"),
                                    (float) data.getDouble(path + ".pitch"));
                            playerHomes.put(name.toLowerCase(), loc);
                        }
                    }
                    homes.put(uuid, playerHomes);
                } catch (Exception ignored) {}
            }
        }
    }

    public void save() {
        if (data == null) return;
        data.set("homes", null);
        for (Map.Entry<UUID, Map<String, Location>> entry : homes.entrySet()) {
            for (Map.Entry<String, Location> h : entry.getValue().entrySet()) {
                String path = "homes." + entry.getKey() + "." + h.getKey();
                Location loc = h.getValue();
                data.set(path + ".world", loc.getWorld().getName());
                data.set(path + ".x", loc.getX());
                data.set(path + ".y", loc.getY());
                data.set(path + ".z", loc.getZ());
                data.set(path + ".yaw", loc.getYaw());
                data.set(path + ".pitch", loc.getPitch());
            }
        }
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean setHome(Player player, String name) {
        name = name.toLowerCase();
        int max = plugin.getConfig().getInt("homes.max-homes", 3);
        Map<String, Location> playerHomes = homes.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        if (!playerHomes.containsKey(name) && playerHomes.size() >= max) {
            plugin.getMessageUtil().send(player, "&cYou have reached the maximum number of homes (" + max + ")!");
            return false;
        }
        playerHomes.put(name, player.getLocation());
        save();
        plugin.getMessageUtil().send(player, plugin.getMessageUtil().format("home-set", "name", name));
        return true;
    }

    public boolean deleteHome(Player player, String name) {
        name = name.toLowerCase();
        Map<String, Location> playerHomes = homes.get(player.getUniqueId());
        if (playerHomes == null || !playerHomes.containsKey(name)) {
            plugin.getMessageUtil().send(player, "&cHome not found!");
            return false;
        }
        playerHomes.remove(name);
        save();
        plugin.getMessageUtil().send(player, plugin.getMessageUtil().format("home-deleted", "name", name));
        return true;
    }

    public boolean teleportHome(Player player, String name) {
        name = name.toLowerCase();
        Map<String, Location> playerHomes = homes.get(player.getUniqueId());
        if (playerHomes == null || !playerHomes.containsKey(name)) {
            plugin.getMessageUtil().send(player, "&cHome not found!");
            return false;
        }
        player.teleport(playerHomes.get(name));
        plugin.getMessageUtil().send(player, plugin.getMessageUtil().get("teleported"));
        return true;
    }

    public Set<String> getHomes(Player player) {
        Map<String, Location> playerHomes = homes.get(player.getUniqueId());
        return playerHomes == null ? Collections.emptySet() : playerHomes.keySet();
    }
}
