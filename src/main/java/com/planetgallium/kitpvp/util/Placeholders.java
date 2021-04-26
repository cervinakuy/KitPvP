package com.planetgallium.kitpvp.util;

import com.planetgallium.kitpvp.Game;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import com.planetgallium.kitpvp.game.Arena;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class Placeholders extends PlaceholderExpansion {

	private final Arena arena;
	private final Resources resources;
	
	public Placeholders(Game plugin) {
		this.arena = plugin.getArena();
		this.resources = plugin.getResources();
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

		} else {
			String username = p.getName();

			switch (identifier) {
				case "stats_kills": return String.valueOf(arena.getStats().getStat("kills", username));
				case "stats_deaths": return String.valueOf(arena.getStats().getStat("deaths", username));
				case "stats_kdr": return String.valueOf(arena.getStats().getKDRatio(username));
				case "stats_level": return String.valueOf(arena.getStats().getStat("level", username));
				case "stats_experience": return String.valueOf(arena.getStats().getStat("experience", username));
				case "player_killstreak": return String.valueOf(arena.getKillStreaks().getStreak(username));
				case "player_kit":
					if (arena.getKits().hasKit(username)) {
						return arena.getKits().getKitOfPlayer(username).getName();
					}
					return resources.getMessages().getString("Messages.Other.NoKit");
				case "max_level": return String.valueOf(resources.getLevels().getInt("Levels.Options.Maximum-Level"));
				case "max_xp": return String.valueOf(resources.getLevels().getInt("Levels.Options.Experience-To-Level-Up"));
			}
		}

		return null;
		
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
