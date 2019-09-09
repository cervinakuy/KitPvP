package com.planetgallium.kitpvp.api.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KitPvPLevelChangeEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean isCancelled;

    private final Player player;

    private final int oldLevel;

    private final int newLevel;

    public KitPvPLevelChangeEvent(Player player, int oldLevel, int newLevel) {
        this.isCancelled = false;
        this.player = player;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    @Override
    public boolean isCancelled() { return isCancelled; }

    @Override
    public void setCancelled(boolean option) { isCancelled = option; }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    @Override
    public String getEventName() { return "KitPvPLevelChangeEvent"; }

    public static HandlerList getHandlerList() { return HANDLERS; }

    public Player getPlayer() { return player; }

    public int getOldLevel() { return oldLevel; }

    public int getNewLevel() { return newLevel; }
}
