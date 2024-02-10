package com.planetgallium.kitpvp.util;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;

public enum WorldGuardFlag {
    PASSTHROUGH,
    BUILD,
    BLOCK_BREAK,
    BLOCK_PLACE,
    USE,
    INTEERACT,
    DAMAGE_ANIMALS,
    PVP,
    SLEEP,
    TNT,
    CHEST_ACCESS,
    PLACE_VEHICLE,
    DESTROY_VEHICLE,
    LIGHTER,
    RIDE,
    POTION_SPLASH,
    ITEM_FRAME_ROTATE,
    TRAMPLE_BLOCKS,
    ITEM_PICKUP,
    ITEM_DROP,
    EXP_DROPS,
    MOB_DAMAGE,
    CREEPER_EXPLOSION,
    ENDERDRAGON_BLOCK_DAMAGE,
    GHAST_FIREBALL,
    FIREWORK_DAMAGE,
    OTHER_EXPLOSION,
    WITHER_DAMAGE,
    ENDER_BUILD,
    SNOWMAN_TRAILS,
    RAVAGER_RAVAGE,
    ENTITY_PAINTING_DESTROY,
    ENTITY_ITEM_FAME_DESTROY,
    MOB_SPAWNING,
    DENY_SPAWN,
    PISTONS,
    FIRE_SPREAD,
    LAVA_FIRE,
    LIGHTNING,
    SNOW_FALL,
    SNOW_MELT,
    ICE_FORM,
    ICE_MELT,
    FROSTED_ICE_MELT,
    FROSTED_ICE_FORM,
    MUSHROOMS,
    LEAF_DECAY,
    GRASS_SPREAD,
    MYCELIUM_SPREAD,
    VINE_GROWTH,
    CROP_GROWTH,
    SOIL_DRY,
    WATER_FLOW,
    LAVA_FLOW,
    WEATHER_LOCK,
    TIME_LOCK,
    SEND_CHAT,
    RECEIVE_CHAT,
    BLOCKED_CMDS,
    ALLOWED_CMDS,
    TELE_LOC,
    SPAWN_LOC,
    INVINCIBILITY,
    FALL_DAMAGE,
    ENTRY,
    EXIT,
    EXIT_OVERRIDE,
    EXIT_VIA_TELEPORT,
    ENDERPEARL,
    CHORUS_TELEPORT,
    GREET_MESSAGE,
    FAREWELL_MESSAGE,
    GREET_TITLE,
    FAREWELL_TITLE,
    NOTIFY_ENTER,
    NOTIFY_LEAVE,
    GAME_MODE,
    HEAL_DECAY,
    HEAL_AMOUNT,
    MIN_HEAL,
    MAX_HEAL,
    FEED_DELAY,
    FEED_AMOUNT,
    MIN_FOOD,
    MAX_FOOD,
    DENY_MESSAGE,
    ENTRY_DENY_MESSAGE,
    EXIT_DENY_MESSAGE;

    private final byte version = WorldGuardAPI.getInstance().version;
    private StateFlag flag;

    public StateFlag getFlag() {
        if (flag != null)
            return flag;

        String flagName = name();

        try {
            Class<?> flagClass = version == 6 ?
                    Class.forName("com.sk89q.worldguard.protection.flags.DefaultFlag") : Flags.class;
            flag = (StateFlag) flagClass.getDeclaredField(flagName).get(null);
        } catch (Exception e) {
            System.out.println("[KitPvP] Unsupported flag! WorldGuard version " + version + " ; flag " + flagName);
            e.printStackTrace();
        }

        return flag;
    }

}