package com.planetgallium.kitpvp.api.Events;

import com.planetgallium.kitpvp.game.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KitPvPExperienceChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean isCancelled;

    private final Player player;

    private final int oldExperience;

    private final int newExperience;

    public KitPvPExperienceChangeEvent(Player player, int oldExperience, int newExperience) {
        this.isCancelled = false;
        this.player = player;
        this.oldExperience = oldExperience;
        this.newExperience = newExperience;
    }
    @Override
    public boolean isCancelled() { return isCancelled; }

    @Override
    public void setCancelled(boolean option) { isCancelled = option; }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    @Override
    public String getEventName() { return "KitPvPExperienceChangeEvent"; }

    public static HandlerList getHandlerList() { return HANDLERS; }

    public Player getPlayer() { return player; }

    public int getOldExperience() { return oldExperience; }

    public int getNewExperience() { return newExperience; }

}
