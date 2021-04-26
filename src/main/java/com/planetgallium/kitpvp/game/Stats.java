package com.planetgallium.kitpvp.game;

import com.cryptomorin.xseries.XSound;
import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.PlayerLevelUpEvent;
import com.planetgallium.kitpvp.util.*;
import com.zp4rker.localdb.DataType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class Stats {

    private final Infobase database;
    private final Resources resources;
    private final Resource levels;
    private final Leaderboards leaderboards;

    public Stats(Game plugin, Arena arena) {
        this.database = plugin.getDatabase();
        this.resources = plugin.getResources();
        this.levels = plugin.getResources().getLevels();
        this.leaderboards = arena.getLeaderboards();
    }

    public void createPlayer(Player p) {
        CacheManager.getUUIDCache().put(p.getName(), p.getUniqueId().toString());
        if (!isPlayerRegistered(p.getName())) {
            database.createPlayerStats(p);
        }
    }

    public boolean isPlayerRegistered(String username) {
        return database.databaseTableContainsPlayer("stats", username);
    }

    public double getKDRatio(String username) {
        if (getStat("deaths", username) != 0) {
            double divided = (double) getStat("kills", username) / getStat("deaths", username);
            return Toolkit.round(divided, 2);
        }
        return 0.00;
    }

    public void removeExperience(String username, int amount) {
        if (levels.getBoolean("Levels.Levels.Enabled") && isPlayerRegistered(username)) {
            int currentExperience = getStat("experience", username);
            setStat("experience", username, currentExperience >= amount ? currentExperience - amount : 0);
        }
    }

    public void addExperience(Player p, int experience) {
        if (levels.getBoolean("Levels.Levels.Enabled")) {
            setStat("experience", p.getName(), experience);
            if (getStat("experience", p.getName()) >= levels.getInt("Levels.Options.Experience-To-Level-Up")) {
                levelUp(p);
                Bukkit.getPluginManager().callEvent(new PlayerLevelUpEvent(p, getStat("level", p.getName())));
            }
        }
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

            p.sendMessage(resources.getMessages().getString("Messages.Other.Level")
                                  .replace("%level%", String.valueOf(newLevel)));
            XSound.play(p, "ENTITY_PLAYER_LEVELUP, 1, 1");

        } else {
            setStat("experience", username, 0);
        }

    }

    public void addToStat(String identifier, String username, int amount) {
        int updatedAmount = (int) database.getData("stats", identifier, username) + amount;
        setStat(identifier, username, updatedAmount);
    }

    public void setStat(String identifier, String username, int data) {
        // valid identifier: "kills", "deaths", "level", "experience"
        if (!database.databaseTableContainsPlayer("stats", username)) return;
        database.setData("stats", identifier, data, DataType.INTEGER, username);
        leaderboards.updateCache(identifier, new PlayerEntry(username, data));
    }

    public int getStat(String identifier, String username) {
        return (int) database.getData("stats", identifier, username);
    }

}
