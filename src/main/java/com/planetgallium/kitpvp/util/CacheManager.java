package com.planetgallium.kitpvp.util;

import com.planetgallium.kitpvp.api.Kit;

import java.util.HashMap;
import java.util.Map;

public class CacheManager {

    private static Map<String, Kit> kitCache = new HashMap<>();;
    private static Map<String, Menu> menuCache = new HashMap<>();

    public static Map<String, Kit> getKitCache() { return kitCache; }

    public static Map<String, Menu> getMenuCache() { return menuCache; }

}
