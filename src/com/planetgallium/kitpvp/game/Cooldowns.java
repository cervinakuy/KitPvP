package com.planetgallium.kitpvp.game;

import java.util.UUID;

import com.planetgallium.kitpvp.util.Cooldown;
import com.planetgallium.kitpvp.util.Resources;

public class Cooldowns {

	private Arena arena;
	private Resources resources;
	
	public Cooldowns(Arena arena, Resources resources) {
		this.arena = arena;
		this.resources = resources;
	}
	
	public void setCooldown(UUID uuid, String kit) {
		
		// TODO: MySQL storing
		resources.getStats().set("Stats.Players." + uuid + ".Cooldowns." + kit, (System.currentTimeMillis() / 1000));
		resources.getStats().save();
		
	}
	
	public boolean isOnCooldown(UUID uuid, String kit) {
	
		// TODO: MySQL usage
		if (resources.getStats().contains("Stats.Players." + uuid + ".Cooldowns." + kit)) {
			
			if ((resources.getStats().getInt("Stats.Players." + uuid + ".Cooldowns." + kit) + cooldownToSeconds(kit)) <= (System.currentTimeMillis() / 1000)) {
				
				resources.getStats().set("Stats.Players." + uuid + ".Cooldowns." + kit, null);
				resources.getStats().save();
				return false;
				
			}
			
			return true;
			
		}
		
		return false;
		
	}
	
	public Cooldown getCooldown(String kit) {
		
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		
		if (arena.getKits().isKit(kit)) {
			
			String[] units = resources.getKits(kit).getString("Kit.Cooldown").split(":");
			
			for (int i = 0; i < units.length; i++) {
				
				if (units[i].toUpperCase().endsWith("D")) {
					
					days = Integer.valueOf(units[i].split("D")[0]);
					
				} else if (units[i].toUpperCase().endsWith("H")) {
					
					hours = Integer.valueOf(units[i].split("H")[0]);
					
				} else if (units[i].toUpperCase().endsWith("M")) {
					
					minutes = Integer.valueOf(units[i].split("M")[0]);
					
				} else if (units[i].toUpperCase().endsWith("S")) {
					
					seconds = Integer.valueOf(units[i].split("S")[0]);
					
				}
				
			}
			
		}
		
		return new Cooldown(days, hours, minutes, seconds);
		
	}
	
	public int cooldownToSeconds(String kit) {
		
		Cooldown cooldown = getCooldown(kit);
		
		return (cooldown.getDays() * 86400) + (cooldown.getHours() * 3600) + (cooldown.getMinutes() * 60) + (cooldown.getSeconds());
		
	}
	
	public String getFormattedCooldown(UUID uuid, String kit) {
		
		int cooldownSeconds = (int) ((resources.getStats().getInt("Stats.Players." + uuid + ".Cooldowns." + kit) + cooldownToSeconds(kit)) - (System.currentTimeMillis() / 1000));
		
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		
		if (cooldownSeconds / 86400 > 0) {
			
			days = cooldownSeconds / 86400;
			cooldownSeconds -= (days * 86400);
			
		}
		
		if (cooldownSeconds / 3600 > 0) {
			
			hours = cooldownSeconds / 3600;
			cooldownSeconds -= (hours * 3600);
			
		}
		
		if (cooldownSeconds / 60 > 0) {
			
			minutes = cooldownSeconds / 60;
			cooldownSeconds -= (minutes * 60);
			
		}
		
		if (cooldownSeconds > 0) {
			
			seconds = cooldownSeconds;
			
		}
		
		return new Cooldown(days, hours, minutes, seconds).toString();
		
	}
	
}
