package com.planetgallium.kitpvp.game;

import com.planetgallium.database.TopEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Leaderboard {

    private final String name;
    private final List<TopEntry> rankings;
    private final int maxSize;

    public Leaderboard(String name, List<TopEntry> initialRankings, int maxSize) {
        this.name = name;
        this.rankings = initialRankings;
        this.maxSize = maxSize;
    }

    public void updateRankings(TopEntry playerEntry) {
        if (rankingsContainPlayer(playerEntry.getIdentifier())) {
            // Update the player's score if they are already in the leaderboard
            updatePlayerEntryInRanking(playerEntry);
        } else if (rankings.size() < maxSize || playerEntry.getValue() > rankings.get(rankings.size() - 1).getValue()) {
            // Add the new player if there's space, or they have a higher score than the lowest in the leaderboard
            if (rankings.size() == maxSize) {
                // Remove the lowest ranking player to make space for the new entry
                rankings.remove(rankings.size() - 1);
            }
            rankings.add(playerEntry);
        }
        sortRankings();
    }

//    public void updateRankings(TopEntry playerEntry) {
//        if (rankings.size() < maxSize) {
//            rankings.add(playerEntry);
//            sortRankings();
//        } else if (rankings.size() == maxSize) {
//            TopEntry lowestRankingPlayer = rankings.get(rankings.size() - 1);
//            if (playerEntry.getValue() > lowestRankingPlayer.getValue()) {
//                if (!rankingsContainPlayer(playerEntry.getIdentifier())) {
//                    // remove lowest ranking player and add new better player
//                    rankings.remove(rankings.size() - 1);
//                    rankings.add(playerEntry);
//                } else {
//                    // update ranking for player already in leaderboard
//                    updatePlayerEntryInRanking(playerEntry);
//                }
//                sortRankings(); // resort in both above cases
//            }
//        }
//    }

    public TopEntry getNRanking(int n) { // n = 1 is top player
        if (n <= rankings.size() && n >= 1) {
            return rankings.get(n - 1);
        }
        return new TopEntry("N / A", 0);
    }

    private boolean rankingsContainPlayer(String username) {
        for (TopEntry entry : this.rankings) {
            if (entry.getIdentifier().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private void updatePlayerEntryInRanking(TopEntry updatedEntry) {
        for (TopEntry entry : this.rankings) {
            if (entry.getIdentifier().equals(updatedEntry.getIdentifier())) {
                entry.setValue(updatedEntry.getValue());
                return;
            }
        }
    }

    public void sortRankings() {
        Collections.sort(rankings);
    }

    public String getName() { return name; }

}
