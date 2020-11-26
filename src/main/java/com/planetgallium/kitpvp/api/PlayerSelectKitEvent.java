package com.planetgallium.kitpvp.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerSelectKitEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();
	
	private final Player player;
	private final Kit kit;
	
	public PlayerSelectKitEvent(Player player, Kit kit) {
		this.player = player;
		this.kit = kit;
	}
	
	public Player getPlayer() { return player; }
	
	public Kit getKit() { return kit; }
	
	public HandlerList getHandlers() { return HANDLERS; }

	public String getEventName() { return "PlayerSelectKitEvent"; }

	public static HandlerList getHandlerList() { return HANDLERS; }
	
}
