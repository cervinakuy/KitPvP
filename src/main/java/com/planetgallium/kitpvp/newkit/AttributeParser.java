package com.planetgallium.kitpvp.newkit;

import com.planetgallium.kitpvp.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
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
    private static final int FALLBACK_EFFECT_DURATION = 10;
    private static final int FALLBACK_EFFECT_AMPLIFIER = 1;

    // ItemStack
    private static final Material FALLBACK_ITEM_MATERIAL = Material.BEDROCK;
    private static final int FALLBACK_ITEM_AMOUNT = 1;
    private static final Enchantment FALLBACK_ITEM_ENCHANTMENT = Enchantment.THORNS;
    private static final int FALLBACK_ITEM_ENCHANTMENT_AMPLIFIER = 1;

    public static List<PotionEffect> getEffectsFromPath(Resource resource, String path) {

        List<PotionEffect> effects = new ArrayList<>();
        ConfigurationSection effectsSection = resource.getConfigurationSection(path);

        for (String effectName : effectsSection.getKeys(false)) {
            PotionEffectType type = XPotion.matchXPotion(effectName).get().parsePotionEffectType();
            int amplifier = resource.getInt(path + "." + effectName + ".Amplifier");
            int duration = resource.getInt(path + "." + effectName + ".Duration");

            effects.add(new PotionEffect(type != null ? type : FALLBACK_EFFECT_TYPE,
                                        duration != 0 ? duration : FALLBACK_EFFECT_DURATION,
                                        amplifier != 0 ? amplifier : FALLBACK_EFFECT_AMPLIFIER));
        }

        return effects;

    }

    public static ItemStack getItemStackFromPath(Resource resource, String path) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        ItemStack item = XMaterial.matchXMaterial(FALLBACK_ITEM_MATERIAL).parseItem();
        ItemMeta meta = item.getItemMeta();

        //          BASIC ITEM INFORMATION          //

        if (resource.contains(path + ".Name")) {
            meta.setDisplayName(resource.getString(path + ".Name"));
        }

        if (resource.contains(path + ".Lore")) {
            meta.setLore(resource.getStringList(path + ".Lore"));
        }

        if (resource.contains(path + ".Item")) {
            item.setType(XMaterial.matchXMaterial(resource.getString(path + ".Item")).get().parseMaterial().get());
        }

        if (resource.contains(path + ".Amount")) {
            item.setAmount(resource.getInt(path + ".Amount"));
        } else {
            item.setAmount(FALLBACK_ITEM_AMOUNT);
        }

        //          CUSTOM ATTRIBUTES               //

        if (resource.contains(path + ".Durability")) {
            setCustomDurability(item, resource.getInt(path + ".Durability"));
        }

        if (resource.contains(path + ".Dye")) {
            dyeItem(item, Toolkit.getColorFromConfig(resource, path + ".Dye"));
        }

        if (resource.contains(path + ".Skull")) {
            setSkull(item, resource.getString(path + ".Skull"));
        }

        if (resource.contains(path + ".Effects")) {
            boolean isCustom = !resource.contains(path + "." + resource.getConfigurationSection(path).getKeys(false).toArray()[0] + ".Upgraded");
            PotionMeta potionMeta = (PotionMeta) item.getItemMeta();

            // still missing more implementation for potions (check effectitem)

            if (isCustom) {

                List<PotionEffect> effects = new ArrayList<>();

                for (String effectName : resource.getConfigurationSection(path).getKeys(false)) {
                    PotionEffectType effectType = PotionEffectType.getByName(effectName);
                    int duration = resource.getInt(path + "." + effectName + ".Duration") * 20;
                    int amplifier = resource.getInt(path + "." + effectName + ".Amplifier");
                    effects.add(new PotionEffect(effectType, duration, amplifier - 1));
                }

                for (PotionEffect effect : effects) {
                    potionMeta.addCustomEffect(effect, true);
                }

                item.setItemMeta(potionMeta);

            } else {

                PotionType potionType = PotionType.UNCRAFTABLE;
                boolean isUpgraded = false;
                boolean isExtended = false;

                for (String potionName : resource.getConfigurationSection(path).getKeys(false)) {
                    potionType = PotionType.valueOf(resource.getString(path.replace("Effects", "") + "Type"));
                    isUpgraded = resource.getBoolean(path + "." + potionName + ".Upgraded");
                    isExtended = resource.getBoolean(path + "." + potionName + ".Extended");
                }

                PotionData potionData = new PotionData(potionType, isExtended, isUpgraded);
                potionMeta.setBasePotionData(potionData);

            }

        }

        if (resource.contains(path + ".Enchantments")) {
            Map<Enchantment, Integer> enchantments = new HashMap<>();
            ConfigurationSection section = resource.getConfigurationSection(path);

            for (String enchantmentName : section.getKeys(false)) {
                Enchantment enchantment = XEnchantment.matchXEnchantment(enchantmentName).get().parseEnchantment();
                int amplifier = resource.getInt(path + "." + enchantmentName + ".Level");

                enchantments.put(enchantment != null ? enchantment : FALLBACK_ITEM_ENCHANTMENT,
                                amplifier != 0 ? amplifier : FALLBACK_ITEM_ENCHANTMENT_AMPLIFIER);
            }

            setEnchantments(item, enchantments);
        }

        if (Config.getB("Arena.PreventItemDurabilityDamage")) {
            setUnbreakable(item);
        }

        return item;

    }

    private static void setCustomDurability(ItemStack item, int damageAmount) {

        // TEST BOTH VERSIONS OF THIS

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

    private static void setEnchantments(ItemStack item, Map<Enchantment, Integer> enchantments) {

        item.addUnsafeEnchantments(enchantments);

    }

    // for public api use
    private static void setSkull(ItemStack item, String skullOwner) {

        // TEST TO SEE IF THIS WORKs

        item.setType(XMaterial.PLAYER_HEAD.parseMaterial().get());

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        skullMeta.setOwner(skullOwner);

        item.setItemMeta(skullMeta);

    }

    // for public api use
    private static void dyeItem(ItemStack item, Color color) {

        // TEST TO SEE IF THIS WORKs

        if (item.getType() == XMaterial.LEATHER_HELMET.parseMaterial().get() ||
                item.getType() == XMaterial.LEATHER_CHESTPLATE.parseMaterial().get() ||
                item.getType() == XMaterial.LEATHER_LEGGINGS.parseMaterial().get() ||
                item.getType() == XMaterial.LEATHER_BOOTS.parseMaterial().get()) {

            LeatherArmorMeta dyedMeta = (LeatherArmorMeta) item.getItemMeta();
            dyedMeta.setColor(color);
            item.setItemMeta(dyedMeta);

        }

    }

    // for public api use? see if this works
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
//
//            return original;

        } else if (Toolkit.versionToNumber() > 114) {

            item.getItemMeta().setUnbreakable(true);
//            return original;

        }

//        return original;

    }

    public void addEffects(ItemStack item, List<PotionEffect> effects) {



    }

}
