package com.planetgallium.kitpvp.util;

import com.planetgallium.kitpvp.api.Kit;

import java.util.*;

public class CacheManager {

    private static final Map<String, UUID> usernameToUUID = new HashMap<>();
    private static final Map<String, Kit> kitCache = new HashMap<>();
    private static final Map<String, Menu> previewMenuCache = new HashMap<>();
    private static final Map<UUID, Map<String, Long>> abilityCooldowns = new HashMap<>();
    private static final Map<UUID, PlayerData> statsCache = new HashMap<>();
    private static final Set<UUID> potionSwitcherUsers = new HashSet<>();

    public static Map<String, UUID> getUUIDCache() { return usernameToUUID; }

    public static Map<String, Kit> getKitCache() { return kitCache; }

    public static Map<String, Menu> getPreviewMenuCache() { return previewMenuCache; }

    public static Map<UUID, PlayerData> getStatsCache() { return statsCache; }

    public static Set<UUID> getPotionSwitcherUsers() { return potionSwitcherUsers; }

    public static Map<String, Long> getPlayerAbilityCooldowns(UUID uniqueId) {
        if (!abilityCooldowns.containsKey(uniqueId)) {
            abilityCooldowns.put(uniqueId, new HashMap<>());
        }
        return abilityCooldowns.get(uniqueId);
    }

    public static void clearCaches() {
        kitCache.clear();
        previewMenuCache.clear();
        abilityCooldowns.clear();
        // stats, usernameToUUID, and cache isn't here as of right now
    }

}
