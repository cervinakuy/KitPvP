package com.planetgallium.kitpvp.api.Objects.KitManager.Abilities;

import com.planetgallium.kitpvp.Game;

import org.bukkit.Material;

public class Activator {

    private Game game;

    private String name;

    private Material item;

    private String kit;

    public Activator(String kit) {
        this.game = Game.getInstance();
        this.name = game.getResources().getKits(kit).getString("Ability.Activator.Name");
        this.item = Material.valueOf(game.getResources().getKits(kit).getString("Ability.Activator.Item"));
        this.kit = kit;
    }

    public String getName() {
        return name;
    }

    public Material getItem() {
        return item;
    }

    public void setName(String name) {
        this.name = name;
        game.getResources().getKits(kit).set("Ability.Activator.Name", name);
    }

    public void setItem(Material item) {
        this.item = item;
        game.getResources().getKits(kit).set("Ability.Activator.Item", item.toString());
    }
}
