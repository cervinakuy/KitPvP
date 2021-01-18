package com.planetgallium.kitpvp.util;

import com.planetgallium.kitpvp.api.Kit;

import java.util.*;

public class CacheManager {

    private static Map<String, Kit> kitCache = new HashMap<>();
    private static Map<String, Menu> previewMenuCache = new HashMap<>();
    private static List<String> compassUsers = new ArrayList<>();
    private static Map<String, Map<String, Long>> abilityCooldowns = new HashMap<>();
    private static Set<String> witchPotionUsers = new HashSet<>();

    public static Map<String, Kit> getKitCache() { return kitCache; }

    public static Map<String, Menu> getPreviewMenuCache() { return previewMenuCache; }

    public static List<String> getCompassUsers() { return compassUsers; }

    public static Set<String> getWitchPotionUsers() { return witchPotionUsers; }

    public static Map<String, Long> getPlayerAbilityCooldowns(String username) {

        if (abilityCooldowns.get(username) == null) {
            abilityCooldowns.put(username, new HashMap<>());
        }

        return abilityCooldowns.get(username);

    }

    public static void clearCaches() {
        kitCache.clear();
        previewMenuCache.clear();
        compassUsers.clear();
        abilityCooldowns.clear();
    }

}
