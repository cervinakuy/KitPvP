package com.planetgallium.kitpvp.api.Events;

import com.planetgallium.kitpvp.api.Enums.KitPvPDeathReason;
import com.planetgallium.kitpvp.game.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KitPvPPlayerDeathEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Arena arena;

    private final Player killer;

    private final Player victim;

    private KitPvPDeathReason deathReason;

    private boolean isCancelled;

    public KitPvPPlayerDeathEvent(Arena arena, Player killer, Player victim, KitPvPDeathReason deathReason) {
        this.arena = arena;
        this.killer = killer;
        this.victim = victim;
        this.deathReason = deathReason;
        this.isCancelled = false;
    }

    @Override
    public boolean isCancelled() { return isCancelled; }

    @Override
    public void setCancelled(boolean option) { isCancelled = option; }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    @Override
    public String getEventName() { return "KitPvPPlayerDeathEvent"; }

    public static HandlerList getHandlerList() { return HANDLERS; }

    public Player getVictim() { return victim; }

    public Player getKiller() { return killer; }

    public KitPvPDeathReason getDeathReason() { return deathReason; }

    public int getVictimDeaths() { return arena.getStats().getDeaths(victim.getUniqueId()); }

    public int getVictimKills() { return arena.getStats().getKills(victim.getUniqueId()); }

    public double getVicitimKDRatio() { return arena.getStats().getKDRatio(victim.getUniqueId()); }

    public double getKillerKDRatio() { return arena.getStats().getKDRatio(killer.getUniqueId()); }

}
