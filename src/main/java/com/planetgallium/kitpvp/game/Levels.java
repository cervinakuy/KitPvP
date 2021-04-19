package com.planetgallium.kitpvp.game;

import java.util.List;

import com.cryptomorin.xseries.XSound;
import com.planetgallium.kitpvp.util.Resource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.planetgallium.kitpvp.api.PlayerLevelUpEvent;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;

public class Levels {
	
	private final Arena arena;
	private final Resources resources;
	private final Resource levels;
	
	public Levels(Arena arena, Resources resources) {
		this.arena = arena;
		this.resources = resources;
		this.levels = resources.getLevels();
	}
	
	public void addExperience(Player p, int experience) {
		
		if (levels.getBoolean("Levels.Levels.Enabled")) {
			
			arena.getStats().addExperience(p.getUniqueId().toString(), experience);
			
			if (arena.getLevels().getExperience(p.getUniqueId().toString()) >= levels.getInt("Levels.Options.Experience-To-Level-Up")) {

				arena.getLevels().levelUp(p);
				Bukkit.getPluginManager().callEvent(new PlayerLevelUpEvent(p, getLevel(p.getUniqueId().toString())));
				
			}
			
		}
		
	}
	
	public void removeExperience(Player p, int experience) {
		
		if (levels.getBoolean("Levels.Levels.Enabled")) {
		
			arena.getStats().removeExperience(p.getUniqueId().toString(), experience);
			
		}
		
	}
	
	public void levelUp(Player p) {
		
		if (arena.getStats().getLevel(p.getUniqueId().toString()) != levels.getInt("Levels.Options.Maximum-Level")) {
			
			arena.getStats().setLevel(p.getUniqueId().toString(), arena.getLevels().getLevel(p.getUniqueId().toString()) + 1);
			arena.getStats().setExperience(p.getUniqueId().toString(), 0);

			String newLevel = String.valueOf(getLevel(p.getUniqueId().toString()));

			List<String> levelUpCommands = levels.getStringList("Levels.Commands-On-Level-Up");
			Toolkit.runCommands(p, levelUpCommands, "%level%", newLevel);

	        if (levels.contains("Levels.Levels." + newLevel + ".Commands")) {
	        	List<String> commandsList = levels.getStringList("Levels.Levels." + newLevel + ".Commands");
				Toolkit.runCommands(p, commandsList, "%level%", newLevel);
			}
			
			p.sendMessage(resources.getMessages().getString("Messages.Other.Level").replace("%level%", newLevel));
			p.playSound(p.getLocation(), XSound.ENTITY_PLAYER_LEVELUP.parseSound(), 1, 1);
			
		} else {

			arena.getStats().setExperience(p.getUniqueId().toString(), 0);

		}
		
	}
	
	public int getLevel(String uuid) {
		return arena.getStats().getLevel(uuid);
	}
	
	public int getExperience(String uuid) {
		return arena.getStats().getExperience(uuid);
	}
	
}
