package com.planetgallium.kitpvp.game;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.planetgallium.kitpvp.api.PlayerLevelUpEvent;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.XSound;

public class Levels {
	
	private Arena arena;
	private Resources resources;
	
	public Levels(Arena arena, Resources resources) {
		this.arena = arena;
		this.resources = resources;
	}
	
	public void addExperience(Player p, int experience) {
		
		if (resources.getLevels().getBoolean("Levels.Levels.Enabled")) {
			
			arena.getStats().addExperience(p.getUniqueId(), experience);
			
			if (arena.getLevels().getExperience(p.getUniqueId()) >= resources.getLevels().getInt("Levels.Options.Experience-To-Level-Up")) {
				
				arena.getLevels().levelUp(p);
				Bukkit.getPluginManager().callEvent(new PlayerLevelUpEvent(p, getLevel(p.getUniqueId())));
				
			}
			
		}
		
	}
	
	public void removeExperience(Player p, int experience) {
		
		if (resources.getLevels().getBoolean("Levels.Levels.Enabled")) {
		
			arena.getStats().removeExperience(p.getUniqueId(), experience);
			
		}
		
	}
	
	public void levelUp(Player p) {
		
		if (arena.getStats().getLevel(p.getUniqueId()) != resources.getLevels().getInt("Levels.Options.Maximum-Level")) {
			
			arena.getStats().setLevel(p.getUniqueId(), arena.getLevels().getLevel(p.getUniqueId()) + 1);
			arena.getStats().setExperience(p.getUniqueId(), 0);

			String newLevel = String.valueOf(getLevel(p.getUniqueId()));

	        if (resources.getLevels().getBoolean("Levels.Commands.Enabled")) {
	        	Toolkit.runCommands(resources.getLevels(), "Levels", p, "%level%", newLevel);
	        }

	        if (resources.getLevels().contains("Levels.Levels." + newLevel + ".Commands")) {
	        	List<String> commandsList = resources.getLevels().getStringList("Levels.Levels." + newLevel + ".Commands");
				Toolkit.runCommands(p, commandsList, "none", "none");
			}
			
			p.sendMessage(resources.getMessages().getString("Messages.Other.Level").replace("%level%", newLevel));
			p.playSound(p.getLocation(), XSound.ENTITY_PLAYER_LEVELUP.parseSound(), 1, 1);
			
		}
		
	}
	
	public int getLevel(UUID uuid) {
		
		return arena.getStats().getLevel(uuid);
		
	}
	
	public int getExperience(UUID uuid) {
		
		return arena.getStats().getExperience(uuid);
		
	}
	
}
