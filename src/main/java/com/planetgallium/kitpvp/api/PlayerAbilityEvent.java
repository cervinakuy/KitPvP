package com.planetgallium.kitpvp.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerAbilityEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();
	
	private final Player player;
	private final Ability ability;
	private final PlayerInteractEvent originalInteractionEvent;
	
	public PlayerAbilityEvent(Player player, Ability ability, PlayerInteractEvent originalInteractionEvent) {
		this.player = player;
		this.ability = ability;
		this.originalInteractionEvent = originalInteractionEvent;
	}
	
	public Player getPlayer() { return player; }
	
	public Ability getAbility() { return ability; }
	
	public HandlerList getHandlers() { return HANDLERS; }

	public String getEventName() { return "PlayerAbilityEvent"; }

	public PlayerInteractEvent getOriginalInteractionEvent() { return originalInteractionEvent; }

	public static HandlerList getHandlerList() { return HANDLERS; }
	
}
