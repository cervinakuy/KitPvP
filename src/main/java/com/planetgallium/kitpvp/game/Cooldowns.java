package com.planetgallium.kitpvp.game;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.Ability;
import com.planetgallium.kitpvp.api.Kit;
import com.planetgallium.kitpvp.util.CacheManager;
import com.planetgallium.kitpvp.util.Cooldown;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class Cooldowns {

	private final Stats stats;
	private final Infobase database;
	
	public Cooldowns(Game plugin, Arena arena) {
		this.stats = arena.getStats();
		this.database = plugin.getDatabase();
	}

	public void setAbilityCooldown(UUID uniqueId, String abilityName) {
		CacheManager.getPlayerAbilityCooldowns(uniqueId).put(abilityName, (System.currentTimeMillis() / 1000));
	}

	public void clearPlayerAbilityCooldowns(UUID uniqueId) {
		CacheManager.getPlayerAbilityCooldowns(uniqueId).clear();
	}

	public void setKitCooldown(UUID uniqueId, String kitName) {
		long timeKitLastUsed = System.currentTimeMillis() / 1000;
		stats.getOrCreateStatsCache(uniqueId).addKitCooldown(kitName, timeKitLastUsed);
	}

	public Cooldown getRemainingCooldown(Player p, Object type) {
		long currentTimeSeconds = (System.currentTimeMillis() / 1000);
		int timeLastUsedSeconds = 0;
		int actionCooldownSeconds = 0;
		Cooldown noCooldown = new Cooldown(0, 0, 0, 0);

		if (type instanceof Kit) {

			Kit kit = (Kit) type;
			if (kit.getCooldown() == null) return noCooldown;

			Object timeLastUsedResult = database.getData(kit.getName() + "_cooldowns", "last_used", p.getUniqueId());
			if (timeLastUsedResult != null) {
				timeLastUsedSeconds = (int) timeLastUsedResult;
			} else {
				return noCooldown;
			}
			actionCooldownSeconds = kit.getCooldown().toSeconds();

		} else if (type instanceof Ability) {

			Ability ability = (Ability) type;
			if (ability.getCooldown() == null ||
					!CacheManager.getPlayerAbilityCooldowns(p.getUniqueId()).containsKey(ability.getName()))
				return noCooldown;

			timeLastUsedSeconds = CacheManager.getPlayerAbilityCooldowns(p.getUniqueId()).get(ability.getName()).intValue();
			actionCooldownSeconds = ability.getCooldown().toSeconds();

		}

		int cooldownRemainingSeconds = (int) (timeLastUsedSeconds + actionCooldownSeconds - currentTimeSeconds);
		return new Cooldown(cooldownRemainingSeconds);
	}
	
}
