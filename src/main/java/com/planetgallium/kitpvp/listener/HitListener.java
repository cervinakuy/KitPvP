package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.XSound;

public class HitListener implements Listener {

	private Arena arena;

	public HitListener(Game plugin) {
		this.arena = plugin.getArena();
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {

		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {

			Player damager = (Player) e.getDamager();
			Player damagedPlayer = (Player) e.getEntity();

			if (Toolkit.inArena(damagedPlayer)) {

				arena.getHitCache().put(damagedPlayer.getName(), damager.getName());

				if (Config.getB("HitSound.Enabled")) {

					damager.playSound(damager.getLocation(), XSound.matchXSound(Config.getS("Combat.HitSound.Sound")).get().parseSound(), 1, (float) Config.getI("HitSound.Pitch"));
					damagedPlayer.playSound(damagedPlayer.getLocation(), XSound.matchXSound(Config.getS("HitSound.Sound")).get().parseSound(), 1, (float) Config.getI("HitSound.Pitch"));

				}

			}

		}
		 
	}
	
}
