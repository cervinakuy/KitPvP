package com.planetgallium.database;

public class TopEntry implements Comparable<TopEntry> {

    private static final TopEntry EMPTY = new TopEntry("NAN", -1);

    public static TopEntry empty() {
        return EMPTY;
    }

    private final String name;
    private int value;

    public TopEntry(String name, int value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public int compareTo(TopEntry otherEntry) {
        if (this.getValue() < otherEntry.getValue()) {
            return 1;
        } else if (this.getValue() > otherEntry.getValue()) {
            return -1;
        }
        return 0; // if they're equal, will return 0
    }

    public void setValue(int newValue) { this.value = newValue; }

    public String getName() { return name; }

    public int getValue() { return value; }

}
