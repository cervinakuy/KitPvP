package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.util.Toolkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Arena;

public class LeaveListener implements Listener {

	private final Game plugin;
	private final Arena arena;
	
	public LeaveListener(Game plugin) {
		this.plugin = plugin;
		this.arena = plugin.getArena();
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (Toolkit.inArena(p)) {
			arena.deletePlayer(p);
		}
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {
		if (Toolkit.inArena(e.getFrom())) { // if they left from a kitpvp arena
			Player p = e.getPlayer();

			if (plugin.getConfig().getBoolean("Arena.ClearInventoryOnLeave")) {
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
			}

			arena.removePlayer(p);
			// no need to clear stats from cache; that will be done on player quit above
		}
	}
	
}
