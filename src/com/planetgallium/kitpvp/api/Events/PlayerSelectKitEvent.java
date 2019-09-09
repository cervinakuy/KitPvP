package com.planetgallium.kitpvp.api.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerSelectKitEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();
	
	private final Player player;
	
	private final String kitName;
	
	private boolean isCancelled;
	
	public PlayerSelectKitEvent(Player player, String kitName) {
		this.player = player;
		this.kitName = kitName;
		this.isCancelled = false;
	}
	
	public Player getPlayer() { return player; }
	
	public String getKitName() { return kitName; }
	
	public static HandlerList getHandlerList() { return HANDLERS; }

	@Override
	public boolean isCancelled() { return this.isCancelled; }

	@Override
	public void setCancelled(boolean option) { this.isCancelled = option; }

	@Override
	public HandlerList getHandlers() { return HANDLERS; }

	@Override
	public String getEventName() { return "PlayerSelectKitEvent"; }
	
}
