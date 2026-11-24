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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WarpManager {

    private final MasterManCore plugin;
    private final Map<String, Location> warps = new HashMap<>();
    private File dataFile;
    private FileConfiguration data;

    public WarpManager(MasterManCore plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "warps.yml");
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
        warps.clear();
        if (data.getConfigurationSection("warps") != null) {
            for (String name : data.getConfigurationSection("warps").getKeys(false)) {
                String path = "warps." + name;
                World world = Bukkit.getWorld(data.getString(path + ".world"));
                if (world != null) {
                    Location loc = new Location(world,
                            data.getDouble(path + ".x"),
                            data.getDouble(path + ".y"),
                            data.getDouble(path + ".z"),
                            (float) data.getDouble(path + ".yaw"),
                            (float) data.getDouble(path + ".pitch"));
                    warps.put(name.toLowerCase(), loc);
                }
            }
        }
    }

    public void save() {
        if (data == null) return;
        data.set("warps", null);
        for (Map.Entry<String, Location> entry : warps.entrySet()) {
            String path = "warps." + entry.getKey();
            Location loc = entry.getValue();
            data.set(path + ".world", loc.getWorld().getName());
            data.set(path + ".x", loc.getX());
            data.set(path + ".y", loc.getY());
            data.set(path + ".z", loc.getZ());
            data.set(path + ".yaw", loc.getYaw());
            data.set(path + ".pitch", loc.getPitch());
        }
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setWarp(String name, Location loc) {
        warps.put(name.toLowerCase(), loc);
        save();
    }

    public boolean deleteWarp(String name) {
        if (warps.remove(name.toLowerCase()) != null) {
            save();
            return true;
        }
        return false;
    }

    public boolean teleport(Player player, String name) {
        Location loc = warps.get(name.toLowerCase());
        if (loc == null) {
            plugin.getMessageUtil().send(player, "&cWarp not found!");
            return false;
        }
        player.teleport(loc);
        plugin.getMessageUtil().send(player, plugin.getMessageUtil().get("teleported"));
        return true;
    }

    public Set<String> getWarps() {
        return warps.keySet();
    }
}
