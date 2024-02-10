package com.planetgallium.kitpvp.game;

import com.cryptomorin.xseries.XSound;
import com.planetgallium.database.TopEntry;
import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.PlayerLevelUpEvent;
import com.planetgallium.kitpvp.util.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

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
        CacheManager.getUUIDCache().put(p.getName(), p.getUniqueId().toString());
        database.registerPlayerStats(p);
    }

    private boolean isPlayerRegistered(String username) {
        if (CacheManager.getStatsCache().containsKey(username)) { // try to use cache first to be faster
            return true;
        }
        return database.isPlayerRegistered(username);
    }

    public double getKDRatio(String username) {
        if (getStat("deaths", username) != 0) {
            double divided = (double) getStat("kills", username) / getStat("deaths", username);
            return Toolkit.round(divided, 2);
        }
        return 0.00;
    }

    public void removeExperience(String username, int amount) {
        if (levels.getBoolean("Levels.Levels.Enabled")) {
            int currentExperience = getStat("experience", username);
            setStat("experience", username, currentExperience >= amount ? currentExperience - amount : 0);
        }
    }

    public void addExperience(Player p, int experienceToAdd) {
        if (levels.getBoolean("Levels.Levels.Enabled")) {
            int currentExperience = getStat("experience", p.getName());
            int newExperience = applyPossibleXPMultiplier(p, currentExperience + experienceToAdd);
            setStat("experience", p.getName(), newExperience);
            if (getStat("experience", p.getName()) >= getRegularOrRelativeNeededExperience(p.getName())) {
                levelUp(p);
                Bukkit.getPluginManager().callEvent(new PlayerLevelUpEvent(p, getStat("level", p.getName())));
            }
        }
    }

    private int applyPossibleXPMultiplier(Player p, int experience) {
        double xpMultiplier = Toolkit.getPermissionAmountDouble(p, "kp.xpmultiplier.", 1.0);
        return (int) (experience * xpMultiplier);
    }

    public void levelUp(Player p) {
        String username = p.getName();

        if (getStat("level", username) < levels.getInt("Levels.Options.Maximum-Level")) {

            int newLevel = getStat("level", username) + 1;
            setStat("level", username, newLevel);
            setStat("experience", username, 0);

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
            setStat("experience", username, 0);
        }
    }

    public void addToStat(String identifier, String username, int amount) {
        int updatedAmount = getStat(identifier, username) + amount;
        setStat(identifier, username, updatedAmount);
    }

    public void setStat(String identifier, String username, int data) {
        if (!isPlayerRegistered(username)) {
            return;
        }

        getOrCreateStatsCache(username).setData(identifier, data);
        leaderboards.updateRankings(identifier, new TopEntry(username, data));
    }

    public void pushCachedStatsToDatabase(String username, boolean removeFromCacheAfter) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!CacheManager.getStatsCache().containsKey(username)) {
                    return; // nothing to push if stats cache is empty
                }

                database.setStatsData(username, getOrCreateStatsCache(username));
                if (removeFromCacheAfter) {
                    CacheManager.getStatsCache().remove(username);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public int getStat(String identifier, String username) {
        if (!isPlayerRegistered(username)) {
            return -1;
        }

        return getOrCreateStatsCache(username).getData(identifier);
    }

    public PlayerData getOrCreateStatsCache(String username) {
        if (!isPlayerRegistered(username)) {
            return new PlayerData(-1, -1, -1, -1);
        }

        if (!CacheManager.getStatsCache().containsKey(username)) {
            CacheManager.getStatsCache().put(username, database.getStatsData(username));
        }
        return CacheManager.getStatsCache().get(username);
    }

    public int getRegularOrRelativeNeededExperience(String username) {
        int level = getStat("level", username);

        if (levels.contains("Levels.Levels." + level + ".Experience-To-Level-Up")) {
            return levels.getInt("Levels.Levels." + level + ".Experience-To-Level-Up");
        }
        return levels.getInt("Levels.Options.Experience-To-Level-Up");
    }

}
