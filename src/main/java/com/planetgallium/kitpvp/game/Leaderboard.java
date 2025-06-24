package com.planetgallium.kitpvp.game;

import com.planetgallium.database.TopEntry;
import com.planetgallium.kitpvp.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Leaderboard {

    private final String name;
    private final Map<UUID, TopEntry> rankings;
    private final int maxSize;

    private List<UUID> sorted = new ArrayList<>();

    public Leaderboard(String name, Map<UUID, TopEntry> initialRankings, int maxSize) {
        this.name = name;
        this.rankings = initialRankings;
        this.maxSize = maxSize;
        sortRankings();
    }

    public void updateRankings(UUID uniqueId, int data) {
        TopEntry entry = this.rankings.get(uniqueId);
        if (entry == null) {
            if (data < getLast().getValue()) {
                return;
            }
            entry = new TopEntry(Game.getInstance().getDatabase().uuidToUsername(uniqueId), data);
            this.rankings.put(uniqueId, entry);
        }
        entry.setValue(data);
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
        if (n <= this.sorted.size() && n >= 1) {
            return this.rankings.get(this.sorted.get(n - 1));
        }
        return TopEntry.empty();
    }

    public TopEntry getLast() {
        if (this.rankings.isEmpty()) {
            return TopEntry.empty();
        }
        return this.rankings.get(this.sorted.get(this.sorted.size() - 1));
    }

    public void sortRankings() {
        this.sorted = this.rankings.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(this.maxSize)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public String getName() { return name; }

}
