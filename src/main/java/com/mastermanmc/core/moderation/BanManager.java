package com.mastermanmc.core.moderation;

import com.mastermanmc.core.MasterManCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BanManager {

    private final MasterManCore plugin;
    private final Map<UUID, BanEntry> bans = new HashMap<>();
    private File dataFile;
    private FileConfiguration data;

    public BanManager(MasterManCore plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "bans.yml");
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
        bans.clear();
        if (data.getConfigurationSection("bans") != null) {
            for (String key : data.getConfigurationSection("bans").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String path = "bans." + key;
                    String reason = data.getString(path + ".reason", "No reason specified");
                    String banner = data.getString(path + ".banner", "Console");
                    long expire = data.getLong(path + ".expire", -1);
                    long bannedAt = data.getLong(path + ".bannedAt", System.currentTimeMillis());
                    if (expire == -1 || expire > System.currentTimeMillis()) {
                        bans.put(uuid, new BanEntry(uuid, reason, banner, expire, bannedAt));
                    }
                } catch (Exception ignored) {}
            }
        }
        plugin.getLogger().info("Loaded " + bans.size() + " active bans.");
    }

    public void save() {
        if (data == null) return;
        data.set("bans", null);
        for (Map.Entry<UUID, BanEntry> entry : bans.entrySet()) {
            BanEntry ban = entry.getValue();
            String path = "bans." + entry.getKey();
            data.set(path + ".reason", ban.getReason());
            data.set(path + ".banner", ban.getBanner());
            data.set(path + ".expire", ban.getExpire());
            data.set(path + ".bannedAt", ban.getBannedAt());
        }
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isBanned(UUID uuid) {
        BanEntry ban = bans.get(uuid);
        if (ban == null) return false;
        if (ban.getExpire() != -1 && ban.getExpire() < System.currentTimeMillis()) {
            bans.remove(uuid);
            save();
            return false;
        }
        return true;
    }

    public BanEntry getBan(UUID uuid) {
        if (!isBanned(uuid)) return null;
        return bans.get(uuid);
    }

    public void ban(UUID uuid, String reason, String banner, long durationMs) {
        long expire = durationMs <= 0 ? -1 : System.currentTimeMillis() + durationMs;
        bans.put(uuid, new BanEntry(uuid, reason, banner, expire, System.currentTimeMillis()));
        save();

        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            String msg = "&cYou have been banned!\n&7Reason: &f" + reason + "\n&7By: &f" + banner;
            if (expire != -1) {
                msg += "\n&7Expires: &f" + formatDuration(expire - System.currentTimeMillis());
            } else {
                msg += "\n&7This ban is permanent.";
            }
            player.kickPlayer(plugin.getMessageUtil().color(msg));
        }
    }

    public boolean unban(UUID uuid) {
        if (bans.remove(uuid) != null) {
            save();
            return true;
        }
        return false;
    }

    public Collection<BanEntry> getAllBans() {
        return bans.values();
    }

    public static String formatDuration(long ms) {
        if (ms < 0) return "Permanent";
        long seconds = ms / 1000;
        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long mins = (seconds % 3600) / 60;
        if (days > 0) return days + "d " + hours + "h";
        if (hours > 0) return hours + "h " + mins + "m";
        return mins + "m";
    }

    public static long parseDuration(String input) {
        input = input.toLowerCase().trim();
        if (input.equals("perm") || input.equals("permanent") || input.equals("-1")) return -1;
        try {
            long value = Long.parseLong(input.replaceAll("[^0-9]", ""));
            if (input.endsWith("d")) return value * 86400000L;
            if (input.endsWith("h")) return value * 3600000L;
            if (input.endsWith("m")) return value * 60000L;
            if (input.endsWith("s")) return value * 1000L;
            return value * 60000L;
        } catch (Exception e) {
            return -1;
        }
    }

    public static class BanEntry {
        private final UUID uuid;
        private final String reason;
        private final String banner;
        private final long expire;
        private final long bannedAt;

        public BanEntry(UUID uuid, String reason, String banner, long expire, long bannedAt) {
            this.uuid = uuid;
            this.reason = reason;
            this.banner = banner;
            this.expire = expire;
            this.bannedAt = bannedAt;
        }

        public UUID getUuid() { return uuid; }
        public String getReason() { return reason; }
        public String getBanner() { return banner; }
        public long getExpire() { return expire; }
        public long getBannedAt() { return bannedAt; }
        public boolean isPermanent() { return expire == -1; }
    }
}
