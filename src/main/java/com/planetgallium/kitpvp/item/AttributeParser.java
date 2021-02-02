package com.planetgallium.kitpvp.item;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.XSound;
import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.Ability;
import com.planetgallium.kitpvp.util.*;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttributeParser {

    // PotionEffect
    private static final PotionEffectType FALLBACK_EFFECT_TYPE = PotionEffectType.CONFUSION;
    private static final int FALLBACK_EFFECT_DURATION_SECONDS = 10;
    private static final int FALLBACK_EFFECT_AMPLIFIER_NON_ZERO_BASED = 1;

    // ItemStack
    private static final Material FALLBACK_ITEM_MATERIAL = Material.BEDROCK;
    private static final int FALLBACK_ITEM_AMOUNT = 1;
    private static final Enchantment FALLBACK_ITEM_ENCHANTMENT = Enchantment.THORNS;
    private static final int FALLBACK_ITEM_ENCHANTMENT_AMPLIFIER = 1;

    public static List<PotionEffect> getEffectsFromPath(Resource resource, String path) {

        List<PotionEffect> effects = new ArrayList<>();
        ConfigurationSection effectsSection = resource.getConfigurationSection(path);

        if (effectsSection != null) {

            for (String effectName : effectsSection.getKeys(false)) {
                PotionEffectType type = XPotion.matchXPotion(effectName).get().parsePotionEffectType();
                int amplifier = resource.getInt(path + "." + effectName + ".Amplifier");
                int duration = resource.getInt(path + "." + effectName + ".Duration");

                effects.add(new PotionEffect(type != null ? type : FALLBACK_EFFECT_TYPE,
                        duration != 0 ? duration : FALLBACK_EFFECT_DURATION_SECONDS,
                        amplifier != 0 ? amplifier : FALLBACK_EFFECT_AMPLIFIER_NON_ZERO_BASED));
            }

        }

        return effects;

    }

    public static List<Ability> getAbilitiesFromResource(Resource resource) {

        List<Ability> abilities = new ArrayList<>();
        ConfigurationSection abilitySection = resource.getConfigurationSection("Abilities");

        if (abilitySection == null) return new ArrayList<>();

        for (String abilityName : abilitySection.getKeys(false)) {

            Ability ability = new Ability(abilityName);
            String pathPrefix = "Abilities." + abilityName;

            String activatorMaterial = resource.getString(pathPrefix + ".Activator.Material");
            String activatorName = resource.getString(pathPrefix + ".Activator.Name");

            ItemStack activator = new ItemStack(XMaterial.matchXMaterial(activatorMaterial).get().parseMaterial());
            ItemMeta abilityMeta = activator.getItemMeta();
            abilityMeta.setDisplayName(activatorName);
            activator.setItemMeta(abilityMeta);
            ability.setActivator(activator);

            if (resource.contains(pathPrefix + ".Cooldown")) {
                String formattedCooldown = resource.getString(pathPrefix + ".Cooldown.Cooldown");
                ability.setCooldown(new Cooldown(formattedCooldown));
            }

            if (resource.contains(pathPrefix + ".Message")) {
                String abilityMessage = resource.getString(pathPrefix + ".Message.Message");
                ability.setMessage(abilityMessage);
            }

            if (resource.contains(pathPrefix + ".Sound")) {
                Sound abilitySound = XSound.matchXSound(resource.getString(pathPrefix + ".Sound.Sound")).get().parseSound();
                int abilitySoundPitch = resource.getInt(pathPrefix + ".Sound.Pitch");
                int abilitySoundVolume = resource.getInt(pathPrefix + ".Sound.Volume");
                ability.setSound(abilitySound, abilitySoundPitch, abilitySoundVolume);
            }

            if (resource.contains(pathPrefix + ".Effects")) {
                ConfigurationSection effectSection = resource.getConfigurationSection(pathPrefix + ".Effects");

                for (String effectName : effectSection.getKeys(false)) {
                    PotionEffectType effectType = XPotion.matchXPotion(effectName).get().parsePotionEffectType();
                    int amplifier = resource.getInt(pathPrefix + ".Effects." + effectName + ".Amplifier");
                    int duration = resource.getInt(pathPrefix + ".Effects." + effectName + ".Duration");
                    ability.addEffect(effectType, amplifier, duration);
                }
            }

            if (resource.contains(pathPrefix + ".Commands")) {
                for (String command : resource.getStringList(pathPrefix + ".Commands")) {
                    ability.addCommand(command);
                }
            }

            abilities.add(ability);

        }

        return abilities;

    }

    public static ItemStack getItemStackFromPath(Resource resource, String path) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        if (!resource.contains(path)) return null;

        ItemStack item = XMaterial.matchXMaterial(FALLBACK_ITEM_MATERIAL).parseItem();
        ItemMeta meta = item.getItemMeta();

        //          BASIC ITEM INFORMATION          //

        if (resource.contains(path + ".Name")) {
            meta.setDisplayName(resource.getString(path + ".Name"));
        }

        if (resource.contains(path + ".Lore")) {
            meta.setLore(resource.getStringList(path + ".Lore"));
        }

        if (resource.contains(path + ".Material")) {
            item.setType(XMaterial.matchXMaterial(resource.getString(path + ".Material")).get().parseMaterial());
        }

        if (resource.contains(path + ".Amount")) {
            item.setAmount(resource.getInt(path + ".Amount"));
        } else {
            item.setAmount(FALLBACK_ITEM_AMOUNT);
        }

        item.setItemMeta(meta);

        //              CUSTOM ATTRIBUTES            //

        if (resource.contains(path + ".Effects")) {
            String effectsPath = path + ".Effects";

            if (Toolkit.versionToNumber() == 18) {
                if (item.getType() == XMaterial.POTION.parseMaterial() || item.getType() == XMaterial.SPLASH_POTION.parseMaterial()) {
                    boolean isSplash = resource.getString(effectsPath.replace("Effects", "") + "Type").equals("SPLASH_POTION");
                    Potion potion = Potion.fromItemStack(item);
                    potion.setSplash(isSplash);

                    item = potion.toItemStack(item.getAmount());
                }
            }

            boolean isCustom = !resource.contains(effectsPath + "." + resource.getConfigurationSection(effectsPath).getKeys(false).toArray()[0] + ".Upgraded");

            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();

            if (isCustom) {

                List<PotionEffect> effects = new ArrayList<>();

                for (String effectName : resource.getConfigurationSection(effectsPath).getKeys(false)) {
                    PotionEffectType effectType = PotionEffectType.getByName(effectName);
                    int duration = resource.getInt(effectsPath + "." + effectName + ".Duration") * 20;
                    int amplifier = resource.getInt(effectsPath + "." + effectName + ".Amplifier");
                    effects.add(new PotionEffect(effectType, duration, amplifier - 1));
                }

                for (PotionEffect effect : effects) {
                    potionMeta.addCustomEffect(effect, true);
                }

            } else {

                PotionType potionType = PotionType.UNCRAFTABLE;
                boolean isUpgraded = false;
                boolean isExtended = false;

                for (String potionName : resource.getConfigurationSection(effectsPath).getKeys(false)) {
                    potionType = PotionType.valueOf(resource.getString(effectsPath.replace("Effects", "") + "Type"));
                    isUpgraded = resource.getBoolean(effectsPath + "." + potionName + ".Upgraded");
                    isExtended = resource.getBoolean(effectsPath + "." + potionName + ".Extended");
                }

                PotionData potionData = new PotionData(potionType, isExtended, isUpgraded);
                potionMeta.setBasePotionData(potionData);

            }

            item.setItemMeta(potionMeta);

        }

        if (Game.getInstance().getResources().getConfig().getBoolean("Arena.PreventItemDurabilityDamage")) {
            setUnbreakable(item);
        }

        if (resource.contains(path + ".Dye")) {
            dyeItem(item, Toolkit.getColorFromConfig(resource, path + ".Dye"));
        }

        if (resource.contains(path + ".Skull")) {
            setSkull(item, resource.getString(path + ".Skull"));
        }

        if (resource.contains(path + ".Durability")) {
            setCustomDurability(item, resource.getInt(path + ".Durability"));
        }

        if (resource.contains(path + ".Enchantments")) {
            Map<Enchantment, Integer> enchantments = new HashMap<>();
            ConfigurationSection section = resource.getConfigurationSection(path + ".Enchantments");

            for (String enchantmentName : section.getKeys(false)) {
                Enchantment enchantment = XEnchantment.matchXEnchantment(enchantmentName).get().parseEnchantment();
                int amplifier = resource.getInt(path + ".Enchantments." + enchantmentName);

                enchantments.put(enchantment != null ? enchantment : FALLBACK_ITEM_ENCHANTMENT,
                        amplifier != 0 ? amplifier : FALLBACK_ITEM_ENCHANTMENT_AMPLIFIER);
            }

            item.addUnsafeEnchantments(enchantments);
        }

        return item;

    }

    private static void setCustomDurability(ItemStack item, int damageAmount) {

        if (Toolkit.versionToNumber() < 113) {

            item.setDurability((short) damageAmount);

        } else if (Toolkit.versionToNumber() >= 113) {

            ItemMeta meta = item.getItemMeta();

            if (meta instanceof Damageable) {

                ((Damageable) meta).setDamage(damageAmount);
                item.setItemMeta(meta);

            }

        }

    }

    private static void setSkull(ItemStack item, String skullOwner) {

        item.setType(XMaterial.PLAYER_HEAD.parseMaterial());

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        skullMeta.setOwner(skullOwner);

        item.setItemMeta(skullMeta);

    }

    private static void dyeItem(ItemStack item, Color color) {

        if (item.getType() == XMaterial.LEATHER_HELMET.parseMaterial() ||
                item.getType() == XMaterial.LEATHER_CHESTPLATE.parseMaterial() ||
                item.getType() == XMaterial.LEATHER_LEGGINGS.parseMaterial() ||
                item.getType() == XMaterial.LEATHER_BOOTS.parseMaterial()) {

            LeatherArmorMeta dyedMeta = (LeatherArmorMeta) item.getItemMeta();
            dyedMeta.setColor(color);
            item.setItemMeta(dyedMeta);

        }

    }

    private static void setUnbreakable(ItemStack item) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        if (Toolkit.versionToNumber() <= 114) {

            ItemMeta meta = item.getItemMeta();

            Method spigotMethod = meta.getClass().getMethod("spigot");
            spigotMethod.setAccessible(true);

            Object spigotInstance = spigotMethod.invoke(meta);
            Class spigotClass = spigotInstance.getClass();

            Method setUnbreakableMethod = spigotClass.getDeclaredMethod("setUnbreakable", boolean.class);
            setUnbreakableMethod.setAccessible(true);

            setUnbreakableMethod.invoke(spigotInstance, true);

        } else if (Toolkit.versionToNumber() > 114) {

            item.getItemMeta().setUnbreakable(true);

        }

    }

}
