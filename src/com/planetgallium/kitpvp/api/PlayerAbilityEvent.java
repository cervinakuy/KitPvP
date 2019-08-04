package com.planetgallium.kitpvp.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.planetgallium.kitpvp.kit.Ability;

public class PlayerAbilityEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();
	
	private final Player player;
	
	private final Ability ability;
	
	public PlayerAbilityEvent(Player player, Ability ability) {
		this.player = player;
		this.ability = ability;
	}
	
	public Player getPlayer() { return player; }
	
	public Ability getAbility() { return ability; }
	
	public HandlerList getHandlers() { return HANDLERS; }
	
	public static HandlerList getHandlerList() { return HANDLERS; }
	
}
