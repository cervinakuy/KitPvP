package com.planetgallium.kitpvp.game;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;
import com.zp4rker.localdb.Column;
import com.zp4rker.localdb.DataType;
import com.zp4rker.localdb.Database;
import com.zp4rker.localdb.Table;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class Infobase {

    private final Game plugin;
    private final Resources resources;
    private final Database database;
    private final Map<String, Table> kitCooldownTables;

    // TODO: add cooldown table when kit is created
    // TODO: eventually fork localDB and add these helper methods ig?
    // TODO: use this to make leaderboards (use table.search for a kills column for example)

    public Infobase(Game plugin) {

        this.plugin = plugin;
        this.resources = plugin.getResources();
        this.kitCooldownTables = new HashMap<>();

        Column uuid = new Column("uuid", DataType.STRING, 0);

        // Stats
        Column username = new Column("username", DataType.STRING, 0);
        Column kills = new Column("kills", DataType.INTEGER, 0);
        Column deaths = new Column("deaths", DataType.INTEGER, 0);
        Column experience = new Column("experience", DataType.INTEGER, 0);
        Column level = new Column("level", DataType.INTEGER, 0);
        Table statsTable = new Table("stats", uuid, username, kills, deaths, experience, level);

        // Cooldowns
        Column last_used = new Column("last_used", DataType.INTEGER, 0);

        this.database = new Database(plugin, "storage", statsTable, "plugins/KitPvP");

        for (String kitName : resources.getKitList(false)) {
            kitCooldownTables.put(kitName, new Table(kitName + "_cooldowns", uuid, last_used));
        }

        for (String kitCooldownTableName : kitCooldownTables.keySet()) {
            database.addTable(kitCooldownTables.get(kitCooldownTableName));
        }

    }

    public void createPlayerStats(Player p) {

        if (databaseTableContainsUUID("stats", p.getUniqueId().toString())) {
            return;
        }

        Column uuid = new Column("uuid", DataType.STRING, 0);
        Column username = new Column("username", DataType.STRING, 0);
        Column kills = new Column("kills", DataType.INTEGER, 0);
        Column deaths = new Column("deaths", DataType.INTEGER, 0);
        Column experience = new Column("experience", DataType.INTEGER, 0);
        Column level = new Column("level", DataType.INTEGER, 0);

        uuid.setValue(p.getUniqueId().toString());
        username.setValue(p.getName());
        kills.setValue(0);
        deaths.setValue(0);
        experience.setValue(0);
        level.setValue(resources.getLevels().getInt("Levels.Options.Minimum-Level"));

        getTableByName("stats").insert(uuid, username, kills, deaths, experience, level);

    }

    public void exportStats() {

        Resource statsResource = new Resource(plugin, "stats.yml");
        statsResource.load();

        ConfigurationSection statsSection = statsResource.getConfigurationSection("Stats.Players");

        Column uuidColumn = new Column("uuid", DataType.STRING);
        Column username = new Column("username", DataType.STRING);
        Column kills = new Column("kills", DataType.INTEGER);
        Column deaths = new Column("deaths", DataType.INTEGER);
        Column experience = new Column("experience", DataType.INTEGER);
        Column level = new Column("level", DataType.INTEGER);
        Table statsTable = getTableByName("stats");

        for (String uuid : statsSection.getKeys(false)) {
            ConfigurationSection playerSection = statsSection.getConfigurationSection(uuid);
            uuidColumn.setValue(uuid);
            username.setValue(playerSection.getString("Username"));
            kills.setValue(playerSection.getInt("Kills"));
            deaths.setValue(playerSection.getInt("Deaths"));
            experience.setValue(playerSection.getInt("Experience"));
            level.setValue(playerSection.getInt("Level"));
            statsTable.insert(uuidColumn, username, kills, deaths, experience, level);
        }

        statsResource.getFile().delete();

    }

    public void addKitCooldownTable(String kitName) {
        Column uuid = new Column("uuid", DataType.STRING, 0);
        Column last_used = new Column("last_used", DataType.INTEGER, 0);

        kitCooldownTables.put(kitName, new Table(kitName + "_cooldowns", uuid, last_used));
        database.addTable(kitCooldownTables.get(kitName));
    }

    public void deleteKitCooldownTable(String kitName) {
        Table table = getTableByName(kitName + "_cooldowns");
        if (table == null) return;

        database.deleteTable(table);
        cleanupUnusedKitCooldownTables();
    }

    public boolean databaseTableContainsUUID(String tableName, String uuid) {

        Table table = getTableByName(tableName);
        if (table == null) return false;

        Column uuidColumn = new Column("uuid", DataType.STRING, 0);
        uuidColumn.setValue(uuid);

        return table.containsColumn(uuidColumn);

    }

    public String usernameToUUID(String tableName, String username) {

        Table table = getTableByName(tableName);
        if (table == null) return null;

        Column usernameColumn = new Column("username", DataType.STRING);
        usernameColumn.setValue(username);

        List<List<Column>> results = table.search(usernameColumn);
        if (results.size() > 0 && results.get(0).size() > 0) {
            return (String) table.search(usernameColumn).get(0).get(0).getValue();
        }
        return null;

    }

    public void cleanupUnusedKitCooldownTables() {

        for (Table table : database.getTables()) {
            String tableName = table.getName();
            if (tableName.contains("_cooldowns")) {
                String kitName = tableName.split("_cooldowns")[0];
                if (!plugin.getArena().getKits().isKit(kitName)) {
                    database.deleteTable(table);
                }
            }
        }

    }

    public String getTopNStat(int n, String identifier) {

        // update topN results when a player dies
        Column numberColumn = new Column(identifier, DataType.INTEGER);
        return getTableByName("stats").getTopN(numberColumn, n).get(n - 1);

    }

    public Table getTableByName(String name) {

        for (Table table : database.getTables()) {
            if (table.getName().equals(name)) {
                return table;
            }
        }
        Toolkit.printToConsole(String.format("&7[&b&lKIT-PVP&7] &cCould not find table with name [%s]", name));
        return null;

    }

    public List<Column> getRowByUUID(String tableName, String uuid) {

        Table table = getTableByName(tableName);
        if (table == null) return null;

        Column uuidColumn = new Column("uuid", DataType.STRING, 0);
        uuidColumn.setValue(uuid);

        if (table.containsColumn(uuidColumn)) {
            return table.getExact(uuidColumn);
        }
        return null;

    }

    public Column getColumnByName(String tableName, String columnName, String uuid) {

        Table table = getTableByName(tableName);
        if (table == null) return null;

        for (Column column : getRowByUUID(tableName, uuid)) {
            if (column.getName().equals(columnName)) {
                return column;
            }
        }
        return null;

    }

    public void setData(String tableName, String identifier, Object data, DataType type, String uuid) {

        Table table = getTableByName(tableName);
        if (table == null) return;

        Column dataColumn = new Column(identifier.toLowerCase(), type, 0);
        dataColumn.setValue(data);

        Column uuidColumn = new Column("uuid", DataType.STRING, 0);
        uuidColumn.setValue(uuid);

        if (databaseTableContainsUUID(tableName, uuid)) {
            table.update(uuidColumn, dataColumn);
        } else {
            table.insert(uuidColumn, dataColumn);
        }

    }

    public Object getData(String tableName, String identifier, String uuid) {

        if (databaseTableContainsUUID(tableName, uuid)) {
            return getColumnByName(tableName, identifier, uuid).getValue();
        }
        return null;

    }

}
