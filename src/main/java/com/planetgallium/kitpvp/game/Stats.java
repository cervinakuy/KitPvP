package com.planetgallium.kitpvp.game;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Toolkit;
import com.zp4rker.localdb.DataType;
import org.bukkit.entity.Player;

public class Stats {

    private final Infobase database;

    public Stats(Game plugin) {
        this.database = plugin.getDatabase();
    }

    public void createPlayer(Player p) {
        if (!isPlayerRegistered(p.getUniqueId().toString())) {
            database.createPlayerStats(p);
        }
    }

    public boolean isPlayerRegistered(String uuid) {
        return database.databaseTableContainsUUID("stats", uuid);
    }

    public double getKDRatio(String uuid) {
        if (getDeaths(uuid) != 0) {
            double divided = (double) getKills(uuid) / getDeaths(uuid);
            return Toolkit.round(divided, 2);
        }
        return 0.00;
    }

    public void addKills(String uuid, int amount) {
        setKills(uuid, getKills(uuid) + amount);
    }

    public void addDeaths(String uuid, int amount) {
        setDeaths(uuid, getDeaths(uuid) + amount);
    }

    public void addExperience(String uuid, int amount) {
        setExperience(uuid, getExperience(uuid) + amount);
    }

    public void removeExperience(String uuid, int amount) {
        if (isPlayerRegistered(uuid)) {
            if (getExperience(uuid) > amount) {
                setExperience(uuid, getExperience(uuid) - amount);
            }
        }
    }

    public void setKills(String uuid, int kills) {
        if (!database.databaseTableContainsUUID("stats", uuid)) return;
        database.setData("stats", "kills", kills, DataType.INTEGER, uuid);
    }

    public void setDeaths(String uuid, int deaths) {
        if (!database.databaseTableContainsUUID("stats", uuid)) return;
        database.setData("stats", "deaths", deaths, DataType.INTEGER, uuid);
    }

    public void setExperience(String uuid, int experience) {
        if (!database.databaseTableContainsUUID("stats", uuid)) return;
        database.setData("stats", "experience", experience, DataType.INTEGER, uuid);
    }

    public void setLevel(String uuid, int level) {
        if (!database.databaseTableContainsUUID("stats", uuid)) return;
        database.setData("stats", "level", level, DataType.INTEGER, uuid);
    }

    public int getKills(String uuid) {
        return (int) database.getData("stats", "kills", uuid);
    }

    public int getDeaths(String uuid) {
        return (int) database.getData("stats", "deaths", uuid);
    }

    public int getExperience(String uuid) {
        return (int) database.getData("stats", "experience", uuid);
    }

    public int getLevel(String uuid) {
        return (int) database.getData("stats", "level", uuid);
    }

}
