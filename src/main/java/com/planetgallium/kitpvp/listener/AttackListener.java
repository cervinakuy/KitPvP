package com.planetgallium.kitpvp.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;

public class AttackListener implements Listener {

	private Resources resources;
	
	public AttackListener(Game plugin) {
		this.resources = plugin.getResources();
	}
	
	@EventHandler
	public void onDamageDealt(EntityDamageByEntityEvent e) {
		
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			
			Player damagedPlayer = (Player) e.getEntity();
			Player damager = (Player) e.getDamager();
			
			if (Toolkit.inArena(damagedPlayer) && !damagedPlayer.hasMetadata("NPC")) {
				
				if (resources.getConfig().getBoolean("Arena.NoKitProtection")) {
					
					if (!Game.getInstance().getArena().getKits().hasKit(damagedPlayer.getName())) {
						
						damager.sendMessage(resources.getMessages().getString("Messages.Error.Invincible"));
						e.setCancelled(true);
						
					}
					
					if (Game.getInstance().getArena().getKits().hasKit(damagedPlayer.getName()) && !Game.getInstance().getArena().getKits().hasKit(damager.getName())) {
						
						damager.sendMessage(resources.getMessages().getString("Messages.Error.Kit"));
						e.setCancelled(true);
						
					}
					
				}
				
			}
			
		} 
		
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		
		if (e.getEntity() instanceof Player) {
			
			Player damagedPlayer = (Player) e.getEntity();
			
			if (Toolkit.inArena(damagedPlayer)) {
				
				if (resources.getConfig().getBoolean("Arena.NoKitProtection")) {
					
					if (!Game.getInstance().getArena().getKits().hasKit(damagedPlayer.getName())) {
						
						if (e.getCause() != DamageCause.VOID) {
						
							e.setCancelled(true);
						
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
}
