package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.game.Kits;
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
	private Kits kits;

	public AttackListener(Game plugin) {
		this.resources = plugin.getResources();
		this.kits = plugin.getArena().getKits();
	}
	
	@EventHandler
	public void onDamageDealt(EntityDamageByEntityEvent e) {
		
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			
			Player damagedPlayer = (Player) e.getEntity();
			Player damager = (Player) e.getDamager();
			
			if (Toolkit.inArena(damagedPlayer) && !damagedPlayer.hasMetadata("NPC")) {
				
				if (resources.getConfig().getBoolean("Arena.NoKitProtection")) {
					
					if (!kits.hasKit(damagedPlayer.getName())) {
						
						damager.sendMessage(resources.getMessages().fetchString("Messages.Error.Invincible"));
						e.setCancelled(true);
						
					}
					
					if (kits.hasKit(damagedPlayer.getName()) && !kits.hasKit(damager.getName())) {
						
						damager.sendMessage(resources.getMessages().fetchString("Messages.Error.Kit"));
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
//			System.out.println("Damaged player: " + damagedPlayer.getName());

			// bot is not doing damage to the player

			if (Toolkit.inArena(damagedPlayer)) {
//				System.out.println("Damaged player is in arena");
				if (resources.getConfig().getBoolean("Arena.NoKitProtection")) {
//					System.out.println("NoKitProt enabled");


					if (!kits.hasKit(damagedPlayer.getName())) {
//						System.out.println("has kit");
						if (e.getCause() != DamageCause.VOID) {
//							System.out.println("Cancelled damage");
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}

}
