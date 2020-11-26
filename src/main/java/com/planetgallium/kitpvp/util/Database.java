package com.planetgallium.kitpvp.util;

import java.io.File;
import java.io.IOException;
import java.sql.*;
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
		
		this.cache = new HashMap<>();
		
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
				
				if (connection != null && !connection.isClosed()) {
					
					return;
					
				}

				Class.forName("com.mysql.jdbc.Driver");
				this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username, password);
				Bukkit.getConsoleSender().sendMessage(Toolkit.translate("&7[&b&lKIT-PVP&7] &aMySQL was successfully connected on port " + port + "."));
				isEnabled = true;
				
			}
			
		} catch (SQLException | ClassNotFoundException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public void holdConnection() {
		
        try {
        	
            PreparedStatement statement = connection.prepareStatement("SELECT ?");
            statement.setInt(1, 1);
            statement.executeQuery();
            
        } catch (SQLException e) {
        	
            e.printStackTrace();
            
        }
        
        Bukkit.getScheduler().runTaskLaterAsynchronously(game, () -> holdConnection(), 24000L);
		
	}
	
	public void createData() {
		
		boolean isTableCreated = false;
		
		try {
			
			DatabaseMetaData databaseMeta = connection.getMetaData();
			ResultSet result = databaseMeta.getTables(null, null, table, null);
			
			if (result.next()) {
				
				isTableCreated = true;
				
			}
			String playerTable = "CREATE TABLE " + table + " (" +
					"UUID VARCHAR(255)," +
					"USERNAME VARCHAR(255)," +
					"LEVEL INT(4)," +
					"EXPERIENCE INT(10)," +
					"KILLS INT(10)," +
					"DEATHS INT(10)" +
					")";
			
			if (!isTableCreated) {

				PreparedStatement statement = connection.prepareStatement(playerTable);
				statement.executeUpdate();
				
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public void addPlayer(Player p) {
		
		if (this.isEnabled) {
			
			try {

				PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");
				statement.setString(1, p.getUniqueId().toString());
				ResultSet result = statement.executeQuery();
				boolean hasPlayerData = false;
				
				if (result.next()) {
					
					hasPlayerData = true;
					
	                String username = result.getString("USERNAME");
	                int level = result.getInt("LEVEL");
	                int experience = result.getInt("EXPERIENCE");
	                int kills = result.getInt("KILLS");
	                int deaths = result.getInt("DEATHS");
	                PlayerData playerData = new PlayerData(username, level, experience, kills, deaths);
	                
	                cache.put(p.getUniqueId(), playerData);
	                
				}
				
				if (!hasPlayerData) {
					
					game.getArena().getStats().createPlayer(p.getName(), p.getUniqueId());
					PreparedStatement stmt = connection.prepareStatement("INSERT INTO " + table +
							" (UUID, USERNAME, LEVEL, EXPERIENCE, KILLS, DEATHS) VALUES (?, ?, ?, ?, ?, ?)");
					stmt.setString(1, p.getUniqueId().toString());
					stmt.setString(2, p.getName());
					stmt.setInt(3, 0);
					stmt.setInt(4, 0);
					stmt.setInt(5, 0);
					stmt.setInt(6, 0);
					stmt.executeUpdate();
					
				}
				
			} catch (SQLException e) {
				
	            e.printStackTrace();
	            
	        }
			
		}
		
	}
	
	public void saveAndRemovePlayer(Player p) {
		
		if (isEnabled) {
			
			PlayerData playerData = getCache().get(p.getUniqueId());
			
			try {

				PreparedStatement statement = connection.prepareStatement("UPDATE " + table +
						" SET USERNAME=?, LEVEL=?, EXPERIENCE=?, KILLS=?, DEATHS=? WHERE UUID=?");
				statement.setString(1, playerData.getUsername());
				statement.setInt(2, playerData.getLevel());
				statement.setInt(3, playerData.getExperience());
				statement.setInt(4, playerData.getKills());
				statement.setInt(5, playerData.getDeaths());
				statement.setString(6, p.getUniqueId().toString());
				statement.executeUpdate();

			} catch (SQLException e) {

	            e.printStackTrace();

	        }
			
			cache.remove(p.getUniqueId());
			
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

		PreparedStatement statement = null;
		try {
			
			statement = connection.prepareStatement("DELETE FROM " + table);
			statement.executeUpdate();
		} catch (SQLException e) {
			
			e.printStackTrace();
			
		}
		
		for (String uuid: stats.getKeys(false)) {
			
			String username = resources.getStats().getString("Stats.Players." + uuid + ".Username");
			int level = resources.getStats().getInt("Stats.Players." + uuid + ".Level");
			int experience = resources.getStats().getInt("Stats.Players." + uuid + ".Experience");
			int kills = resources.getStats().getInt("Stats.Players." + uuid + ".Kills");
			int deaths = resources.getStats().getInt("Stats.Players." + uuid + ".Deaths");

			try {
				
				PreparedStatement stmt = connection.prepareStatement("INSERT INTO " + table + " (UUID, USERNAME, LEVEL, EXPERIENCE, KILLS, DEATHS)" +
						" VALUES (?, ?, ?, ?, ?, ?)");
				stmt.setString(1, uuid);
				stmt.setString(2, username);
				stmt.setInt(3, level);
				stmt.setInt(4, experience);
				stmt.setInt(5, kills);
				stmt.setInt(6, deaths);
				stmt.executeUpdate();
				
			} catch (SQLException e) {
				
				e.printStackTrace();
				
			}

		}
		
	}
	
	public boolean isEnabled() { return isEnabled; }
	
	public Map<UUID, PlayerData> getCache() { return cache; }
	
}
