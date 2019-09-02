package com.planetgallium.kitpvp.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.PlayerData;

public class Database {

	private Game game;
	private boolean isEnabled;
	
	private Connection connection;
	private Map<UUID, PlayerData> cache;
	
	private String host;
	private String database;
	private String table;
	private int port;
	private String username;
	private String password;
	
	public Database(Game game, String path) {
		this.game = game;
		
		this.cache = new HashMap<UUID, PlayerData>();
		
		this.host = game.getConfig().getString(path + ".Host");
		this.database = game.getConfig().getString(path + ".Database");
		this.table = game.getConfig().getString(path + ".Table");
		this.port = game.getConfig().getInt(path + ".Port");
		this.username = game.getConfig().getString(path + ".Username");
		this.password = game.getConfig().getString(path + ".Password");
	}
	
	public void setup() {
		
		try {
			
			synchronized (this) {
				
				if (getConnection() != null && !getConnection().isClosed()) {
					
					return;
					
				}
				
				this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username, password);
				
				Bukkit.getConsoleSender().sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aMySQL was successfully connected on port " + port + "."));
				isEnabled = true;
				
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public void holdConnection() {
		
        try {
        	
            getConnection().createStatement().executeQuery("SELECT 1;");
            
        } catch (SQLException e) {
        	
            e.printStackTrace();
            
        }
        
        Bukkit.getScheduler().runTaskLater(game, () -> holdConnection(), 24000L);
		
	}
	
	public void createData() {
		
		boolean isTableCreated = false;
		
		try {
			
			DatabaseMetaData databaseMeta = getConnection().getMetaData();
			ResultSet result = databaseMeta.getTables(null, null, table, null);
			
			if (result.next()) {
				
				isTableCreated = true;
				
			}
			
			String playerTable = "CREATE TABLE " + table + " (" +
					"UUID VARCHAR(255)," +
                    "USERNAME VARCHAR(255)," +
                    "LEVEL INT (4)," +
                    "EXPERIENCE INT(10)," +
                    "KILLS INT(10)," +
                    "DEATHS INT(10)" +
                    ");";
			
			if (!isTableCreated) {
				
				Statement statement = getConnection().createStatement();
				statement.executeUpdate(playerTable);
				
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public void addPlayer(Player p) {
		
		if (this.isEnabled) {
			
			try {
				
				Statement statement = getConnection().createStatement();
				ResultSet result = statement.executeQuery("SELECT * FROM " + getTable() + " WHERE UUID='" + p.getUniqueId() + "'");
				boolean hasPlayerData = false;
				
				while (result.next()) {
					
					hasPlayerData = true;
					
	                String username = result.getString("USERNAME");
	                int level = result.getInt("LEVEL");
	                int experience = result.getInt("EXPERIENCE");
	                int kills = result.getInt("KILLS");
	                int deaths = result.getInt("DEATHS");
	                PlayerData playerData = new PlayerData(username, level, experience, kills, deaths);
	                
	                getCache().put(p.getUniqueId(), playerData);
	                
				}
				
				if (!hasPlayerData) {
					
					game.getArena().getStats().createPlayer(p.getName(), p.getUniqueId());
					String sqlStatement = "INSERT INTO " + getTable() + " (UUID, USERNAME, LEVEL, EXPERIENCE, KILLS, DEATHS) VALUES ('" + p.getUniqueId() + "', '" + p.getName() + "', 0, 0, 0, 0)";
					statement.executeUpdate(sqlStatement);
					
				}
				
			} catch (SQLException e) {
				
	            e.printStackTrace();
	            
	        }
			
		}
		
	}
	
	public void saveAndRemovePlayer(Player p) {
		
		if (this.isEnabled) {
			
			PlayerData playerData = getCache().get(p.getUniqueId());
			
			try {
				
				Statement statement = getConnection().createStatement();
				String sqlStatement = "UPDATE " + getTable() + " SET USERNAME='" + playerData.getUsername() + "', LEVEL=" + playerData.getLevel() + ", EXPERIENCE="
	                    + playerData.getExperience()+", KILLS=" + playerData.getKills() + ", DEATHS=" + playerData.getDeaths() + " WHERE UUID='"
	                    + p.getUniqueId() + "'";
				
				statement.executeUpdate(sqlStatement);
				
			} catch (SQLException e) {
				
	            e.printStackTrace();
	            
	        }
			
			getCache().remove(p.getUniqueId());
			
		}
		
	}
	
	public void importData(Resources resources) {
			
		File file = new File(game.getDataFolder(), "stats.yml");
		
		if (file.exists()) {
			file.delete();
		}
		
		try {
			
			file.createNewFile();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		for (UUID uuid: game.getDatabase().getCache().keySet()) {

			PlayerData playerData = game.getDatabase().getCache().get(uuid);

			resources.getStats().set("Stats.Players." + uuid + ".Username", playerData.getUsername());
			resources.getStats().set("Stats.Players." + uuid + ".Level", playerData.getLevel());
			resources.getStats().set("Stats.Players." + uuid + ".Experience", playerData.getExperience());
			resources.getStats().set("Stats.Players." + uuid + ".Kills", playerData.getKills());
			resources.getStats().set("Stats.Players." + uuid + ".Deaths", playerData.getDeaths());

			resources.save();
			
		}
		
	}
	
	public void exportData(Resources resources) {
		
		ConfigurationSection stats = resources.getStats().getConfigurationSection("Stats.Players");
		
		String deleteTableContents = "DELETE FROM " + game.getDatabase().getTable();
		Statement statement = null;
		
		try {
			
			statement = game.getDatabase().getConnection().createStatement();
			statement.executeUpdate(deleteTableContents);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		}
		
		for (String uuid: stats.getKeys(false)) {
			
			String username = resources.getStats().getString("Stats.Players." + uuid + ".Username");
			int level = resources.getStats().getInt("Stats.Players." + uuid + ".Level");
			int experience = resources.getStats().getInt("Stats.Players." + uuid + ".Experience");
			int kills = resources.getStats().getInt("Stats.Players." + uuid + ".Kills");
			int deaths = resources.getStats().getInt("Stats.Players." + uuid + ".Deaths");

			String sqlStatement = "INSERT INTO " + game.getDatabase().getTable() + " (UUID, USERNAME, LEVEL, EXPERIENCE, KILLS, DEATHS) VALUES ('" + uuid
					+ "', '" + username + "', " + level + ", " + experience + ", " + kills + ", " + deaths + ")";
			try {
				
				statement.executeUpdate(sqlStatement);
				
			} catch (SQLException e) {
				
				e.printStackTrace();
				
			}

		}
		
	}
	
	public boolean isEnabled() { return isEnabled; }
	
	public String getTable() { return table; }
	
	public Connection getConnection() { return connection; }
	
	public Map<UUID, PlayerData> getCache() { return cache; }
	
}
