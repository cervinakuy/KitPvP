package com.planetgallium.kitpvp.game;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.base.Preconditions;

public class Infoboard {
	
    private List<ScoreboardText> list;
    private Scoreboard scoreBoard;
    private Objective objective;
    private String tag;
    private int lastSentCount;

    public Infoboard(final Scoreboard scoreboard2, final String title) {
        this.list = new ArrayList<ScoreboardText>();
        this.tag = "PlaceHolder";
        this.lastSentCount = -1;
        Preconditions.checkState(title.length() <= 32, (Object) "title can not be more than 32");
        this.tag = ChatColor.translateAlternateColorCodes('&', title);
        this.scoreBoard = scoreboard2;
        (this.objective = this.getOrCreateObjective(this.tag)).setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void add(String input) {
        input = ChatColor.translateAlternateColorCodes('&', input);
        
        ScoreboardText text = null;
        if (input.length() <= 16) {
            text = new ScoreboardText(input, "");
        }
        else {
            String first = input.substring(0, 16);
            String second = input.substring(16, input.length());
            if (first.endsWith(String.valueOf('§'))) {
                first = first.substring(0, first.length() - 1);
                second = String.valueOf(String.valueOf('§')) + second;
            }
            final String lastColors = ChatColor.getLastColors(first);
            second = String.valueOf(String.valueOf(lastColors)) + second;
            text = new ScoreboardText(first, StringUtils.left(second, 16));
        }
        this.list.add(text);
    }

    public void clear() {
        this.list.clear();
    }

    public void remove(final int index) {
        final String name = this.getNameForIndex(index);
        this.scoreBoard.resetScores(name);
        final Team team = this.getOrCreateTeam(String.valueOf(String.valueOf(ChatColor.stripColor(StringUtils.left(this.tag, 14)))) + index, index);
        team.unregister();
    }

    public void update(final Player player) {
        player.setScoreboard(this.scoreBoard);
        for (int sentCount = 0; sentCount < this.list.size(); ++sentCount) {
            final Team i = this.getOrCreateTeam(String.valueOf(String.valueOf(ChatColor.stripColor(StringUtils.left(this.tag, 14)))) + sentCount, sentCount);
            final ScoreboardText str = this.list.get(this.list.size() - sentCount - 1);
            i.setPrefix(str.getLeft());
            i.setSuffix(str.getRight());
            this.objective.getScore(this.getNameForIndex(sentCount)).setScore(sentCount + 1);
        }
        if (this.lastSentCount != -1) {
            for (int sentCount = this.list.size(), var4 = 0; var4 < this.lastSentCount - sentCount; ++var4) {
                this.remove(sentCount + var4);
            }
        }
        this.lastSentCount = this.list.size();
    }

    public Team getOrCreateTeam(final String team, final int i) {
        Team value = this.scoreBoard.getTeam(team);
        if (value == null) {
            value = this.scoreBoard.registerNewTeam(team);
            value.addEntry(this.getNameForIndex(i));
        }
        return value;
    }

    public Objective getOrCreateObjective(final String objective) {
        Objective value = this.scoreBoard.getObjective("dummyhubobj");
        if (value == null) {
            value = this.scoreBoard.registerNewObjective("dummyhubobj", "dummy");
        }
        value.setDisplayName(objective);
        return value;
    }

    public String getNameForIndex(final int index) {
        return String.valueOf(String.valueOf(ChatColor.values()[index].toString())) + ChatColor.RESET;
    }

    private static class ScoreboardText
    {
        private String left;
        private String right;

        public ScoreboardText(final String left, final String right) {
            this.left = left;
            this.right = right;
        }

        public String getLeft() {
            return this.left;
        }

        public String getRight() {
            return this.right;
        }
    }
    
    /**
     * Added for testing purposes, not part of original class
     */
    public void hide() {
    	objective.setDisplaySlot(null);
    }
    
}
