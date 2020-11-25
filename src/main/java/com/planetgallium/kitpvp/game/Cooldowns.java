package com.planetgallium.kitpvp.game;

import java.util.UUID;

import com.planetgallium.kitpvp.api.Kit;
import com.planetgallium.kitpvp.util.Cooldown;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Resources;

public class Cooldowns {

	private Arena arena;
	private Resource stats;
	
	public Cooldowns(Arena arena, Resources resources) {
		this.arena = arena;
		this.stats = resources.getStats();
	}
	
	public void setCooldown(UUID uuid, String kit) {
		
		// TODO: MySQL storing
		stats.set("Stats.Players." + uuid + ".Cooldowns." + kit, (System.currentTimeMillis() / 1000));
		stats.save();
		
	}
	
	public boolean isOnCooldown(UUID uuid, String kit) {
	
		// TODO: MySQL usage
		if (stats.contains("Stats.Players." + uuid + ".Cooldowns." + kit)) {
			
			if ((stats.getInt("Stats.Players." + uuid + ".Cooldowns." + kit) + cooldownToSeconds(kit)) <= (System.currentTimeMillis() / 1000)) {
				
				stats.set("Stats.Players." + uuid + ".Cooldowns." + kit, null);
				stats.save();
				return false;
				
			}
			
			return true;
			
		}
		
		return false;
		
	}

	public int cooldownToSeconds(String kitName) {

		Kit kit = arena.getKits().getKitByName(kitName);
		Cooldown cooldown = kit.getCooldown();

		return (cooldown.getDays() * 86400) + (cooldown.getHours() * 3600) + (cooldown.getMinutes() * 60) + (cooldown.getSeconds());
		
	}
	
	public String getFormattedCooldown(UUID uuid, String kit) {
		
		int cooldownSeconds = (int) ((stats.getInt("Stats.Players." + uuid + ".Cooldowns." + kit) + cooldownToSeconds(kit)) - (System.currentTimeMillis() / 1000));
		
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
