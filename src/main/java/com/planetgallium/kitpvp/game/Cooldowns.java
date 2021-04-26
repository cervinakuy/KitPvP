package com.planetgallium.kitpvp.game;

import java.util.UUID;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.Ability;
import com.planetgallium.kitpvp.api.Kit;
import com.planetgallium.kitpvp.util.CacheManager;
import com.planetgallium.kitpvp.util.Cooldown;
import com.zp4rker.localdb.DataType;
import org.bukkit.entity.Player;

public class Cooldowns {

	private Infobase database;
	
	public Cooldowns(Game plugin) {
		this.database = plugin.getDatabase();
	}

	public void setAbilityCooldown(String playerName, String abilityName) {
		CacheManager.getPlayerAbilityCooldowns(playerName).put(abilityName, (System.currentTimeMillis() / 1000));
	}

	public void setKitCooldown(String username, String kitName) {
		database.setData(kitName + "_cooldowns", "last_used", (System.currentTimeMillis() / 1000), DataType.INTEGER, username);
	}

	public Cooldown getRemainingCooldown(Player p, Object type) {

		long currentTimeSeconds = (System.currentTimeMillis() / 1000);
		int timeLastUsedSeconds = 0;
		int actionCooldownSeconds = 0;
		Cooldown noCooldown = new Cooldown(0, 0, 0, 0);

		if (type instanceof Kit) {

			Kit kit = (Kit) type;
			if (kit.getCooldown() == null) return noCooldown;

			Object timeLastUsedResult = database.getData(kit.getName() + "_cooldowns", "last_used", p.getName());
			if (timeLastUsedResult != null) {
				timeLastUsedSeconds = (int) timeLastUsedResult;
			} else {
				return noCooldown;
			}
			actionCooldownSeconds = kit.getCooldown().toSeconds();

		} else if (type instanceof Ability) {

			Ability ability = (Ability) type;
			if (ability.getCooldown() == null ||
					!CacheManager.getPlayerAbilityCooldowns(p.getName()).containsKey(ability.getName()))
				return noCooldown;

			timeLastUsedSeconds = CacheManager.getPlayerAbilityCooldowns(p.getName()).get(ability.getName()).intValue();
			actionCooldownSeconds = ability.getCooldown().toSeconds();

		}

		int cooldownRemainingSeconds = (int) (timeLastUsedSeconds + actionCooldownSeconds - currentTimeSeconds);
		return new Cooldown(cooldownRemainingSeconds);

	}
	
}
