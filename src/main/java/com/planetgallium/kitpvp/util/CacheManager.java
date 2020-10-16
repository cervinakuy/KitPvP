package com.planetgallium.kitpvp.util;

import com.planetgallium.kitpvp.api.Kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheManager {

    private static Map<String, Kit> kitCache = new HashMap<>();;
    private static Map<String, Menu> menuCache = new HashMap<>();
    private static List<String> compassUsers = new ArrayList<>();

    public static Map<String, Kit> getKitCache() { return kitCache; }

    public static Map<String, Menu> getMenuCache() { return menuCache; }

    public static List<String> getCompassUsers() { return compassUsers; }

}
