package com.planetgallium.kitpvp.game;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Resources;

public class Utilities {

    private final Arena arena;
    private final Resources resources;

    public Utilities(Game plugin) {
        this.arena = plugin.getArena();
        this.resources = plugin.getResources();
    }

    public String getPlayerLevelPrefix(String username) {
        String playerLevel = String.valueOf(arena.getStats().getStat("level", username));
        return resources.getLevels().getString("Levels.Levels." + playerLevel + ".Prefix")
                .replace("%level%", playerLevel);
    }

}
