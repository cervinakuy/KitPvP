package com.planetgallium.kitpvp.game;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.Ability;
import com.planetgallium.kitpvp.util.Cooldown;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class Abilities {

    private final Resources resources;
    private final Map<ItemStack, Ability> activatorToAbility;

    public Abilities(Game plugin) {
        this.resources = plugin.getResources();
        this.activatorToAbility = new HashMap<>();

        rebuildCache();
    }

    public void rebuildCache() {
        for (Resource abilityResource : resources.getAbilityResources()) {
            Ability ability = getAbilityFromResource(abilityResource);
            activatorToAbility.put(ability.getActivator(), ability);
        }
    }

    public Ability getAbilityByActivator(ItemStack potentialActivator) {
        int previousPotentialActivatorAmount = potentialActivator.getAmount();
        potentialActivator.setAmount(1);

        for (ItemStack loadedActivator : activatorToAbility.keySet()) {
            if (Toolkit.itemStacksMatch(loadedActivator, potentialActivator)) {
                Ability ability = activatorToAbility.get(potentialActivator);
                potentialActivator.setAmount(previousPotentialActivatorAmount);
                return ability;
            }
        }
        return null;
    }

    private Ability getAbilityFromResource(Resource resource) {
        Ability ability = new Ability(resource.getName());

        // Activator
        ItemStack activator = Toolkit.safeItemStack(resource.fetchString("Activator.Material"));
        ItemMeta activatorMeta = activator.getItemMeta();

        activatorMeta.setDisplayName(resource.fetchString("Activator.Name"));
        activator.setItemMeta(activatorMeta);

        ability.setActivator(activator);

        // Cooldown
        if (resource.contains("Cooldown")) {
            String formattedCooldown = resource.fetchString("Cooldown.Cooldown");
            ability.setCooldown(new Cooldown(formattedCooldown));
        }

        // Message
        if (resource.contains("Message")) {
            ability.setMessage(resource.fetchString("Message.Message"));
        }

        // Sound
        if (resource.contains("Sound")) {
            Sound abilitySound = Toolkit.safeSound(resource.fetchString("Sound.Sound"));
            int abilitySoundPitch = resource.getInt("Sound.Pitch");
            int abilitySoundVolume = resource.getInt("Sound.Volume");

            ability.setSound(abilitySound, abilitySoundPitch, abilitySoundVolume);
        }

        // Effects
        if (resource.contains("Effects")) {
            ConfigurationSection effectSection = resource.getConfigurationSection("Effects");

            for (String effectName : effectSection.getKeys(false)) {
                PotionEffectType effectType = Toolkit.safePotionEffectType(effectName);
                int amplifier = resource.getInt("Effects." + effectName + ".Amplifier");
                int duration = resource.getInt("Effects." + effectName + ".Duration");

                ability.addEffect(effectType, amplifier, duration);
            }
        }

        // Commands
        if (resource.contains("Commands")) {
            for (String command : resource.getStringList("Commands")) {
                ability.addCommand(command);
            }
        }

        return ability;
    }

}
