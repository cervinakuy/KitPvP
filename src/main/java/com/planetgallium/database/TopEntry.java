package com.planetgallium.database;

public class TopEntry implements Comparable<TopEntry> {

    private final String identifier;
    private int value;

    public TopEntry(String identifier, int value) {
        this.identifier = identifier;
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

    public String getIdentifier() { return identifier; }

    public int getValue() { return value; }

}
