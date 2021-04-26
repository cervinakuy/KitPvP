package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.game.Arena;
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
	
	public ChatListener(Game plugin) {
		this.arena = plugin.getArena();
		this.resources = plugin.getResources();
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		
		if (resources.getConfig().getBoolean("Chat.Enabled") && Toolkit.inArena(e.getPlayer())) {
			
			Player p = e.getPlayer();

			String playerLevel = String.valueOf(arena.getStats().getStat("level", p.getName()));
			String levelPrefix = resources.getLevels().getString("Levels.Levels." + playerLevel + ".Prefix")
					.replace("%level%", playerLevel);

			String format = resources.getConfig().getString("Chat.Format")
					.replace("%player%", "%s")
					.replace("%message%", "%s")
					.replace("%level%", levelPrefix);

			e.setFormat(Toolkit.addPlaceholdersIfPossible(p, format));

		}

	}

}
