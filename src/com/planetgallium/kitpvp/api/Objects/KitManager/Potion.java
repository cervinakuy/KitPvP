package com.planetgallium.kitpvp.api.Objects.KitManager;

public class Potion {

    private int level;
    private int duration;

    public Potion(int level, int duration) {
        this.level = level;
        this.duration = duration;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setDuration(int amount) {
        this.duration = amount;
    }

    public int getLevel() {
        return this.level;
    }

    public int getDuration() {
        return this.duration;
    }
}
