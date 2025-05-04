package com.planetgallium.kitpvp.api;

import com.planetgallium.kitpvp.util.Cooldown;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Toolkit;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Ability {

    private final String name;
    private ItemStack activator;
    private Cooldown cooldown;
    private String message;
    private Sound sound;
    private int soundPitch;
    private int soundVolume;
    private final List<PotionEffect> effects;
    private final List<String> commands;

    public Ability(String name) {
        this.name = name;
        this.effects = new ArrayList<>();
        this.commands = new ArrayList<>();
    }

    public void setActivator(ItemStack activator) {
        this.activator = activator;
    }

    public void setCooldown(Cooldown cooldown) {
        this.cooldown = cooldown;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSound(Sound sound, int pitch, int volume) {
        this.sound = sound;
        this.soundPitch = pitch;
        this.soundVolume = volume;
    }

    public void addEffect(PotionEffectType type, int amplifierNonZeroBased, int durationSeconds) {
        PotionEffect effect = new PotionEffect(type, Toolkit.parsePotionEffectDuration(durationSeconds), amplifierNonZeroBased - 1);
        effects.add(effect);
    }

    public void addCommand(String command) {
        commands.add(command);
    }

    public void toResource(Resource resource) {
        resource.set("Activator.Name", Toolkit.toNormalColorCodes(activator.getItemMeta().getDisplayName()));
        resource.set("Activator.Material", activator.getType().toString());
        if (cooldown != null) {
            resource.set("Cooldown.Cooldown", cooldown.formatted(true));
        }
        resource.set("Message.Message", message);
        resource.set("Sound.Sound", sound.toString());
        resource.set("Sound.Pitch", soundPitch != 0 ? soundPitch : null);
        resource.set("Sound.Volume", soundVolume != 0 ? soundVolume : null);

        for (PotionEffect effect : effects) {
            String type = effect.getType().getName();
            int amplifierNonZeroBased = effect.getAmplifier() + 1;
            int durationSeconds = effect.getDuration() / 20;

            resource.set("Effects." + type + ".Amplifier", amplifierNonZeroBased);
            resource.set("Effects." + type + ".Duration", durationSeconds);
        }

        resource.set("Commands", commands.toArray());

        resource.save();
    }

    public String getName() { return name; }

    public ItemStack getActivator() { return activator; }

    public Cooldown getCooldown() { return cooldown; }

    public String getMessage() { return message; }

    public Sound getSound() { return sound; }

    public int getSoundPitch() { return soundPitch; }

    public int getSoundVolume() { return soundVolume; }

    public List<PotionEffect> getEffects() { return effects; }

    public List<String> getCommands() { return commands; }

}
