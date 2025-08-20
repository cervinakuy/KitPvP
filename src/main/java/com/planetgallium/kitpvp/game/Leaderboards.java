package com.planetgallium.kitpvp.game;

import com.planetgallium.database.TopEntry;
import com.planetgallium.kitpvp.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Leaderboards {

    private final Map<String, Leaderboard> leaderboards;

    public Leaderboards(Game plugin) {
        Infobase database = plugin.getDatabase();
        this.leaderboards = new HashMap<>();

        leaderboards.put("kills",
                new Leaderboard("kills", database.getTopNStats("kills", 25), 25));
        leaderboards.put("deaths",
                new Leaderboard("deaths", database.getTopNStats("deaths", 25), 25));
        leaderboards.put("level",
                new Leaderboard("level", database.getTopNStats("level", 25), 25));
    }

    public void updateRankings(String leaderboardName, UUID uniqueId, int data) {
        if (isValidLeaderboardName(leaderboardName)) {
            leaderboards.get(leaderboardName).updateRankings(uniqueId, data);
        }
    }

    public TopEntry getTopN(String leaderboardName, int rank) {
        if (isValidLeaderboardName(leaderboardName)) {
            return leaderboards.get(leaderboardName).getNRanking(rank);
        }
        return TopEntry.empty();
    }

    private boolean isValidLeaderboardName(String leaderboardName) {
        return leaderboardName.equals("kills") || leaderboardName.equals("deaths") || leaderboardName.equals("level");
    }

}
