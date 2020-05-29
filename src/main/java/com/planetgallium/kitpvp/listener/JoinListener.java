package com.planetgallium.kitpvp.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Toolkit;

public class JoinListener implements Listener {

	private Game game;
	private Arena arena;
	
	public JoinListener(Game game, Arena arena) {
		this.game = game;
		this.arena = arena;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		
		Player p = e.getPlayer();
		
		// Update checker
		if (Game.getInstance().needsUpdate()) {
			
			if (p.isOp()) {
				
				p.sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aAn update was found: v" + Game.getInstance().getUpdateVersion() + " https://www.spigotmc.org/resources/27107/"));
				
			}
			
		}

		game.getDatabase().addPlayer(p);
		arena.getStats().createPlayer(p.getName(), p.getUniqueId());
		
		if (Toolkit.inArena(p)) {
			
			if (Config.getB("Arena.ClearInventoryOnJoin")) {
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
			}
			
			arena.addPlayer(p, Config.getB("Arena.ToSpawnOnJoin"));
			
		}

		if (p.getName().equals("cervinakuy")) {
			
			e.setJoinMessage(Config.tr("&7[&b&lKIT-PVP&7] &7The Developer of &bKitPvP &7has joined the server."));
			
		}
		
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {

		Player p = e.getPlayer();

		if (Toolkit.inArena(p)) {

			if (Config.getB("Arena.ClearInventoryOnJoin")) {
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
			}
			
			arena.addPlayer(p, Config.getB("Arena.ToSpawnOnJoin"));
			
		} else if (Toolkit.inArena(e.getFrom())) {

			// if they left from the kitpvp arena
			
			if (Config.getB("Arena.ClearInventoryOnLeave")) {
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
			}
			
			arena.removePlayer(p);
			
		}
		
	}
	
}
