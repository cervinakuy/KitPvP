package com.planetgallium.kitpvp.command;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import com.planetgallium.kitpvp.util.Config;

public class AliasCommand implements Listener {

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		
		Player p = e.getPlayer();
		String message = e.getMessage();

		if (message.equals("/spawn") && Config.getB("Commands.Alias.Spawn")) {
			
			e.setCancelled(true);
			p.performCommand("cspawn");
			
		} else if (message.equals("/kit") && Config.getB("Commands.Alias.Kit")) {
			
			e.setCancelled(true);
			p.performCommand("ckit");
			
		} else if (message.equals("/kits") && Config.getB("Commands.Alias.Kits")) {
			
			e.setCancelled(true);
			p.performCommand("ckits");
			
		} else if (message.equals("/stats") && Config.getB("Commands.Alias.Stats")) {
			
			e.setCancelled(true);
			p.performCommand("cstats");
			
		}
		
	}
	
}
