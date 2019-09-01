package com.planetgallium.kitpvp.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.planetgallium.kitpvp.kit.Ability;

public class PlayerAbilityEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();
	
	private final Player player;
	
	private final Ability ability;
	
	private boolean isCancelled;
	
	public PlayerAbilityEvent(Player player, Ability ability) {
		this.player = player;
		this.ability = ability;
		this.isCancelled = false;
	}
	public Player getPlayer() { return player; }
	
	public Ability getAbility() { return ability; }
	
	public HandlerList getHandlers() { return HANDLERS; }

	public String getEventName() { return "PlayerAbilityEvent"; }

	public static HandlerList getHandlerList() { return HANDLERS; }

	@Override
	public boolean isCancelled() { return this.isCancelled; }

	@Override
	public void setCancelled(boolean option) { this.isCancelled = option; }
	
}
