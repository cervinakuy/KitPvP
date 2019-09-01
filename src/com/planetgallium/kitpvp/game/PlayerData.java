package com.planetgallium.kitpvp.game;

public class PlayerData {
	
	private String username;
	private int level;
	private int experience;
    private int kills;
    private int deaths;
   
    public PlayerData(String username, int level, int experience, int kills, int deaths) {
    	this.username = username;
    	this.level = level;
    	this.experience = experience;
    	this.kills = kills;
    	this.deaths = deaths;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getExperience() {
        return experience;
    }

    public int getLevel() {
        return level;
    }

    public String getUsername() {
    	return username;
    }
    
    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}


