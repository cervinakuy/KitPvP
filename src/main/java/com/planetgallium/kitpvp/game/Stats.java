package com.planetgallium.kitpvp.game;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.PlayerLevelUpEvent;
import com.planetgallium.kitpvp.util.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class Stats {

    private final Game plugin;
    private final Infobase database;
    private final Resources resources;
    private final Resource levels;
    private final Leaderboards leaderboards;

    public Stats(Game plugin, Arena arena) {
        this.plugin = plugin;
        this.database = plugin.getDatabase();
        this.resources = plugin.getResources();
        this.levels = plugin.getResources().getLevels();
        this.leaderboards = arena.getLeaderboards();
    }

    public void createPlayer(Player p) {
        CacheManager.getUUIDCache().put(p.getName(), p.getUniqueId());
        database.registerPlayerStats(p);
    }

    private boolean isPlayerRegistered(UUID uniqueId) {
        if (CacheManager.getStatsCache().containsKey(uniqueId)) { // try to use cache first to be faster
            return true;
        }
        return database.isPlayerRegistered(uniqueId);
    }

    public double getKDRatio(UUID uniqueId) {
        if (getStat("deaths", uniqueId) != 0) {
            double divided = (double) getStat("kills", uniqueId) / getStat("deaths", uniqueId);
            return Toolkit.round(divided, 2);
        }
        return 0.00;
    }

    public void removeExperience(UUID uniqueId, int amount) {
        if (levels.getBoolean("Levels.Levels.Enabled")) {
            int currentExperience = getStat("experience", uniqueId);
            setStat("experience", uniqueId, currentExperience >= amount ? currentExperience - amount : 0);
        }
    }

    public void addExperience(Player p, int experienceToAdd) {
        if (levels.getBoolean("Levels.Levels.Enabled")) {
            int currentExperience = getStat("experience", p.getUniqueId());
            int newExperience = applyPossibleXPMultiplier(p, currentExperience + experienceToAdd);
            setStat("experience", p.getUniqueId(), newExperience);
            if (getStat("experience", p.getUniqueId()) >= getRegularOrRelativeNeededExperience(p.getUniqueId())) {
                levelUp(p);
                Bukkit.getPluginManager().callEvent(new PlayerLevelUpEvent(p, getStat("level", p.getUniqueId())));
            }
        }
    }

    private int applyPossibleXPMultiplier(Player p, int experience) {
        double xpMultiplier = Toolkit.getPermissionAmountDouble(p, "kp.xpmultiplier.", 1.0);
        return (int) (experience * xpMultiplier);
    }

    public void levelUp(Player p) {
        UUID uniqueId = p.getUniqueId();

        if (getStat("level", uniqueId) < levels.getInt("Levels.Options.Maximum-Level")) {

            int newLevel = getStat("level", uniqueId) + 1;
            setStat("level", uniqueId, newLevel);
            setStat("experience", uniqueId, 0);

            List<String> levelUpCommands = levels.getStringList("Levels.Commands-On-Level-Up");
            Toolkit.runCommands(p, levelUpCommands, "%level%", String.valueOf(newLevel));

            if (levels.contains("Levels.Levels." + newLevel + ".Commands")) {
                List<String> commandsList = levels.getStringList("Levels.Levels." + newLevel + ".Commands");
                Toolkit.runCommands(p, commandsList, "%level%", String.valueOf(newLevel));
            }

            p.sendMessage(resources.getMessages().fetchString("Messages.Other.Level")
                                  .replace("%level%", String.valueOf(newLevel)));
            Toolkit.playSoundToPlayer(p, "ENTITY_PLAYER_LEVELUP", 1);

        } else {
            setStat("experience", uniqueId, 0);
        }
    }

    public void addToStat(String identifier, UUID uniqueId, int amount) {
        int updatedAmount = getStat(identifier, uniqueId) + amount;
        setStat(identifier, uniqueId, updatedAmount);
    }

    public void setStat(String identifier, UUID uniqueId, int data) {
        if (!isPlayerRegistered(uniqueId)) {
            return;
        }

        getOrCreateStatsCache(uniqueId).setData(identifier, data);
        leaderboards.updateRankings(identifier, uniqueId, data);
    }

    public void pushCachedStatsToDatabase(UUID uniqueId, boolean removeFromCacheAfter) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!CacheManager.getStatsCache().containsKey(uniqueId)) {
                    return; // nothing to push if stats cache is empty
                }

                database.setStatsData(uniqueId, getOrCreateStatsCache(uniqueId));
                if (removeFromCacheAfter) {
                    CacheManager.getStatsCache().remove(uniqueId);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public int getStat(String identifier, UUID uniqueId) {
        if (!isPlayerRegistered(uniqueId)) {
            return -1;
        }

        return getOrCreateStatsCache(uniqueId).getData(identifier);
    }

    public PlayerData getOrCreateStatsCache(UUID uniqueId) {
        if (!isPlayerRegistered(uniqueId)) {
            return new PlayerData(-1, -1, -1, -1);
        }

        if (!CacheManager.getStatsCache().containsKey(uniqueId)) {
            CacheManager.getStatsCache().put(uniqueId, database.getStatsData(uniqueId));
        }
        return CacheManager.getStatsCache().get(uniqueId);
    }

    public int getRegularOrRelativeNeededExperience(UUID uniqueId) {
        int level = getStat("level", uniqueId);

        if (levels.contains("Levels.Levels." + level + ".Experience-To-Level-Up")) {
            return levels.getInt("Levels.Levels." + level + ".Experience-To-Level-Up");
        }
        return levels.getInt("Levels.Options.Experience-To-Level-Up");
    }

}
