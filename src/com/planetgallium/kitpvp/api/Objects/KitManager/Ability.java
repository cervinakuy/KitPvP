package com.planetgallium.kitpvp.api.Objects.KitManager;

import com.planetgallium.kitpvp.api.Objects.KitManager.Abilities.*;

public class Ability {

    private Activator activator;

    private Commands commands;

    private Message message;

    private Potions potions;

    private Sound sound;

    private String kit;

    public Ability(String kit) {
        this.activator = new Activator(kit);
        this.commands = new Commands(kit);
        this.message = new Message(kit);
        this.potions = new Potions(kit);
        this.sound = new Sound(kit);
        this.kit = kit;
    }

    public Activator getActivator() { return activator; }

    public Commands getCommands() { return commands; }

    public Message getMessage() { return message; }

    public Potions getPotions() { return potions; }

    public Sound getSound() { return sound; }
}
