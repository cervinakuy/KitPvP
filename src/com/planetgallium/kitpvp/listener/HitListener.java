package com.planetgallium.kitpvp.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Sounds;
import com.planetgallium.kitpvp.util.Toolkit;

public class HitListener implements Listener {

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		
		if (Config.getB("HitSound.Enabled")) {
			
			if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
		
				Player damager = (Player) e.getDamager();
				Player damagedPlayer = (Player) e.getEntity();
				
				if (Toolkit.inArena(damagedPlayer)) {
						
					damager.playSound(damager.getLocation(), Sounds.valueOf(Config.getS("Combat.HitSound.Sound")).bukkitSound(), 1, (float) Config.getI("HitSound.Pitch"));
					damagedPlayer.playSound(damagedPlayer.getLocation(), Sounds.valueOf(Config.getS("HitSound.Sound")).bukkitSound(), 1, (float) Config.getI("HitSound.Pitch"));
					
				}
	        
			}
			
		}
		 
	}
	
}
