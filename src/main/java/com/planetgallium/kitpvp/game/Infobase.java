package com.planetgallium.kitpvp.game;

import com.planetgallium.database.*;
import com.planetgallium.database.Record;
import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Infobase {

    private final Game plugin;
    private final Resources resources;

    private final Database database;
    private final Table statsTable;

    private final static int UUID_MAX_CHARACTERS = 36;
    private final static int USERNAME_MAX_CHARACTERS = 16;

    // TODO: make things async?
    // TODO: add caching for cooldowns
    // TODO: implement TIMESTAMP (described in above link) for cooldowns

    public Infobase(Game plugin) {
        this.plugin = plugin;
        this.resources = plugin.getResources();
        this.database = setupDatabase(resources.getConfig());

        // Stats
        this.statsTable = database.createTable("stats", new Record(
                new Field("uuid", DataType.FIXED_STRING, Infobase.UUID_MAX_CHARACTERS),
                new Field("username", DataType.STRING, Infobase.USERNAME_MAX_CHARACTERS),
                new Field("kills", DataType.INTEGER),
                new Field("deaths", DataType.INTEGER),
                new Field("experience", DataType.INTEGER),
                new Field("level", DataType.INTEGER)));

        // Kit Cooldowns
        for (String kitName : resources.getKitList(false)) {
            if (!kitName.startsWith(".")) { // ignore hidden files
                addKitCooldownTable(kitName);
            }
        }
    }

    private Database setupDatabase(Resource config) {
        Toolkit.printToConsole("&7[&b&lKIT-PVP&7] Establishing database connection...");

        // Set database to MySQL, or in any other case, SQLite
        if (config.getString("Storage.Type").equalsIgnoreCase("mysql")) {
            String host = config.getString("Storage.MySQL.Host");
            int port = config.getInt("Storage.MySQL.Port");
            String databaseName = config.getString("Storage.MySQL.Database");
            String username = config.getString("Storage.MySQL.Username");
            String password = config.getString("Storage.MySQL.Password");

            return new Database(host, port, databaseName, username, password);
        } else {
            return new Database("plugins/KitPvP/storage.db");
        }
    }

    public boolean isPlayerRegistered(String username) {
        String uuid = usernameToUUID(username);
        return tableContainsUUID("stats", uuid);
    }

    public void createPlayerStatsIfNew(Player p) {
        Field uuidField = new Field("uuid", DataType.STRING, p.getUniqueId().toString(),
                Infobase.UUID_MAX_CHARACTERS);

        if (!tableContainsUUID("stats", p.getUniqueId().toString())) {
            Record statsRecord = new Record(
                    uuidField,
                    new Field("username", DataType.STRING, p.getName(), Infobase.USERNAME_MAX_CHARACTERS),
                    new Field("kills", DataType.INTEGER, 0),
                    new Field("deaths", DataType.INTEGER, 0),
                    new Field("experience", DataType.INTEGER, 0),
                    new Field("level", DataType.INTEGER,
                            resources.getLevels().getInt("Levels.Options.Minimum-Level")));

            statsTable.insertRecord(statsRecord);
        } else {
            // check if stored username needs changing if a player changed their username
            Record playerRecord = statsTable.getRecord(uuidField);
            if (playerRecord != null) {
                String storedUsername = (String) playerRecord.getFieldValue("username");
                String currentUsername = p.getName();

                if (!storedUsername.equals(currentUsername)) {
                    Field updatedUsernameField = new Field("username", DataType.STRING, p.getName(),
                            Infobase.USERNAME_MAX_CHARACTERS);
                    statsTable.updateRecord(uuidField, updatedUsernameField);
                }
            }
        }
    }

    public void exportStats() {
        Resource statsResource = new Resource(plugin, "stats.yml");
        statsResource.load();

        ConfigurationSection statsSection = statsResource.getConfigurationSection("Stats.Players");

        for (String uuid : statsSection.getKeys(false)) {
            ConfigurationSection playerSection = statsSection.getConfigurationSection(uuid);

            Field uuidField = new Field("uuid", DataType.STRING, uuid, Infobase.UUID_MAX_CHARACTERS);
            Field usernameField = new Field("username", DataType.STRING,
                    playerSection.getString("Username"), Infobase.USERNAME_MAX_CHARACTERS);
            Field killsField = new Field("kills", DataType.INTEGER, playerSection.getInt("Kills"));
            Field deathsField = new Field("deaths", DataType.INTEGER, playerSection.getInt("Deaths"));
            Field experienceField =
                    new Field("experience", DataType.INTEGER, playerSection.getInt("Experience"));
            Field levelField = new Field("level", DataType.INTEGER, playerSection.getInt("Level"));

            Record playerStatsRecord = new Record(uuidField, usernameField, killsField, deathsField, experienceField,
                    levelField);

            if (statsTable.getRecord(uuidField) != null) {
                // stats table already contains UUID for some reason; update its data just in case
                statsTable.updateRecord(uuidField, usernameField, killsField, deathsField, experienceField,
                        levelField);
            } else {
                statsTable.insertRecord(playerStatsRecord);
            }

        }

        File renamedStatsFile = new File("old_stats.yml");
        if (!statsResource.getFile().renameTo(renamedStatsFile)) {
            Toolkit.printToConsole("&7[&b&lKIT-PVP&7] &cThere was a problem renaming stats.yml to old_stats.yml.");
        }
    }

    public String usernameToUUID(String username) {
        if (CacheManager.getUUIDCache().containsKey(username)) {
            return CacheManager.getUUIDCache().get(username);
        }

        if (verifyTableExists("stats")) {
            Table stats = database.getTableByName("stats");

            List<Record> matchingRecords = stats.searchRecords(
                    new Field("username", DataType.STRING, username, Infobase.USERNAME_MAX_CHARACTERS));
            if (matchingRecords.size() == 1) {
                Record matchingRecord = matchingRecords.get(0);

                String uuid = (String) matchingRecord.getFieldValue("uuid");
                CacheManager.getUUIDCache().put(username, uuid);
                return uuid;
            }
        }
        return null;
    }

    private boolean tableContainsUUID(String tableName, String uuid) {
        if (verifyTableExists(tableName)) {
            Table table = database.getTableByName(tableName);
            return table.getRecord(
                    new Field("uuid", DataType.STRING, uuid, Infobase.UUID_MAX_CHARACTERS)) != null;
        }
        return false;
    }

    public List<TopEntry> getTopNStats(String identifier, int n) {
        if (verifyTableExists("stats")) {
            Table table = database.getTableByName("stats");

            return table.getTopN(new Field(identifier, DataType.INTEGER),
                    new Field("username", DataType.STRING, Infobase.USERNAME_MAX_CHARACTERS), n);
        }
        return new ArrayList<>();
    }

    public void addKitCooldownTable(String kitName) {
        database.createTable(kitName + "_cooldowns", new Record(
                        new Field("uuid", DataType.STRING, Infobase.UUID_MAX_CHARACTERS),
                        new Field("last_used", DataType.INTEGER)));
    }

    public void deleteKitCooldownTable(String kitName) {
        String kitCooldownTableName = kitName + "_cooldowns";
        if (verifyTableExists(kitCooldownTableName)) {
            database.deleteTable(kitCooldownTableName);
        }
        cleanupUnusedKitCooldownTables();
    }

    public void cleanupUnusedKitCooldownTables() {
        for (Table table : database.getTables()) {
            String tableName = table.getName();
            if (tableName.contains("_cooldowns")) {
                String kitName = tableName.split("_cooldowns")[0];
                if (!plugin.getArena().getKits().isKit(kitName)) {
                    database.deleteTable(tableName);
                }
            }
        }
    }

    public void setData(String tableName, String identifier, Object data, DataType type, String username) {
        if (verifyTableExists(tableName)) {
            Table table = database.getTableByName(tableName);

            Field uuidField = new Field("uuid", DataType.STRING,
                    usernameToUUID(username), Infobase.UUID_MAX_CHARACTERS);
            Record record = table.getRecord(uuidField);
            Field fieldToUpdate = new Field(identifier, type, data);

            if (record != null) {
                table.updateRecord(uuidField, fieldToUpdate);
            } else {
                System.out.printf("[Database] Failed to set data; database does not contain player %s\n", username);
            }
        }
    }

    public Object getData(String tableName, String identifier, String username) {
        if (verifyTableExists(tableName)) {
            Table table = database.getTableByName(tableName);

            Record record = table.getRecord(new Field("uuid", DataType.STRING, usernameToUUID(username),
                    Infobase.UUID_MAX_CHARACTERS));

            if (record != null) {
                return record.getFieldValue(identifier);
            } else {
                System.out.printf("[Database] Failed to get data; database does not contain player %s\n", username);
            }
        }
        return null;
    }

    private boolean verifyTableExists(String tableName) {
        if (database.getTableByName(tableName) != null) {
            return true;
        }
        System.out.printf("[Database] Failed to perform action; table %s does not exist\n", tableName);
        return false;
    }

}
