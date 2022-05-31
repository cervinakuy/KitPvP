package com.planetgallium.kitpvp.util;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {

    private final Map<String, Integer> data;
    private final Map<String, Long> kitCooldowns;

    public PlayerData(int kills, int deaths, int experience, int level) {
        this.data = new HashMap<>();
        this.kitCooldowns = new HashMap<>();

        data.put("kills", kills);
        data.put("deaths", deaths);
        data.put("experience", experience);
        data.put("level", level);
    }

    public void setData(String identifier, int value) {
        data.put(identifier, value);
    }

    public void addKitCooldown(String kitName, long timeKitLastUsed) { kitCooldowns.put(kitName, timeKitLastUsed); }

    public int getData(String identifier) {
        return data.get(identifier);
    }

    public long getTimeKitLastUsed(String kitName) { return kitCooldowns.get(kitName); }

    public Map<String, Long> getKitCooldowns() { return kitCooldowns; }

    public Map<String, Integer> getData() { return data; }

}
