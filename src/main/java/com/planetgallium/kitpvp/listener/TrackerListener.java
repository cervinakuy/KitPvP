package com.planetgallium.kitpvp.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.XMaterial;

public class TrackerListener implements Listener {

	private Game plugin;
	
	public TrackerListener(Game plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onCompassHeld(PlayerItemHeldEvent event) {
		
		Player player = event.getPlayer();
		
		if (Toolkit.inArena(player)) {
			
			ItemStack item = player.getInventory().getItem(event.getNewSlot());
			
			if (item != null) {
				
				if (item.getType() == XMaterial.COMPASS.parseMaterial()) {
					
					new BukkitRunnable() {
						
						@Override
						public void run() {

							String[] nearestData = Toolkit.getNearestPlayer(player, plugin.getConfig().getInt("PlayerTracker.TrackBelowY"));

							if (player.getWorld().getPlayers().size() > 1 && nearestData != null) {

								Player nearestPlayer = Bukkit.getPlayer(nearestData[0]);
								double nearestDistance = Double.parseDouble(nearestData[1]);

								nearestDistance = Math.round(nearestDistance * 10.0) / 10.0;

								ItemMeta meta = item.getItemMeta();
								meta.setDisplayName(Config.getS("PlayerTracker.Message").replace("%nearestplayer%", nearestPlayer.getName()).replace("%distance%", String.valueOf(nearestDistance)));
								item.setItemMeta(meta);

								player.setCompassTarget(nearestPlayer.getLocation());
								
							} else {
								
		                		ItemMeta meta = item.getItemMeta();
		                		meta.setDisplayName(Config.tr(plugin.getConfig().getString("PlayerTracker.NoneOnline")));
		                		item.setItemMeta(meta);
		                		
		                		cancel();
								
							}
							
						}
						
					}.runTaskTimer(plugin, 0L, 20L);
					
				}
				
			}
			
		}
		
	}
	
}
