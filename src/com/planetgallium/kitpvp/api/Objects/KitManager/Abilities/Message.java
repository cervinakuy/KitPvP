package com.planetgallium.kitpvp.api.Objects.KitManager.Abilities;

import com.planetgallium.kitpvp.Game;

public class Message {

    private Game game;

    private String message;

    private boolean enabled;

    private String kit;

    public Message(String kit) {
        game = Game.getInstance();
        message = game.getResources().getKits(kit).getString("Ability.Message.Message");
        enabled = game.getResources().getKits(kit).getBoolean("Ability.Message.Enabled");
        this.kit = kit;
    }

    public String getMessage() {
        return message;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setMessage(String message) {
        this.message = message;
        game.getResources().getKits(kit).set("Ability.Message.Message", message);
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        game.getResources().getKits(kit).set("Ability.Message.Enabled", enabled);
    }
}
