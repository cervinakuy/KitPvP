package com.planetgallium.kitpvp.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.kit.Ability;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;

public class EventListener implements Listener {

	private Arena arena;
	private Resources resources;
	
	public EventListener(Arena arena, Resources resources) {
		this.arena = arena;
		this.resources = resources;
	}
	
	@EventHandler
	public void onAbility(PlayerInteractEvent e) {
		
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
		
			if (Toolkit.inArena(e.getPlayer())) {
				
				Player p = e.getPlayer();
				
				if (arena.getKits().hasKit(p.getName())) {
					
					if (p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().hasDisplayName()) {
						
						if (p.getItemInHand().getType().toString().equals(resources.getKits(arena.getKits().getKit(p.getName())).getString("Ability.Activator.Item"))) {
							
							String kit = arena.getKits().getKit(p.getName());
							
							if (p.getItemInHand().getItemMeta().getDisplayName().replace("§", "&").equals(resources.getKits(kit).getString("Ability.Activator.Name"))) {
								
								Ability ability = new Ability(resources.getKits(kit), p.getItemInHand().getType());
								Bukkit.getPluginManager().callEvent(new PlayerAbilityEvent(p, ability));
								
							}
							
						}
						
					}	
					
				}
				
			}
			
		}
		
	}
	
}
