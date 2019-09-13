package com.planetgallium.kitpvp.api.Objects.KitManager.Abilities;

import com.planetgallium.kitpvp.Game;

public class Sound {

    private Game game;

    private String sound;

    private int pitch;

    private boolean enabled;

    private String kit;

    public Sound(String kit) {
        game = Game.getInstance();
        sound = game.getResources().getKits(kit).getString("Ability.Sound.Sound");
        pitch = game.getResources().getKits(kit).getInt("Ability.Sound.Pitch");
        enabled = game.getResources().getKits(kit).getBoolean("Ability.Sound.Enabled");
        this.kit = kit;
    }

    public String getSound() {
        return sound;
    }


    public int getPitch() {
        return pitch;
    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setSound(String sound) {
        this.sound = sound;
        game.getResources().getKits(kit).set("Ability.Sound.Sound", sound);
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
        game.getResources().getKits(kit).set("Ability.Sound.Pitch", pitch);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        game.getResources().getKits(kit).set("Ability.Sound.Enabled", enabled);
    }
}
