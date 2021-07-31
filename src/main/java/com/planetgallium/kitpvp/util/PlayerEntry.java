package com.planetgallium.kitpvp.util;

public class PlayerEntry {

    private final String username;
    private int data;

    public PlayerEntry(String username, int data) {
        this.username = username;
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("Player(%s, %s)", username, data);
    }

    public void setData(int newData) { this.data = newData; }

    public String getUsername() { return username; }

    public int getData() { return data; }

}
