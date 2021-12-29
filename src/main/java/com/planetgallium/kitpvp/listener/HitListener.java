package com.planetgallium.kitpvp.listener;

import com.cryptomorin.xseries.XSound;
import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Resource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import com.planetgallium.kitpvp.util.Toolkit;

public class HitListener implements Listener {

	private final Arena arena;
	private final Resource config;
	private final XSound.Record hitSound;

	public HitListener(Game plugin) {
		this.arena = plugin.getArena();
		this.config = plugin.getResources().getConfig();

		String soundString = config.getString("Combat.HitSound.Sound") + ", 1, " + config.getInt("Combat.HitSound.Pitch");
		this.hitSound = XSound.parse(soundString);
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {

		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {

			Player damager = (Player) e.getDamager();
			Player damagedPlayer = (Player) e.getEntity();

			if (Toolkit.inArena(damagedPlayer)) {

				arena.getHitCache().put(damagedPlayer.getName(), damager.getName());

				if (config.getBoolean("Combat.HitSound.Enabled")) {
					hitSound.forPlayer(damagedPlayer).play();
					hitSound.forPlayer(damager).play();
				}

			}

		}
		 
	}
	
}
