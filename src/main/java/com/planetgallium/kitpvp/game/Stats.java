package com.planetgallium.kitpvp.game;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Toolkit;
import com.zp4rker.localdb.DataType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Stats {

    private final Infobase database;

    public Stats(Game plugin) {
        this.database = plugin.getDatabase();
    }

    public void createPlayer(Player p) {
        if (!isPlayerRegistered(p.getUniqueId())) {
            database.createPlayerStats(p);
        }
    }

    public boolean isPlayerRegistered(UUID uuid) {
        return database.databaseTableContainsUUID("stats", uuid.toString());
    }

    public double getKDRatio(UUID uuid) {
        if (getDeaths(uuid) != 0) {
            double divided = (double) getKills(uuid) / getDeaths(uuid);
            return Toolkit.round(divided, 2);
        }
        return 0.00;
    }

    public void addKills(UUID uuid, int amount) {
        setKills(uuid, getKills(uuid) + amount);
    }

    public void addDeaths(UUID uuid, int amount) {
        setDeaths(uuid, getDeaths(uuid) + amount);
    }

    public void addExperience(UUID uuid, int amount) {
        setExperience(uuid, getExperience(uuid) + amount);
    }

    public void removeExperience(UUID uuid, int amount) {
        if (isPlayerRegistered(uuid)) {
            if (getExperience(uuid) > amount) {
                setExperience(uuid, getExperience(uuid) - amount);
            }
        }
    }

    public void setKills(UUID uuid, int kills) {
        if (!database.databaseTableContainsUUID("stats", uuid.toString())) return;
        database.setData("stats", "kills", kills, DataType.INTEGER, uuid.toString());
    }

    public void setDeaths(UUID uuid, int deaths) {
        if (!database.databaseTableContainsUUID("stats", uuid.toString())) return;
        database.setData("stats", "deaths", deaths, DataType.INTEGER, uuid.toString());
    }

    public void setExperience(UUID uuid, int experience) {
        if (!database.databaseTableContainsUUID("stats", uuid.toString())) return;
        database.setData("stats", "experience", experience, DataType.INTEGER, uuid.toString());
    }

    public void setLevel(UUID uuid, int level) {
        if (!database.databaseTableContainsUUID("stats", uuid.toString())) return;
        database.setData("stats", "level", level, DataType.INTEGER, uuid.toString());
    }

    public int getKills(UUID uuid) {
        return (int) database.getData("stats", "kills", uuid.toString());
    }

    public int getDeaths(UUID uuid) {
        return (int) database.getData("stats", "deaths", uuid.toString());
    }

    public int getExperience(UUID uuid) {
        return (int) database.getData("stats", "experience", uuid.toString());
    }

    public int getLevel(UUID uuid) {
        return (int) database.getData("stats", "level", uuid.toString());
    }

}
