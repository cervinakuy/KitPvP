package com.planetgallium.kitpvp.game;

import java.util.UUID;

import com.planetgallium.kitpvp.api.Ability;
import com.planetgallium.kitpvp.api.Kit;
import com.planetgallium.kitpvp.util.CacheManager;
import com.planetgallium.kitpvp.util.Cooldown;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Resources;
import org.bukkit.entity.Player;

public class Cooldowns {

	private Arena arena;
	private Resource stats;
	
	public Cooldowns(Arena arena, Resources resources) {
		this.arena = arena;
		this.stats = resources.getStats();
	}

	public void setAbilityCooldown(String playerName, String abilityName) {

		CacheManager.getPlayerAbilityCooldowns(playerName).put(abilityName, (System.currentTimeMillis() / 1000));

	}

	public void setCooldown(UUID uuid, String kit) {
		
		// TODO: MySQL storing
		stats.set("Stats.Players." + uuid + ".Cooldowns." + kit, (System.currentTimeMillis() / 1000));
		stats.save();
		
	}

	public boolean isOnCooldown(Player p, Object type) {

		long currentTimeSeconds = (System.currentTimeMillis() / 1000);
		int timeLastUsedSeconds = 0;
		int cooldownSeconds = 0;

		if (type instanceof Kit) {

			Kit kit = (Kit) type;
			if (kit.getCooldown() == null) return false;
			timeLastUsedSeconds = stats.getInt("Stats.Players." + p.getUniqueId() + ".Cooldowns." + kit.getName());
			cooldownSeconds = kit.getCooldown().toSeconds();

		} else if (type instanceof Ability) {

			Ability ability = (Ability) type;
			if (ability.getCooldown() == null ||
					!CacheManager.getPlayerAbilityCooldowns(p.getName()).containsKey(ability.getName()))
				return false;
			timeLastUsedSeconds = CacheManager.getPlayerAbilityCooldowns(p.getName()).get(ability.getName()).intValue();
			cooldownSeconds = ability.getCooldown().toSeconds();

		} else {
			return false;
		}

		return timeLastUsedSeconds + cooldownSeconds >= currentTimeSeconds;

	}

	public String getFormattedCooldown(int useTimeSeconds, int cooldownSeconds) {

		int timeRemainingSeconds = (int) (useTimeSeconds + cooldownSeconds - (System.currentTimeMillis() / 1000));

		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;

		if (timeRemainingSeconds / 86400 > 0) {
			days = timeRemainingSeconds / 86400;
			timeRemainingSeconds -= (days * 86400);
		}

		if (timeRemainingSeconds / 3600 > 0) {
			hours = timeRemainingSeconds / 3600;
			timeRemainingSeconds -= (hours * 3600);
		}

		if (timeRemainingSeconds / 60 > 0) {
			minutes = timeRemainingSeconds / 60;
			timeRemainingSeconds -= (minutes * 60);
		}

		if (timeRemainingSeconds > 0) {
			seconds = timeRemainingSeconds;
		}

		return new Cooldown(days, hours, minutes, seconds).formatted(false);

	}
	
}
