package com.planetgallium.kitpvp.util;

public class PlayerData {

    private int kills, deaths, experience, level, xpMultiplier;

    public PlayerData(int kills, int deaths, int experience, int level, int xpMultiplier) {
        this.kills = kills;
        this.deaths = deaths;
        this.experience = experience;
        this.level = level;
        this.xpMultiplier = xpMultiplier;
    }

    public int getDataByIdentifier(String identifier) {
        switch (identifier) {
            case "kills": return getKills();
            case "deaths": return getDeaths();
            case "experience": return getExperience();
            case "level": return getLevel();
            case "xpMultiplier": return getXpMultiplier();
        }
        return -1;
    }

    public void setDataByIdentifier(String identifier, int data) {
        switch (identifier) {
            case "kills": setKills(data); break;
            case "deaths": setDeaths(data); break;
            case "experience": setExperience(data); break;
            case "level": setLevel(data); break;
            case "xpMultiplier": setLevel(data); break;
        }
    }

    private void setKills(int amount) { this.kills = amount; }

    private void setDeaths(int amount) { this.deaths = amount; }

    private void setExperience(int amount) { this.experience = amount; }

    private void setLevel(int amount) { this.level = amount; }

    public void setXpMultiplier(int xpMultiplier) { this.xpMultiplier = xpMultiplier; }

    private int getKills() { return kills; }

    private int getDeaths() { return deaths; }

    private int getExperience() { return experience; }

    private int getLevel() { return level; }

    public int getXpMultiplier() { return xpMultiplier; }
    
    

}
