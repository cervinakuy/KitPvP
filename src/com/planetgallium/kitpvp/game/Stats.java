package com.planetgallium.kitpvp.game;

import java.util.UUID;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;

public class Stats {
	
	private Resources resources;
	
	public Stats(Resources resources) {
		this.resources = resources;
	}
	
	public void createPlayer(String username, UUID uuid) {
		if (Game.storageType.equalsIgnoreCase("yaml")) {
			if (!resources.getStats().contains("Stats.Players." + uuid + ".Username")) {
				
				resources.getStats().set("Stats.Players." + uuid + ".Username", username);
				resources.getStats().set("Stats.Players." + uuid + ".Level", 0);
				resources.getStats().set("Stats.Players." + uuid + ".Experience", 0);
				resources.getStats().set("Stats.Players." + uuid + ".Kills", 0);
				resources.getStats().set("Stats.Players." + uuid + ".Deaths", 0);
				
				resources.save();
				
			}
		
		} else {
			if (Game.playerCache.containsKey(uuid) || Game.playerCache.get(uuid) == null) {
				PlayerData playerData = new PlayerData(username, 0, 0, 0, 0);
				Game.playerCache.put(uuid, playerData);
			}
		}
		
	}
	
	public void addKill(UUID uuid) {
		
		if (Game.storageType.equalsIgnoreCase("yaml")) {
			if (resources.getStats().contains("Stats.Players." + uuid + ".Kills")) {
				
				resources.getStats().set("Stats.Players." + uuid + ".Kills", getKills(uuid) + 1);
				resources.save();
				
			}
		} else {
			PlayerData playerData = Game.playerCache.get(uuid);
			playerData.setKills(playerData.getKills() + 1);
		}
		
	}
	
	public void addDeath(UUID uuid) {
		
		if (Game.storageType.equalsIgnoreCase("yaml")) {
			if (resources.getStats().contains("Stats.Players." + uuid + ".Deaths")) {
				
				resources.getStats().set("Stats.Players." + uuid + ".Deaths", getDeaths(uuid) + 1);
				resources.save();
				
			}
		} else {
			PlayerData playerData = Game.playerCache.get(uuid);
			playerData.setDeaths(playerData.getDeaths() + 1);
		}
		
	}
	
	public void addExperience(UUID uuid, int experience) {
		if (Game.storageType.equalsIgnoreCase("yaml")) {
			if (resources.getStats().contains("Stats.Players." + uuid + ".Experience")) {
				
				resources.getStats().set("Stats.Players." + uuid + ".Experience", getExperience(uuid) + experience);
				resources.save();
				
			}
		
		} else {
			PlayerData playerData = Game.playerCache.get(uuid);
			playerData.setExperience(playerData.getExperience() + experience);
		}
		
	}
	
	public void removeExperience(UUID uuid, int experience) {
		
		if (Game.storageType.equalsIgnoreCase("yaml")) {
			if (resources.getStats().contains("Stats.Players." + uuid + ".Experience")) {
				
				if (resources.getStats().getInt("Stats.Players." + uuid + ".Experience") > experience) {
					
					resources.getStats().set("Stats.Players." + uuid + ".Experience", getExperience(uuid) - experience);
					resources.save();
					
				}
				
			}
		} else {
			PlayerData playerData = Game.playerCache.get(uuid);
			
			if (playerData.getExperience() > experience) {
				
				playerData.setExperience(playerData.getExperience() - experience);
			}
		}
		
	}
	
	public void setLevel(UUID uuid, int level) {
		
		if (Game.storageType.equalsIgnoreCase("yaml")) {
			if (resources.getStats().contains("Stats.Players." + uuid + ".Level")) {
				
				resources.getStats().set("Stats.Players." + uuid + ".Level", level);
				resources.save();
				
			}
		} else {
			PlayerData playerData = Game.playerCache.get(uuid);
			playerData.setLevel(level);
		}
		
	}
	
	public void setExperience(UUID uuid, int experience) {
		
		if (Game.storageType.equalsIgnoreCase("yaml")) {
			if (resources.getStats().contains("Stats.Players." + uuid + ".Experience")) {
				
				resources.getStats().set("Stats.Players." + uuid + ".Experience", experience);
				resources.save();
				
			}
		} else {
			PlayerData playerData = Game.playerCache.get(uuid);
			playerData.setExperience(experience);
		}
	}
	
	
	public int getKills(UUID uuid) {
		
		if (Game.storageType.equalsIgnoreCase("yaml")) {
			if (resources.getStats().contains("Stats.Players." + uuid + ".Kills")) {
				
				return resources.getStats().getInt("Stats.Players." + uuid + ".Kills");
				
			}
		} else {
			PlayerData playerData = Game.playerCache.get(uuid);
			return playerData.getKills();
		}
		return 0;
		
	}
	
	public int getDeaths(UUID uuid) {
		
		if (Game.storageType.equalsIgnoreCase("yaml")) {
			if (resources.getStats().contains("Stats.Players." + uuid + ".Deaths")) {
				
				return resources.getStats().getInt("Stats.Players." + uuid + ".Deaths");
				
			}
		} else {
			PlayerData playerData = Game.playerCache.get(uuid);
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
		
		if (Game.storageType.equalsIgnoreCase("yaml")) {
			if (resources.getStats().contains("Stats.Players." + uuid + ".Experience")) {
				
				return resources.getStats().getInt("Stats.Players." + uuid + ".Experience");
				
			}
		} else {
			PlayerData playerData = Game.playerCache.get(uuid);
			return playerData.getExperience();
		}
		return 0;
		
	}
	
	public int getLevel(UUID uuid) {
		
		if (Game.storageType.equalsIgnoreCase("yaml")) {
			if (resources.getStats().contains("Stats.Players." + uuid + ".Level")) {
				
				return resources.getStats().getInt("Stats.Players." + uuid + ".Level");
				
			}
		} else {
			PlayerData playerData = Game.playerCache.get(uuid);
			return playerData.getLevel();
		}
		
		return 0;
		
	}
	
}
