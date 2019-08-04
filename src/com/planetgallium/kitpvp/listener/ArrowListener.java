package com.planetgallium.kitpvp.listener;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Toolkit;

public class ArrowListener implements Listener {
	
	@EventHandler
	public void onShot(EntityDamageByEntityEvent e) {
		
		if (Toolkit.inArena(e.getEntity()) && Config.getB("Combat.ArrowHit.Enabled")) {
			
			if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow) {
				
				Player damagedPlayer = (Player) e.getEntity();
				Arrow arrow = (Arrow) e.getDamager();
				
				if (arrow.getShooter() != null && arrow.getShooter() instanceof Player) {
					
					Player shooter = (Player) arrow.getShooter();
					
					// ARROW HEALTH MESSAGE
					
					if (damagedPlayer.getName() != shooter.getName()) {
						
						new BukkitRunnable() {
							
							@Override
							public void run() {
								
								double health = Math.round(damagedPlayer.getHealth() * 10.0) / 10.0;
								
								if (shooter.hasPermission("kp.arrowmessage")) {
									
									if (health != 20.0) {
										
										shooter.sendMessage(Config.getS("Combat.ArrowHit.Message").replace("%player%", damagedPlayer.getName()).replace("%health%", String.valueOf(health)));
										
									}
									
								}
								
							}
							
						}.runTaskLater(Game.getInstance(), 2L);
						
					}
					
					// ARROW RETURN
					
					if (Config.getB("Combat.ArrowReturn.Enabled")) {
						
						for (ItemStack items : shooter.getInventory().getContents()) {
							
							if (items != null && items.getType() == Material.ARROW && items.getAmount() < 64) {
								
								if (shooter.hasPermission("kp.arrowreturn")) {
									
									ItemStack arrowInv = new ItemStack(Material.ARROW);
									shooter.getInventory().addItem(arrowInv);
									shooter.getInventory().addItem(arrowInv);
				
									return;
									
								}

							}
							
						}
						
				    	if (shooter.getInventory().firstEmpty() == -1) {
			     		       
			        		shooter.sendMessage(Config.getS("Combat.ArrowReturn.NoSpace"));
			        			
			        	}
						
					}
						
				}
				
			}
			
		}
		
	}

}
