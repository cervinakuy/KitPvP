package com.planetgallium.kitpvp.game;

import com.planetgallium.database.TopEntry;
import com.planetgallium.kitpvp.Game;

import java.util.HashMap;
import java.util.Map;

public class Leaderboards {

    private final Map<String, Leaderboard> leaderboards;

    public Leaderboards(Game plugin) {
        Infobase database = plugin.getDatabase();
        this.leaderboards = new HashMap<>();

        leaderboards.put("kills",
                new Leaderboard("kills", database.getTopNStats("kills", 25), 25));
        leaderboards.put("deaths",
                new Leaderboard("kills", database.getTopNStats("deaths", 25), 25));
        leaderboards.put("level",
                new Leaderboard("kills", database.getTopNStats("level", 25), 25));
    }

    public void updateRankings(String leaderboardName, TopEntry newEntry) {
        if (isValidLeaderboardName(leaderboardName)) {
            leaderboards.get(leaderboardName).updateRankings(newEntry);
        }
    }

    public TopEntry getTopN(String leaderboardName, int rank) {
        if (isValidLeaderboardName(leaderboardName)) {
            return leaderboards.get(leaderboardName).getNRanking(rank);
        }
        return new TopEntry("NAN", -1);
    }

    private boolean isValidLeaderboardName(String leaderboardName) {
        return leaderboardName.equals("kills") || leaderboardName.equals("deaths") || leaderboardName.equals("level");
    }

}
