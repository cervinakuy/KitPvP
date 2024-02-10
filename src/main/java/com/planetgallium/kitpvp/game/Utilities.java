package com.planetgallium.kitpvp.game;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.WorldGuardAPI;
import com.planetgallium.kitpvp.util.WorldGuardFlag;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class Utilities {

    private final Game plugin;
    private final Arena arena;
    private final Resources resources;

    public Utilities(Game plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
        this.resources = plugin.getResources();
    }

    public String getPlayerLevelPrefix(String username) {
        String playerLevel = String.valueOf(arena.getStats().getStat("level", username));
        return resources.getLevels().fetchString("Levels.Levels." + playerLevel + ".Prefix")
                .replace("%level%", playerLevel);
    }

    public String addPlaceholdersIfPossible(Player p, String text) {
        if (plugin.hasPlaceholderAPI()) {
            text = PlaceholderAPI.setPlaceholders(p, text);
        }

        return replaceBuiltInPlaceholdersIfPresent(text, p.getName());
    }

    public String replaceBuiltInPlaceholdersIfPresent(String s, String username) {
        // The reason I'm doing all these if statements rather than a more concise code solution is to reduce
        // the amount of data that is unnecessarily fetched (ex by using .replace) to improve performance
        // no longer constantly fetching stats from database for EACH line of scoreboard on update and player join

        if (s.contains("%streak%")) {
            s = s.replace("%streak%", String.valueOf(arena.getKillStreaks().getStreak(username)));
        }

        if (s.contains("%player%")) {
            s = s.replace("%player%", username);
        }

        if (s.contains("%xp%")) {
            s = s.replace("%xp%", String.valueOf(arena.getStats().getStat("experience", username)));
        }

        if (s.contains("%level%")) {
            s = s.replace("%level%", String.valueOf(arena.getStats().getStat("level", username)));
        }

        if (s.contains("%level_prefix%")) {
            String levelPrefix = getPlayerLevelPrefix(username);
            s = s.replace("%level_prefix%", levelPrefix);
        }

        if (s.contains("%max_xp%")) {
            s = s.replace("%max_xp%", String.valueOf(arena.getStats().getRegularOrRelativeNeededExperience(username)));
        }

        if (s.contains("%max_level%")) {
            s = s.replace("%max_level%",
                    String.valueOf(resources.getLevels().getInt("Levels.Options.Maximum-Level")));
        }

        if (s.contains("%kd%")) {
            s = s.replace("%kd%", String.valueOf(arena.getStats().getKDRatio(username)));
        }

        if (s.contains("%kills%")) {
            s = s.replace("%kills%", String.valueOf(arena.getStats().getStat("kills", username)));
        }

        if (s.contains("%deaths%")) {
            s = s.replace("%deaths%", String.valueOf(arena.getStats().getStat("deaths", username)));
        }

        if (s.contains("%kit%")) {
            if (arena.getKits().getKitOfPlayer(username) != null) {
                s = s.replace("%kit%", arena.getKits().getKitOfPlayer(username).getName());
            } else {
                s = s.replace("%kit%", "None");
            }
        }

        return s;
    }

    public boolean isCombatActionPermittedInRegion(Player p) {
        if (plugin.hasWorldGuard()) {
            if (WorldGuardAPI.getInstance().allows(p, WorldGuardFlag.PVP.getFlag())) {
                return true;
            }

            p.sendMessage(resources.getMessages().fetchString("Messages.Error.PVP"));
            return false;
        }
        return true;
    }

}
