package com.zp4rker.localdb;

import org.bukkit.plugin.java.JavaPlugin;

import com.zp4rker.localdb.db.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private List<Table> tables = new ArrayList<>();
    private SQLite sqLite;

    public Database(JavaPlugin plugin, String dbName, Table table) {
        this.tables.add(table);
        this.sqLite = new SQLite(plugin, dbName, this);
        sqLite.load();
        table.setSqLite(sqLite);
    }

    public Database(JavaPlugin plugin, String dbName, Table table, String file) {
        this.tables.add(table);
        this.sqLite = new SQLite(plugin, dbName, this, file);
        sqLite.load();
        table.setSqLite(sqLite);
    }

    public List<Table> getTables() {
        return tables;
    }

    public String getTableQuery() {
        return tables.get(0).getQuery();
    }

    public SQLite getDB() {
        return this.sqLite;
    }

    public Connection getConnection() {
        return this.sqLite.getSQLConnection();
    }

    public void addTable(Table table) {
        tables.add(table);
        table.setSqLite(sqLite);
        try {
            PreparedStatement statement = this.sqLite.getSQLConnection().prepareStatement(table.getQuery());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteTable(Table table) {
        tables.remove(table);
        try {
            PreparedStatement statement = this.sqLite.getSQLConnection().prepareStatement(table.getDeleteQuery());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
