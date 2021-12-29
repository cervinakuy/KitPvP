package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Resources;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Toolkit;

public class JoinListener implements Listener {

	private final Game plugin;
	private final Arena arena;
	private final Resources resources;
	private final Resource config;
	
	public JoinListener(Game plugin) {
		this.plugin = plugin;
		this.arena = plugin.getArena();
		this.resources = plugin.getResources();
		this.config = resources.getConfig();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {

		Player p = e.getPlayer();

		// Update checker
		if (plugin.needsUpdate()) {
			
			if (p.isOp()) {
				
				p.sendMessage(Toolkit.translate("&7[&b&lKIT-PVP&7] &aAn update was found: v" + plugin.getUpdateVersion() + " https://www.spigotmc.org/resources/27107/"));
				
			}
			
		}

		arena.getStats().createPlayer(p);
		
		if (Toolkit.inArena(p)) {
			
			if (config.getBoolean("Arena.ClearInventoryOnJoin")) {
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
			}
			
			arena.addPlayer(p, config.getBoolean("Arena.ToSpawnOnJoin"), config.getBoolean("Arena.GiveItemsOnJoin"));
			
		}

		if (p.getName().equals("cervinakuy")) {
			
			e.setJoinMessage(Toolkit.translate("&7[&b&lKIT-PVP&7] &7The Developer of &bKitPvP &7has joined the server."));
			
		}
		
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {

		Player p = e.getPlayer();

		if (Toolkit.inArena(p)) {

			if (config.getBoolean("Arena.ClearInventoryOnJoin")) {
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
			}
			
			arena.addPlayer(p, config.getBoolean("Arena.ToSpawnOnJoin"), config.getBoolean("Arena.GiveItemsOnJoin"));
			
		} else if (Toolkit.inArena(e.getFrom())) {

			// if they left from the kitpvp arena
			
			if (config.getBoolean("Arena.ClearInventoryOnLeave")) {
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
			}
			
			arena.removePlayer(p);
			
		}
		
	}
	
}
