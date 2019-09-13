package com.planetgallium.kitpvp.api.Objects.KitManager.Abilities;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.Objects.KitManager.Potion;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class Potions {

    private Game game;

    private HashMap<String, Potion> potionCache;

    private String kit;

    public Potions(String kit) {
        game = Game.getInstance();
        ConfigurationSection section = game.getResources().getKits(kit).getConfigurationSection("Ability.Potions");
        for (String pot: section.getKeys(false)) {
            Potion potion = new Potion(game.getResources().getKits(kit).getInt("Ability.Potions." + pot + ".Level"),
                    game.getResources().getKits(kit).getInt("Ability.Potions." + pot + ".Duration"));
            potionCache.put(pot, potion);
        }
        this.kit = kit;
    }

    public HashMap<String, Potion> getPotions() { return potionCache; }
    public void setPotions(HashMap<String, Potion> potions) {
        potionCache = potions;
        for (String pot: potionCache.keySet()) {
            game.getResources().getKits(kit).set("Ability.Potions." + pot + ".Level", potionCache.get(pot).getLevel());
            game.getResources().getKits(kit).set("Ability.Potions." + pot + ".Duration", potionCache.get(pot).getDuration());
        }
    }


}
