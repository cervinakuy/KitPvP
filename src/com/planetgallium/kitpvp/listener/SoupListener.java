package com.planetgallium.kitpvp.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Sounds;

public class SoupListener implements Listener {

	private int health = 6;
	
	@EventHandler
	public void onDamage(PlayerDeathEvent e) {

		Player victim = e.getEntity();
		
		if (victim.getKiller() != null && victim.getKiller() instanceof Player) {
			
			Player killer = victim.getKiller();
			
			if (Config.getB("GiveSoupOnKill.Enabled")) {
				
				if (killer.hasPermission("kp.soupreturn")) {
				
					int count = 0;
					for (int i = 0; i < 36; i++) {
						if (killer.getInventory().getItem(i) == null) {
							count++;
						}
					}
					
					if (count < Config.getI("GiveSoupOnKill.Amount")) {
						killer.sendMessage(Config.getS("GiveSoupOnKill.NoSpace"));
					}
					
					for (int r = 0; r < count; r++) {
						killer.getInventory().addItem(new ItemStack(Material.MUSHROOM_SOUP));
					}
					
				}
				
			}
			
		}
		
	}
	
	@EventHandler
	public void useSoup(PlayerInteractEvent e) {
	    
		if (Config.getB("Soups.Enabled")) {
			
		    if (e.getItem() != null && e.getItem().getType() == Material.MUSHROOM_SOUP) {
		    	
		        e.setCancelled(true);
		        
		        Player p = e.getPlayer();
		        
		        if (p.getHealth() < 20.0) {
		        	
		            p.playSound(p.getLocation(), Sounds.valueOf(Config.getS("Soups.Sound")).bukkitSound(), 1, (float) Config.getI("Soups.Pitch"));
		            p.setHealth(p.getHealth() + (double) health >= 20.0 ? 20.0 : p.getHealth() + (double) health);
		            
		            if (Config.getB("Soups.RemoveAfterUse")) {
		            	
		            	p.setItemInHand(new ItemStack(Material.AIR));
		            	
		            } else {
		            	
		            	p.setItemInHand(new ItemStack(Material.BOWL));
		            	
		            }
		            
		        }
		        
		    }
			
		}
		
	}
	
}
