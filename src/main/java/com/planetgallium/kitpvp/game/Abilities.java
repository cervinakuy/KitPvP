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
    private final Map<AbilityActivatorMetaData, Ability> activatorDataToAbility;

    public Abilities(Game plugin) {
        this.resources = plugin.getResources();
        this.activatorDataToAbility = new HashMap<>();

        rebuildCache();
    }

    public void rebuildCache() {
        for (Resource abilityResource : resources.getAbilityResources()) {
            Ability ability = getAbilityFromResource(abilityResource);
            AbilityActivatorMetaData activatorMetaData = new AbilityActivatorMetaData(ability.getActivator());
            activatorDataToAbility.put(activatorMetaData, ability);
        }
    }

    public Ability getAbilityByActivator(ItemStack potentialActivator) {
        return activatorDataToAbility.get(new AbilityActivatorMetaData(potentialActivator));
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

    public class AbilityActivatorMetaData {

        private final String materialName;
        private String displayName;

        public AbilityActivatorMetaData(ItemStack buildFromItem) {
            this.materialName = buildFromItem.getType().toString();
            if (buildFromItem.hasItemMeta()) {
                ItemMeta itemMeta = buildFromItem.getItemMeta();
                if (itemMeta.hasDisplayName()) {
                    this.displayName = itemMeta.getDisplayName();
                }
            }
        }

        @Override
        public boolean equals(Object otherObject) {
            if (otherObject instanceof AbilityActivatorMetaData) {
                AbilityActivatorMetaData otherAbilityData = (AbilityActivatorMetaData) otherObject;
                return this.displayName.equals(otherAbilityData.getDisplayName()) &&
                        this.materialName.equals(otherAbilityData.getMaterialName());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.displayName.hashCode() + this.materialName.hashCode();
        }

        public String getDisplayName() { return displayName; }

        public String getMaterialName() { return materialName; }

    }

}
