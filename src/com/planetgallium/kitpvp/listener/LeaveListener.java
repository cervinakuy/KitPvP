package com.planetgallium.kitpvp.listener;

import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.PlayerData;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resources;

public class LeaveListener implements Listener {

	@SuppressWarnings("unused")
	private Resources resources;
	
	public LeaveListener(Resources resources) {
		this.resources = resources;
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		
		Player p = e.getPlayer();
		
		if (Game.getInstance().getArena().isPlayer(p.getName()) || Game.getInstance().getArena().isSpectator(p.getName())) {
			
			Game.getInstance().getArena().deletePlayer(p);
			
		}
		
		Game.getInstance().getArena().removeUser(p.getName());
		
		if (Game.storageType.equalsIgnoreCase("mysql")) {
	        PlayerData playerData = Game.playerCache.get(p.getUniqueId());
	        try {
	            Statement statement = Game.getConnection().createStatement();
	            String tableName = Config.getC().getString("MySQL.table");
	            String sqlStatement = "UPDATE " + tableName + " SET USERNAME='"+playerData.getUsername()+"', LEVEL="+playerData.getLevel()+", EXPERIENCE="
	                    + playerData.getExperience()+", KILLS="+playerData.getKills()+", DEATHS="+playerData.getDeaths()+" WHERE UUID='"
	                    + p.getUniqueId() + "'";
	            statement.executeUpdate(sqlStatement);
	        } catch (SQLException e2) {
	            e2.printStackTrace();
	        }
	        Game.playerCache.remove(p.getUniqueId());
		}
		
	}
	
}
