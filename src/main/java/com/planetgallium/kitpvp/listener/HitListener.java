package com.planetgallium.kitpvp.listener;

import com.cryptomorin.xseries.XSound;
import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Resources;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import com.planetgallium.kitpvp.util.Toolkit;

public class HitListener implements Listener {

	private Arena arena;
	private Resources resources;
	private Resource config;
	private XSound.Record hitSound;

	public HitListener(Game plugin) {
		this.arena = plugin.getArena();
		this.resources = plugin.getResources();
		this.config = resources.getConfig();

		String soundString = config.getString("Combat.HitSound.Sound") + ", 1, " + config.getInt("Combat.HitSound.Pitch");
		this.hitSound = XSound.parse(null, Bukkit.getWorlds().get(0).getSpawnLocation(), soundString, false).join();
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {

		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {

			Player damager = (Player) e.getDamager();
			Player damagedPlayer = (Player) e.getEntity();

			if (Toolkit.inArena(damagedPlayer)) {

				arena.getHitCache().put(damagedPlayer.getName(), damager.getName());

				if (config.getBoolean("Combat.HitSound.Enabled")) {
					hitSound.play(damagedPlayer.getLocation());
					hitSound.play(damager.getLocation());
				}

			}

		}
		 
	}
	
}
