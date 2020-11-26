package com.planetgallium.kitpvp.command;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Resource;
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

		String[] words = message.split(" ");

		if (message.equals("/spawn") && config.getBoolean("Commands.Alias.Spawn")) {

			e.setCancelled(true);
			p.performCommand("kp spawn");

		} else if (message.equals("/kits") && config.getBoolean("Commands.Alias.Kits")) {

			e.setCancelled(true);
			p.performCommand("kp kits");

		} else if (message.startsWith("/kit") && config.getBoolean("Commands.Alias.Kit")) {

			if (words.length == 1) {
				e.setCancelled(true);
				p.performCommand("kp kit");
			} else if (words.length == 2) {
				e.setCancelled(true);
				p.performCommand("kp kit " + words[1]);
			}

		} else if (message.startsWith("/stats") && config.getBoolean("Commands.Alias.Stats")) {

			if (words.length == 1) {
				e.setCancelled(true);
				p.performCommand("kp stats");
			} else if (words.length == 2) {
				e.setCancelled(true);
				p.performCommand("kp stats " + words[1]);
			}

		}
		
	}
	
}
