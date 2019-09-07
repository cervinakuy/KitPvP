/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Crypto Morin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.planetgallium.kitpvp.util;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * Up to 1.14 potion type support for multiple aliases.
 * Uses EssentialsX enchantment list for aliases.
 * EssentialsX Potions: https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/enchantments/Enchantment.java
 * Status Effect: https://minecraft.gamepedia.com/Status_effect
 * Potions: https://minecraft.gamepedia.com/Potion
 *
 * Duration: The duration of the effect in ticks. Values 0 or lower are treated as 1. Optional, and defaults to 1 tick.
 * Amplifier: The amplifier of the effect, with level I having value 0. Optional, and defaults to level I.
 *
 * @author Crypto Morin
 * @version 1.0.0
 */
public enum XPotion {
    ABSORPTION("ABSORB"),
    BAD_OMEN("OMEN_BAD", "PILLAGER"),
    BLINDNESS("BLIND"),
    CONDUIT_POWER("CONDUIT"),
    CONFUSION("NAUSEA", "SICKNESS", "SICK"),
    DAMAGE_RESISTANCE("RESISTANCE", "ARMOR", "DMGRESIST"),
    DOLPHINS_GRACE("DOLPHIN", "GRACE"),
    FAST_DIGGING("HASTE", "SUPERPICK", "DIGFAST", "DIGSPEED", "QUICKMINE", "SHARP"),
    FIRE_RESISTANCE("FIRERESIST", "RESISTFIRE", "FIRERESISTANCE"),
    GLOWING("GLOW"),
    HARM("INJURE", "DAMAGE", "HARMING", "INFLICT"),
    HEAL("HEALTHY", "INSTAHEAL"),
    HEALTH_BOOST("HEALTHBOOST", "BOOST"),
    HERO_OF_THE_VILLAGE("HERO", "VILLAGE_HERO"),
    HUNGER("STARVE", "HUNGRY"),
    INCREASE_DAMAGE("STRENGTH", "BULL", "STRONG", "ATTACK"),
    INVISIBILITY("INVISIBLE", "VANISH", "INVIS", "DISAPPEAR"),
    JUMP("LEAP"),
    LEVITATION("LEVITATE"),
    LUCK("LUCKY"),
    NIGHT_VISION("NIGHTVISION", "VISION"),
    POISON("VENOM"),
    REGENERATION("REGEN"),
    SATURATION("FOOD"),
    SLOW("SLOWNESS", "SLUGGISH"),
    SLOW_DIGGING("FATIGUE", "SLOW", "DULL"),
    SLOW_FALLING("SLOW_FALL", "FALL_SLOW"),
    SPEED("SPRINT", "RUNFAST", "SWIFT", "FAST"),
    UNLUCK("UNLUCKY"),
    WATER_BREATHING("WATERBREATH", "WATERBREATHING", "WATERBREATH", "UNDERWATERBREATHING", "UNDERWATERBREATH", "AIR", "WATERBREATHING"),
    WEAKNESS("WEAK"),
    WITHER("DECAY");

    private final String[] names;

    XPotion(String... names) {
        this.names = names;
    }

    /**
     * Parses the potion effect type.
     * @return the parsed potion effect type.
     * @since 1.0.0
     * @see #getPotionType()
     */
    @Nullable
    public PotionEffectType parsePotionEffectType() {
        return PotionEffectType.getByName(this.name());
    }

    /**
     * Parses a potion effect type from the given string.
     * Supports type IDs.
     * @param type the type of the type's ID of the potion effect type.
     * @return a potion effect type.
     * @since 1.0.0
     */
    @Nullable
    @SuppressWarnings("deprecation")
    public static PotionEffectType matchPotionType(@Nonnull String type) {
        if (type.matches("^[0-9]*$")) return PotionEffectType.getById(Integer.parseInt(type));

        String filtered = type.toUpperCase(Locale.ENGLISH).replace(" ", "");
        Optional<XPotion> enchant = Arrays.stream(values())
                .filter(e -> e.name().toLowerCase().equals(filtered) || Arrays.asList(e.names).contains(filtered))
                .findFirst();
        return enchant.map(XPotion::parsePotionEffectType).orElse(null);
    }

    /**
     * Gets the PotionType from this PotionEffectType.
     * Usually for potion items.
     * @return a potion type for potions.
     * @since 1.0.0
     * @see #parsePotionEffectType()
     */
    @SuppressWarnings("deprecation")
    public PotionType getPotionType() {
        PotionEffectType type = this.parsePotionEffectType();
        return type == null ? null : PotionType.getByEffect(type);
    }

    /**
     * Builds a potion effect with the given duration and amplifier.
     * @param duration the duration of the potion effect.
     * @param amplifier the amplifier of the potion effect.
     * @return a potion effect.
     * @since 1.0.0
     * @see #parsePotionFromString(String)
     */
    public PotionEffect parsePotion(int duration, int amplifier) {
        PotionEffectType type = this.parsePotionEffectType();
        if (type == null) return null;
        return new PotionEffect(type, duration, amplifier);
    }

    /**
     * Parse a PotionEffect from a string, usually from config.
     * Supports potion type IDs.
     * <pre>
     *     WEAKNESS, 30, 1
     *     SLOWNESS, 200, 10
     *     1, 10000, 100
     * </pre>
     * @param potion the potion string to parse.
     * @return a potion effect, or null if the potion type is wrong.
     * @since 1.0.0
     * @see #parsePotion(int, int)
     */
    @SuppressWarnings("deprecation")
    public PotionEffect parsePotionFromString(String potion) {
        String[] split = potion.replace(" ", "").split(",");

        PotionEffectType type;
        if (split[0].matches("^[0-9]*$")) type = PotionEffectType.getById(Integer.parseInt(split[0]));
        else type = matchPotionType(split[0]);
        if (type == null) return null;

        int duration = split.length > 1 ? Integer.parseInt(split[1]) * 20 : 20 * 60 * 2;
        int amplifier = split.length > 2 ? Integer.parseInt(split[2]) - 1 : 0;

        return new PotionEffect(type, duration, amplifier);
    }
}
