package com.planetgallium.kitpvp.game;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.PlayerEntry;
import com.planetgallium.kitpvp.util.Toolkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Leaderboards {

    private final Infobase database;
    private final Map<String, List<PlayerEntry>> leaderboards;

    public Leaderboards(Game plugin) {
        this.database = plugin.getDatabase();
        this.leaderboards = new HashMap<>();

        Toolkit.printToConsole("&7[&b&lKIT-PVP&7] &7Loading Leaderboard caches....");
        registerCache("kills", 25);
        registerCache("deaths", 25);
        registerCache("level", 25);

        // TODO: implement PriorityQueue to manage cached Leaderboards... currently you're basically recreating it lol...
    }

    private void registerCache(String key, int size) {
        leaderboards.put(key, new ArrayList<>());
        leaderboards.get(key).addAll(database.getTopNStats(key, size));
    }

    public void updateCache(String key, PlayerEntry playerEntry) {

        if (!isValidCacheType(key)) return;

        List<PlayerEntry> leaderboard = getLeaderboard(key);

        if (leaderboard.size() == 0) {
            return;
        }
        
        PlayerEntry lowestRankingPlayer = leaderboard.get(leaderboard.size() - 1);

        // If updated player entry has a higher data value than the lowest ranking player in a leaderboard
        if (playerEntry.getData() > lowestRankingPlayer.getData()) {
            if (!cacheContainsPlayer(leaderboard, playerEntry.getUsername())) {
                // If not already in the leaderboard, add them
                leaderboard.add(playerEntry);
            } else {
                // If already in the leaderboard, update their player entry with the latest data
                updatePlayerEntry(leaderboard, playerEntry);
            }
            // Sort the leaderboard based on the new insertion / data
            insertionSort(leaderboard);
        } else {
            // If updated player entry does not have a higher data value than the lowest ranking player in the leaderboard
            if (cacheContainsPlayer(leaderboard, playerEntry.getUsername())) {
                // If they are in the leaderboard, remove them and resort
                removePlayerFromCache(leaderboard, playerEntry.getUsername());
                insertionSort(leaderboard); // idk if this sort is necessary
            }
        }

    }

    private boolean isValidCacheType(String identifier) {
        return identifier.equals("kills") || identifier.equals("deaths") || identifier.equals("level");
    }

    private boolean cacheContainsPlayer(List<PlayerEntry> leaderboard, String username) {

        for (PlayerEntry entry : leaderboard) {
            if (entry.getUsername().equals(username)) {
                return true;
            }
        }
        return false;

    }

    private void updatePlayerEntry(List<PlayerEntry> leaderboard, PlayerEntry playerEntry) {

        for (PlayerEntry entry : leaderboard) {
            if (entry.getUsername().equals(playerEntry.getUsername())) {
                entry.setData(playerEntry.getData());
                break;
            }
        }

    }

    private void removePlayerFromCache(List<PlayerEntry> leaderboard, String username) {

        for (PlayerEntry entry : leaderboard) {
            if (entry.getUsername().equals(username)) {
                leaderboard.remove(entry);
                break;
            }
        }

    }

    private void insertionSort(List<PlayerEntry> leaderboard) {

        for (int i = 1; i < leaderboard.size(); ++i) {
            PlayerEntry currentEntry = leaderboard.get(i);
            int j = i - 1;

            while (j >= 0 && leaderboard.get(j).getData() < currentEntry.getData()) {
                leaderboard.set(j + 1, leaderboard.get(j));
                j -= 1;
            }

            leaderboard.set(j + 1, currentEntry);
        }

    }

    public PlayerEntry getTopN(String key, int rank) {

        List<PlayerEntry> leaderboard = getLeaderboard(key);
        int rankIndex = rank - 1;

        return rankIndex < leaderboard.size() ? leaderboard.get(rankIndex) : new PlayerEntry("N / A", 0);

    }

    private List<PlayerEntry> getLeaderboard(String key) {
        return leaderboards.get(key);
    }

}
