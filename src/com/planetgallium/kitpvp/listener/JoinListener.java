package com.planetgallium.kitpvp.listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.game.PlayerData;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Toolkit;

public class JoinListener implements Listener {

	private Arena arena;
	
	public JoinListener(Arena arena) {
		this.arena = arena;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		
		Player p = e.getPlayer();
		
		if (Game.storageType.equalsIgnoreCase("mysql")) {
	        try {
	            Statement statement = Game.getConnection().createStatement();
	            String tableName = Config.getC().getString("MySQL.table");
	            ResultSet rs = statement.executeQuery("SELECT * FROM " + tableName + " WHERE UUID='" + p.getUniqueId() + "'");
	            boolean hasPlayerData = false;
	            while (rs.next()) {
	                hasPlayerData = true;
	                String username = rs.getString("USERNAME");
	                int level = rs.getInt("LEVEL");
	                int experience = rs.getInt("EXPERIENCE");
	                int kills = rs.getInt("KILLS");
	                int deaths = rs.getInt("DEATHS");
	                PlayerData playerData = new PlayerData(username, level, experience, kills, deaths);
	                Game.playerCache.put(p.getUniqueId(), playerData);
	            }
	            if (!hasPlayerData) {
	                arena.getStats().createPlayer(p.getName(), p.getUniqueId());
	                String sqlStatement = "INSERT INTO " + tableName + " (UUID, USERNAME, LEVEL, EXPERIENCE, KILLS, DEATHS) VALUES ('" + p.getUniqueId() + "', '" + p.getName() + "', 0, 0, 0, 0)";
	                statement.executeUpdate(sqlStatement);
	            }
	        } catch (SQLException e2) {
	            e2.printStackTrace();
	        }
		}
		
		// Update checker
		if (Game.getInstance().needsUpdate()) {
			
			if (p.isOp()) {
				
				p.sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aAn update was found: v" + Game.getInstance().getUpdateVersion() + " https://www.spigotmc.org/resources/27107/"));
				
			}
			
		}
		
		if (Toolkit.inArena(p)) {
			
			if (Config.getB("Arena.ClearInventoryOnJoin")) {
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
			}
			
			arena.addPlayer(p);
			
		}
		
		arena.getStats().createPlayer(p.getName(), p.getUniqueId());
		
		if (p.getName().equals("cervinakuy")) {
			
			e.setJoinMessage(Config.tr("&7[&b&lKIT-PVP&7] &7The Developer of &bKitPvP &7has joined the server."));
			
		}
		
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {
		
		Player p = e.getPlayer();
			
		if (Toolkit.inArena(p)) {

			if (Config.getB("Arena.ClearInventoryOnJoin")) {
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
			}
			
			arena.addPlayer(p);
			
		} else if (Toolkit.inArena(e.getFrom())) {
			// if they left from the kitpvp arena
			
			if (Config.getB("Arena.ClearInventoryOnLeave")) {
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
			}
			
			arena.removePlayer(p);
			
		}
		
	}
	
}
