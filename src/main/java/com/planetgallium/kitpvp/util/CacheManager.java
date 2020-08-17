package com.planetgallium.kitpvp.util;

import com.planetgallium.kitpvp.api.Kit;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {

    private static Map<String, Kit> kitCache;
    private static Map<String, Menu> menuCache;

    public CacheManager() {

        this.kitCache = new HashMap<>();
        this.menuCache = new HashMap<>();

    }

    // make method to print out cache contents to see if a getter actually modifies the map

    public static Map<String, Kit> getKitCache() { return kitCache; }

    public static Map<String, Menu> getMenuCache() { return menuCache; }

}
