package com.planetgallium.kitpvp.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;

import me.clip.placeholderapi.PlaceholderAPI;

public class ChatListener implements Listener {
	
	private Resources resources;
	
	public ChatListener(Resources resources) {
		this.resources = resources;
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		
		if (resources.getLevels().getBoolean("Levels.General.Chat.Enabled") && Toolkit.inArena(e.getPlayer())) {
			
			Player p = e.getPlayer();
			
			String levelNumber = String.valueOf(Game.getInstance().getArena().getLevels().getLevel(p.getUniqueId()));
			String level = resources.getLevels().getString("Levels.Levels.Level-" + levelNumber).replace("%number%", levelNumber);
			
			if (Toolkit.hasPlaceholders()) {
			
				String withPlaceholders = PlaceholderAPI.setBracketPlaceholders(p, resources.getLevels().getString("Levels.General.Chat.Format").replace("{message}", "%2$s").replace("{level}", level).replace("{player}", p.getName()));
				e.setFormat(Config.tr(withPlaceholders));
				
			} else {
				
				String withoutPlaceholders = resources.getLevels().getString("Levels.General.Chat.Format").replace("{level}", level).replace("{player}", "%s").replace("{message}", "%s");
				e.setFormat(Config.tr(withoutPlaceholders));
				
			}
				
		}
		
	}

}
