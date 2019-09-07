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

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * Up to 1.14 enchantment support with multiple aliases.
 * Uses EssentialsX enchantment list for aliases.
 * EssentialsX Enchantment: https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/enchantments/Enchantment.java
 * Enchanting: https://minecraft.gamepedia.com/Enchanting
 * <p>
 * Enchantment levels do not start from 0, they start from 1
 *
 * @author Crypto Morin
 * @version 1.0.0
 */
public enum XEnchantment {
    ARROW_DAMAGE("POWER", "ARROWDAMAGE", "ARROWPOWER", "AD"),
    ARROW_FIRE("FLAME", "FLAMEARROW", "FIREARROW", "AF"),
    ARROW_INFINITE("INFINITY", "INFARROWS", "INFINITEARROWS", "INFINITE", "UNLIMITED", "UNLIMITEDARROWS", "AI"),
    ARROW_KNOCKBACK("PUNCH", "ARROWKNOCKBACK", "ARROWKB", "ARROWPUNCH", "AK"),
    BINDING_CURSE(true, "BINDINGCURSE", "BINDCURSE", "BINDING", "BIND"),
    CHANNELING(true, "CHANNELLING", "CHANELLING", "CHANELING", "CHANNEL"),
    DAMAGE_ALL("SHARPNESS", "ALLDAMAGE", "ALLDMG", "SHARP", "DAL"),
    DAMAGE_ARTHROPODS("BANE_OF_ARTHROPODS", "ARDMG", "BANEOFARTHROPODS", "BANEOFARTHROPOD", "ARTHROPOD", "DAR"),
    DAMAGE_UNDEAD("SMITE", "UNDEADDAMAGE", "DU"),
    DEPTH_STRIDER(true, "DEPTHSTRIDER", "DEPTH", "STRIDER"),
    DIG_SPEED("EFFICIENCY", "DIGSPEED", "MINESPEED", "CUTSPEED", "DS", "EFF"),
    DURABILITY("UNBREAKING", "DURA", "D"),
    FIRE_ASPECT(true, "FIREASPECT", "FIRE", "MELEEFIRE", "MELEEFLAME", "FA"),
    FROST_WALKER(true, "FROSTWALKER", "FROST", "WALKER"),
    IMPALING("IMPALE", "OCEANDAMAGE", "OCEANDMG"),
    KNOCKBACK(true, "KBACK", "KB", "K"),
    LOOT_BONUS_BLOCKS("FORTUNE", "BLOCKSLOOTBONUS", "FORT", "LBB"),
    LOOT_BONUS_MOBS("LOOTING", "MOBLOOT", "MOBSLOOTBONUS", "LBM"),
    LOYALTY(true, "LOYAL", "RETURN"),
    LUCK("LUCK_OF_THE_SEA", "LUCKOFSEA", "LUCKOFSEAS", "RODLUCK"),
    LURE(true, "RODLURE"),
    MENDING(true),
    MULTISHOT(true, "TRIPLESHOT"),
    OXYGEN("RESPIRATION", "BREATH", "BREATHING", "O"),
    PIERCING(true),
    PROTECTION_ENVIRONMENTAL("PROTECTION", "PROTECT", "PROT", "P"),
    PROTECTION_EXPLOSIONS("BLAST_PROTECTION", "BLASTPROTECT", "EXPLOSIONSPROTECTION", "EXPLOSIONPROTECTION", "EXPPROT", "BLASTPROTECTION", "BPROTECTION", "BPROTECT", "PE"),
    PROTECTION_FALL("FEATHER_FALLING", "FALLPROT", "FEATHERFALL", "FALLPROTECTION", "FEATHERFALLING", "PFA"),
    PROTECTION_FIRE("FIRE_PROTECTION", "FIREPROT", "FIREPROTECT", "FIREPROTECTION", "FLAMEPROTECTION", "FLAMEPROTECT", "FLAMEPROT", "PF"),
    PROTECTION_PROJECTILE("PROJECTILE_PROTECTION", "PROJECTILEPROTECTION", "PROJPROT", "PP"),
    QUICK_CHARGE("QUICKCHARGE", "QUICKDRAW", "FASTCHARGE", "FASTDRAW"),
    RIPTIDE(true, "RIP", "TIDE", "LAUNCH"),
    SILK_TOUCH(true, "SILKTOUCH", "SOFTTOUCH", "ST"),
    SWEEPING_EDGE("SWEEPING", "SWEEPINGEDGE", "SWEEPEDGE"),
    THORNS(true, "HIGHCRIT", "THORN", "HIGHERCRIT", "T"),
    VANISHING_CURSE(true, "VANISHINGCURSE", "VANISHCURSE", "VANISHING", "VANISH"),
    WATER_WORKER("AQUA_AFFINITY", "WATERWORKER", "AQUAAFFINITY", "WATERMINE", "WW");

    /**
     * Java Edition 1.13/Flattening Update
     * https://minecraft.gamepedia.com/Java_Edition_1.13/Flattening
     */
    private static final boolean ISFLAT;

    static {
        boolean flat;
        try {
            Class<?> namespacedKeyClass = Class.forName("org.bukkit.NamespacedKey");
            Class<?> enchantmentClass = Class.forName("org.bukkit.enchantments.Enchantment");
            enchantmentClass.getDeclaredMethod("getByKey", namespacedKeyClass);
            flat = true;
        } catch (ClassNotFoundException | NoSuchMethodException ex) {
            flat = false;
        }
        ISFLAT = flat;
    }

    /**
     * If an enchantment has {@code self} as true, it means that
     * the vanilla enchantment name matches the Bukkit name.
     *
     * @see NamespacedKey#getKey()
     */
    private final boolean self;
    private final String[] names;

    XEnchantment(String... names) {
        this.names = names;
        this.self = false;
    }

    XEnchantment(boolean self, String... names) {
        this.self = self;
        this.names = names;
    }

    /**
     * Gets an enchantment from Vanilla and bukkit names.
     * There are also some aliases available.
     *
     * @param name the name of the enchantment.
     * @return an enchantment.
     * @since 1.0.0
     */
    @Nullable
    public static Enchantment matchEnchantment(@Nonnull String name) {
        String filtered = name.toUpperCase(Locale.ENGLISH).replace(" ", "");
        Optional<XEnchantment> enchant = Arrays.stream(values())
                .filter(e -> e.name().equals(filtered) || Arrays.asList(e.names).contains(filtered))
                .findFirst();
        return enchant.map(XEnchantment::parseEnchantment).orElse(null);
    }

    /**
     * Adds an unsafe enchantment to the given item from a string.
     * <p>
     * <blockquote><pre>
     *    ItemStack item = ...;
     *    addEnchantFromString(item, "unbreaking, 10");
     *    addEnchantFromString(item, "mending");
     * </pre></blockquote>
     * <p>
     * Note that if you set your item's meta {@link ItemStack#setItemMeta(ItemMeta)} the enchantment
     * will be removed.
     * You need to use {@link ItemMeta#addEnchant(Enchantment, int, boolean)} instead.
     * You can use the {@link #matchEnchantment(String)} method in this case.
     *
     * @param item        the item to add the enchantment to.
     * @param enchantment the enchantment string containing the enchantment name and level (optional)
     * @return an enchanted {@link ItemStack} or the item itself without enchantment added if enchantment type is null.
     * @see #matchEnchantment(String)
     * @since 1.0.0
     */
    public static ItemStack addEnchantFromString(ItemStack item, String enchantment) {
        String[] split = enchantment.replace(" ", "").split(",");
        Enchantment enchant = matchEnchantment(split[0]);
        if (enchant == null) return item;

        int lvl = 1;
        if (split.length > 1) lvl = Integer.parseInt(split[1]);

        item.addUnsafeEnchantment(enchant, lvl);
        return item;
    }

    /**
     * Gets the vanilla name of this enchantment.
     *
     * @return the vanilla name.
     * @see Enchantment#getByKey(NamespacedKey)
     * @since 1.0.0
     */
    public String getVanillaName() {
        return this.self ? this.name() : this.names[0];
    }

    /**
     * Parse the vanilla enchantment.
     *
     * @return a vanilla enchantment.
     * @since 1.0.0
     */
    @Nullable
    @SuppressWarnings("deprecation")
    public Enchantment parseEnchantment() {
        return ISFLAT ? Enchantment.getByKey(NamespacedKey.minecraft(this.getVanillaName().toLowerCase()))
                : Enchantment.getByName(this.name());
    }
}
