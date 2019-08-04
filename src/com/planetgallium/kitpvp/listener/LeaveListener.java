package com.planetgallium.kitpvp.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Resources;

public class LeaveListener implements Listener {

	@SuppressWarnings("unused")
	private Resources resources;
	
	public LeaveListener(Resources resources) {
		this.resources = resources;
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		
		Player p = e.getPlayer();
		
		if (Game.getInstance().getArena().isPlayer(p.getName()) || Game.getInstance().getArena().isSpectator(p.getName())) {
			
			Game.getInstance().getArena().deletePlayer(p);
			
		}
		
		Game.getInstance().getArena().removeUser(p.getName());
		
	}
	
}
