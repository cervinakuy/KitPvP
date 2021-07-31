package com.zp4rker.localdb.db;

import org.bukkit.plugin.java.JavaPlugin;

import com.zp4rker.localdb.Database;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

public class SQLite {

    private String dbName;
    private String query;
    private boolean inDF = true;
    private String dir;
    private JavaPlugin plugin;
    private Connection connection;

    public SQLite(JavaPlugin instance, String dbName, Database db) {
        this.plugin = instance;
        this.dbName = dbName;
        query = db.getTableQuery();
        this.connection = getSQLConnection();
    }

    public SQLite(JavaPlugin instance, String dbName, Database db, String directory) {
        this.plugin = instance;
        this.dbName = dbName;
        query = db.getTableQuery();
        this.inDF = false;
        this.dir = directory;
        this.connection = getSQLConnection();
    }

    public Connection getSQLConnection() {
        File file;
        if (inDF) {
            file = new File(plugin.getDataFolder(), dbName + ".db");
            plugin.getDataFolder().mkdirs();
        } else {
            new File(this.dir).mkdirs();
            file = new File(this.dir + "/" + dbName + ".db");
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: " + dbName + ".db");
            }
        }
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + file);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            PreparedStatement s = connection.prepareStatement(query);
            s.executeUpdate();
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
    
    public void closeConnection() {
    	try {
    		if (!(connection.isClosed())) {
        		connection.close();
        		connection = null;
        	} else {
        		connection = null;
        	}
    	} catch(Exception e) {
    		Error.close(plugin, e);
    	}
    }

}