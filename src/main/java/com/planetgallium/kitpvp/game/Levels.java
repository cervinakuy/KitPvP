package com.planetgallium.kitpvp.game;

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
			
			if (arena.getLevels().getExperience(p.getUniqueId()) >= resources.getLevels().getInt("Levels.General.Experience.Levelup")) {
				
				arena.getLevels().levelUp(p);
				Bukkit.getPluginManager().callEvent(new PlayerLevelUpEvent(p, this.getLevel(p.getUniqueId())));
				
			}
			
		}
		
	}
	
	public void removeExperience(Player p, int experience) {
		
		if (resources.getLevels().getBoolean("Levels.Levels.Enabled")) {
		
			arena.getStats().removeExperience(p.getUniqueId(), experience);
			
		}
		
	}
	
	public void levelUp(Player p) {
		
		if (arena.getStats().getLevel(p.getUniqueId()) != resources.getLevels().getInt("Levels.General.Level.Maximum")) {
			
			arena.getStats().setLevel(p.getUniqueId(), arena.getLevels().getLevel(p.getUniqueId()) + 1);
			arena.getStats().setExperience(p.getUniqueId(), 0);
			
	        if (resources.getLevels().getBoolean("Levels.Commands.Enabled")) {
				
	        	Toolkit.runCommands(resources.getLevels(), "Levels", p, "%level%", String.valueOf(this.getLevel(p.getUniqueId())));
				
	        }
			
			p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Other.Level").replace("%level%", String.valueOf(arena.getLevels().getLevel(p.getUniqueId())))));
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
