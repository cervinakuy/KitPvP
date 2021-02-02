package com.planetgallium.kitpvp.game;

import java.util.UUID;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;

public class Stats {
	
	private Game game;
	private Resources resources;
	private Resource stats;
	
	public Stats(Game game, Resources resources) {
		this.game = game;
		this.resources = resources;
		this.stats = resources.getStats();
	}
	
	public void createPlayer(String username, UUID uuid) {
		
		if (!game.getDatabase().isEnabled()) {
			
			if (!stats.contains("Stats.Players." + uuid + ".Username")) {
				
				stats.set("Stats.Players." + uuid + ".Username", username);
				stats.set("Stats.Players." + uuid + ".Level", resources.getLevels().getInt("Levels.Options.Minimum-Level"));
				stats.set("Stats.Players." + uuid + ".Experience", 0);
				stats.set("Stats.Players." + uuid + ".Kills", 0);
				stats.set("Stats.Players." + uuid + ".Deaths", 0);
				
				resources.save();
				
			}
			
		} else {
			
			if (!game.getDatabase().getCache().containsKey(uuid) || game.getDatabase().getCache().get(uuid) == null) {
				
				PlayerData playerData = new PlayerData(username, 0, 0, 0, 0);
				game.getDatabase().getCache().put(uuid, playerData);
				
			}
			
		}
		
	}
	
	public void addKill(UUID uuid) {
		
		if (!game.getDatabase().isEnabled()) {
			
			if (stats.contains("Stats.Players." + uuid + ".Kills")) {
				
				stats.set("Stats.Players." + uuid + ".Kills", getKills(uuid) + 1);
				resources.save();
				
			}
			
		} else {
			
			PlayerData playerData = game.getDatabase().getCache().get(uuid);
			playerData.addKills(1);
			
		}
		
	}
	
	public void addDeath(UUID uuid) {
		
		if (!game.getDatabase().isEnabled()) {
			
			if (stats.contains("Stats.Players." + uuid + ".Deaths")) {
				
				stats.set("Stats.Players." + uuid + ".Deaths", getDeaths(uuid) + 1);
				resources.save();
				
			}
			
		} else {
			
			PlayerData playerData = game.getDatabase().getCache().get(uuid);
			playerData.addDeaths(1);
			
		}
		
	}
	
	public void addExperience(UUID uuid, int experience) {
		
		if (!game.getDatabase().isEnabled()) {
			
			if (stats.contains("Stats.Players." + uuid + ".Experience")) {
				
				stats.set("Stats.Players." + uuid + ".Experience", getExperience(uuid) + experience);
				resources.save();
				
			}
			
		} else {
			
			PlayerData playerData = game.getDatabase().getCache().get(uuid);
			playerData.setExperience(getExperience(uuid) + experience);
			
		}
		
	}
	
	public void removeExperience(UUID uuid, int experience) {
		
		if (!game.getDatabase().isEnabled()) {
			
			if (stats.contains("Stats.Players." + uuid + ".Experience")) {
				
				if (stats.getInt("Stats.Players." + uuid + ".Experience") > experience) {
					
					stats.set("Stats.Players." + uuid + ".Experience", getExperience(uuid) - experience);
					resources.save();
					
				}
				
			}
			
		} else {
			
			PlayerData playerData = game.getDatabase().getCache().get(uuid);
			
			if (playerData.getExperience() > experience) {
				
				playerData.setExperience(getExperience(uuid) - experience);
				
			}
			
		}
		
	}
	
	public void setLevel(UUID uuid, int level) {
		
		if (!game.getDatabase().isEnabled()) {
			
			if (stats.contains("Stats.Players." + uuid + ".Level")) {
				
				stats.set("Stats.Players." + uuid + ".Level", level);
				resources.save();
				
			}
			
		} else {
			
			PlayerData playerData = game.getDatabase().getCache().get(uuid);
			playerData.setLevel(level);
			
		}
		
	}
	
	public void setExperience(UUID uuid, int experience) {
		
		if (!game.getDatabase().isEnabled()) {
			
			if (stats.contains("Stats.Players." + uuid + ".Experience")) {
				
				stats.set("Stats.Players." + uuid + ".Experience", experience);
				resources.save();
				
			}
			
		} else {
			
			PlayerData playerData = game.getDatabase().getCache().get(uuid);
			playerData.setExperience(experience);
			
		}
		
	}
	
	public int getKills(UUID uuid) {
		
		if (!game.getDatabase().isEnabled()) {
			
			if (stats.contains("Stats.Players." + uuid + ".Kills")) {
				
				return stats.getInt("Stats.Players." + uuid + ".Kills");
				
			}
			
		} else {
			
			PlayerData playerData = game.getDatabase().getCache().get(uuid);
			return playerData.getKills();
			
		}
		
		return 0;
		
	}
	
	public int getDeaths(UUID uuid) {
		
		if (!game.getDatabase().isEnabled()) {
			
			if (stats.contains("Stats.Players." + uuid + ".Deaths")) {
				
				return stats.getInt("Stats.Players." + uuid + ".Deaths");
				
			}
			
		} else {
			
			PlayerData playerData = game.getDatabase().getCache().get(uuid);
			return playerData.getDeaths();
			
		}
		
		return 0;
		
	}
	
	public double getKDRatio(UUID uuid) {
		
		if (getDeaths(uuid) != 0) {
			
			double divided = (double) getKills(uuid) / getDeaths(uuid);
			return Toolkit.round(divided, 2);
			
		}
		
		return 0.00;
		
	}
	
	public int getExperience(UUID uuid) {
		
		if (!game.getDatabase().isEnabled()) {
			
			if (stats.contains("Stats.Players." + uuid + ".Experience")) {
				
				return stats.getInt("Stats.Players." + uuid + ".Experience");
				
			}
			
		} else {
			
			PlayerData playerData = game.getDatabase().getCache().get(uuid);
			return playerData.getExperience();
			
		}
		
		return 0;
		
	}
	
	public int getLevel(UUID uuid) {
		
		if (!game.getDatabase().isEnabled()) {
			
			if (stats.contains("Stats.Players." + uuid + ".Level")) {
				
				return stats.getInt("Stats.Players." + uuid + ".Level");
				
			}
			
		} else {
			
			PlayerData playerData = game.getDatabase().getCache().get(uuid);
			return playerData.getLevel();
			
		}
		
		return 0;
		
	}
	
}
