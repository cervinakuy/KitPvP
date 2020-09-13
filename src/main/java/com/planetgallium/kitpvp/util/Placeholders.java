package com.planetgallium.kitpvp.util;

import com.planetgallium.kitpvp.Game;
import org.bukkit.entity.Player;
import com.planetgallium.kitpvp.game.Arena;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class Placeholders extends PlaceholderExpansion {

	private Arena arena;
	private Resources resources;
	
	public Placeholders(Game plugin) {
		this.arena = plugin.getArena();
		this.resources = plugin.getResources();
	}
	
	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		
		if (p != null) {
			
			switch (identifier) {

				case "stats_kills": return String.valueOf(arena.getStats().getKills(p.getUniqueId()));
				case "stats_deaths": return String.valueOf(arena.getStats().getDeaths(p.getUniqueId()));
				case "stats_kdr": return String.valueOf(arena.getStats().getKDRatio(p.getUniqueId()));
				case "stats_level": return String.valueOf(arena.getLevels().getLevel(p.getUniqueId()));
				case "stats_experience": return String.valueOf(arena.getLevels().getExperience(p.getUniqueId()));
				case "player_killstreak": return String.valueOf(arena.getKillStreaks().getStreak(p.getName()));
				case "player_kit": return arena.getKits().getKitOfPlayer(p.getName()).getName();
				case "max_level": return String.valueOf(resources.getLevels().getInt("Levels.General.Level.Maximum"));
				case "max_xp": return String.valueOf(resources.getLevels().getInt("Levels.General.Experience.Levelup"));
			
			}
			
		}
	
		return null;
		
	}
	
	@Override
	public boolean canRegister() {
		
		return true;
		
	}
	
	@Override
	public String getAuthor() {
		
		return "Cervinakuy";
		
	}
	
	@Override
	public String getIdentifier() {
		
		return "kitpvp";
		
	}
	
	@Override
	public String getPlugin() {
		
		return null;
		
	}
	
	@Override
	public String getVersion() {
		
		return "1.0.0";
		
	}
	
}
