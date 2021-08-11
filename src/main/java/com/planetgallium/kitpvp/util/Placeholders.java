package com.planetgallium.kitpvp.util;

import com.planetgallium.kitpvp.Game;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import com.planetgallium.kitpvp.game.Arena;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.util.HashMap;
import java.util.Map;

public class Placeholders extends PlaceholderExpansion {

	private final Arena arena;
	private final Resources resources;
	private final Map<String, String> placeholderAPItoBuiltIn;
	
	public Placeholders(Game plugin) {
		this.arena = plugin.getArena();
		this.resources = plugin.getResources();
		this.placeholderAPItoBuiltIn = new HashMap<>();

		placeholderAPItoBuiltIn.put("stats_kills", "%kills%");
		placeholderAPItoBuiltIn.put("stats_deaths", "%deaths%");
		placeholderAPItoBuiltIn.put("stats_kd", "%kd%");
		placeholderAPItoBuiltIn.put("stats_experience", "%xp%");
		placeholderAPItoBuiltIn.put("stats_level", "%level%");
		placeholderAPItoBuiltIn.put("player_killstreak", "%streak%");
		placeholderAPItoBuiltIn.put("player_kit", "%kit%");
		placeholderAPItoBuiltIn.put("max_level", "%max_level%");
		placeholderAPItoBuiltIn.put("max_xp", "%max_xp%");
	}
	
	@Override
	public String onPlaceholderRequest(Player p, String identifier) {

		if (p == null) return null;

		if (identifier.contains("top")) {

			String[] queries = identifier.split("_");
			String topType = queries[1]; // ex: kills
			String topIdentifier = queries[2]; // ex: amount, player
			String possibleTopValue = queries[3]; // 1, 15
			int topValue = 1;

			if (StringUtils.isNumeric(possibleTopValue)) {
				topValue = Integer.parseInt(possibleTopValue);
			} else {
				Toolkit.printToConsole("%prefix% &cFailed to properly parse placeholder, expected number but received: " + possibleTopValue);
			}

			PlayerEntry entry = arena.getLeaderboards().getTopN(topType, topValue);
			boolean isUsernamePlaceholder = topIdentifier.equals("player");

			return isUsernamePlaceholder ? entry.getUsername() : String.valueOf(entry.getData());

		}

		return translatePlaceholderAPIPlaceholders(identifier, p.getName());
		
	}

	public String translatePlaceholderAPIPlaceholders(String placeholderAPIIdentifier, String username) {

		if (!placeholderAPItoBuiltIn.containsKey(placeholderAPIIdentifier)) {
			Toolkit.printToConsole(String.format("&7[&b&lKIT-PVP&7] &cUnknown placeholder identifier [%s]. Please see plugin page.",
												 placeholderAPIIdentifier));
			return "invalid-placeholder";
		}

		return arena.replaceBuiltInPlaceholdersIfPresent(placeholderAPItoBuiltIn.get(placeholderAPIIdentifier), username);

	}
	
	@Override
	public boolean canRegister() { return true; }
	
	@Override
	public String getAuthor() { return "Cervinakuy"; }
	
	@Override
	public String getIdentifier() { return "kitpvp"; }
	
	@Override
	public String getVersion() { return "1.0.0"; }
	
}
