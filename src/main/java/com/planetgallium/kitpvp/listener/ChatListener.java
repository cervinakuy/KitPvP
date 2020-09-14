package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.game.Arena;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;

public class ChatListener implements Listener {

	private Arena arena;
	private Resources resources;
	private FileConfiguration config;
	
	public ChatListener(Game plugin) {
		this.arena = plugin.getArena();
		this.config = plugin.getConfig();
		this.resources = plugin.getResources();
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		
		if (config.getBoolean("Chat.Enabled") && Toolkit.inArena(e.getPlayer())) {
			
			Player p = e.getPlayer();

			String playerLevel = String.valueOf(arena.getLevels().getLevel(p.getUniqueId()));
			String levelPrefix = resources.getLevels().getString("Levels.Levels." + playerLevel + ".Prefix")
					.replace("%level%", playerLevel);

			String format = config.getString("Chat.Format")
					.replace("%player%", "%s")
					.replace("%message%", "%s")
					.replace("%level%", levelPrefix);

			e.setFormat(Toolkit.addPlaceholdersIfPossible(p, format));

		}

	}

}
