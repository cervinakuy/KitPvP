package com.planetgallium.kitpvp.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLevelUpEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();
	
	private final Player player;
	private final int level;
	
	public PlayerLevelUpEvent(Player player, int level) {
		this.player = player;
		this.level = level;
	}
	
	public Player getPlayer() { return player; }
	
	public int getLevel() { return level; }
	
	public HandlerList getHandlers() { return HANDLERS; }

	public String getEventName() { return "PlayerLevelUpEvent"; }

	public static HandlerList getHandlerList() { return HANDLERS; }
	
}
