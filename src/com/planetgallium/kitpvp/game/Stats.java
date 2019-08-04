package com.planetgallium.kitpvp.game;

import java.util.UUID;

import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;

public class Stats {
	
	private Resources resources;
	
	public Stats(Resources resources) {
		this.resources = resources;
	}
	
	public void createPlayer(String username, UUID uuid) {
		
		if (!resources.getStats().contains("Stats.Players." + uuid + ".Username")) {
			
			resources.getStats().set("Stats.Players." + uuid + ".Username", username);
			resources.getStats().set("Stats.Players." + uuid + ".Level", 0);
			resources.getStats().set("Stats.Players." + uuid + ".Experience", 0);
			resources.getStats().set("Stats.Players." + uuid + ".Kills", 0);
			resources.getStats().set("Stats.Players." + uuid + ".Deaths", 0);
			
			resources.save();
			
		}
		
	}
	
	public void addKill(UUID uuid) {
		
		if (resources.getStats().contains("Stats.Players." + uuid + ".Kills")) {
			
			resources.getStats().set("Stats.Players." + uuid + ".Kills", getKills(uuid) + 1);
			resources.save();
			
		}
		
	}
	
	public void addDeath(UUID uuid) {
		
		if (resources.getStats().contains("Stats.Players." + uuid + ".Deaths")) {
			
			resources.getStats().set("Stats.Players." + uuid + ".Deaths", getDeaths(uuid) + 1);
			resources.save();
			
		}
		
	}
	
	public void addExperience(UUID uuid, int experience) {
		
		if (resources.getStats().contains("Stats.Players." + uuid + ".Experience")) {
			
			resources.getStats().set("Stats.Players." + uuid + ".Experience", getExperience(uuid) + experience);
			resources.save();
			
		}
		
	}
	
	public void removeExperience(UUID uuid, int experience) {
		
		if (resources.getStats().contains("Stats.Players." + uuid + ".Experience")) {
			
			if (resources.getStats().getInt("Stats.Players." + uuid + ".Experience") > experience) {
				
				resources.getStats().set("Stats.Players." + uuid + ".Experience", getExperience(uuid) - experience);
				resources.save();
				
			}
			
		}
		
	}
	
	public void setLevel(UUID uuid, int level) {
		
		if (resources.getStats().contains("Stats.Players." + uuid + ".Level")) {
			
			resources.getStats().set("Stats.Players." + uuid + ".Level", level);
			resources.save();
			
		}
		
	}
	
	public void setExperience(UUID uuid, int experience) {
		
		if (resources.getStats().contains("Stats.Players." + uuid + ".Experience")) {
			
			resources.getStats().set("Stats.Players." + uuid + ".Experience", experience);
			resources.save();
			
		}
		
	}
	
	
	public int getKills(UUID uuid) {
		
		if (resources.getStats().contains("Stats.Players." + uuid + ".Kills")) {
			
			return resources.getStats().getInt("Stats.Players." + uuid + ".Kills");
			
		}
		
		return 0;
		
	}
	
	public int getDeaths(UUID uuid) {
		
		if (resources.getStats().contains("Stats.Players." + uuid + ".Deaths")) {
			
			return resources.getStats().getInt("Stats.Players." + uuid + ".Deaths");
			
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
		
		if (resources.getStats().contains("Stats.Players." + uuid + ".Experience")) {
			
			return resources.getStats().getInt("Stats.Players." + uuid + ".Experience");
			
		}
		
		return 0;
		
	}
	
	public int getLevel(UUID uuid) {
		
		if (resources.getStats().contains("Stats.Players." + uuid + ".Level")) {
			
			return resources.getStats().getInt("Stats.Players." + uuid + ".Level");
			
		}
		
		return 0;
		
	}
	
}
