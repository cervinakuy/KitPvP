package com.planetgallium.kitpvp.command;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Resources;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class AliasCommand implements Listener {

	private Resource config;

	public AliasCommand(Game plugin) {
		this.config = plugin.getResources().getConfig();
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		
		Player p = e.getPlayer();
		String message = e.getMessage();

		if (message.equals("/spawn") && config.getBoolean("Commands.Alias.Spawn")) {
			
			e.setCancelled(true);
			p.performCommand("cspawn");
			
		} else if (message.equals("/kit") && config.getBoolean("Commands.Alias.Kit")) {
			
			e.setCancelled(true);
			p.performCommand("ckit");
			
		} else if (message.equals("/kits") && config.getBoolean("Commands.Alias.Kits")) {
			
			e.setCancelled(true);
			p.performCommand("ckits");
			
		} else if (message.equals("/stats") && config.getBoolean("Commands.Alias.Stats")) {
			
			e.setCancelled(true);
			p.performCommand("cstats");
			
		}
		
	}
	
}
