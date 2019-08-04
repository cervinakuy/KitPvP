package com.planetgallium.kitpvp.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerSelectKitEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();
	
	private final Player player;
	
	private final String kitName;
	
	public PlayerSelectKitEvent(Player player, String kitName) {
		this.player = player;
		this.kitName = kitName;
	}
	
	public Player getPlayer() { return player; }
	
	public String getKitName() { return kitName; }
	
	public HandlerList getHandlers() { return HANDLERS; }
	
	public static HandlerList getHandlerList() { return HANDLERS; }
	
}
