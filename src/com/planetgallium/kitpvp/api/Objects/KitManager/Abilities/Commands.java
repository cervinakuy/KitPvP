package com.planetgallium.kitpvp.api.Objects.KitManager.Abilities;

import com.planetgallium.kitpvp.Game;

import java.util.List;

public class Commands {

    private Game game;

    private List<String> commands;

    private boolean enabled;

    private String kit;

    public Commands(String kit) {
        game = Game.getInstance();
        commands = game.getResources().getKits(kit).getStringList("Ability.Commands.Commands");
        enabled = game.getResources().getKits(kit).getBoolean("Ability.Commands.Enabled");
        this.kit = kit;
    }

    public List<String> getCommands() {
        return commands;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
        game.getResources().getKits(kit).set("Ability.Commands.Commands", commands);
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        game.getResources().getKits(kit).set("Ability.Commands.Enabled", enabled);
    }
}
