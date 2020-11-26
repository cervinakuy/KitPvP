package com.planetgallium.kitpvp.listener;

import com.cryptomorin.xseries.XMaterial;
import com.planetgallium.kitpvp.util.CacheManager;
import com.planetgallium.kitpvp.util.Resources;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Toolkit;

public class TrackerListener implements Listener {

	private Resources resources;
	
	public TrackerListener(Game plugin) {
		this.resources = plugin.getResources();
	}
	
	@EventHandler
	public void onCompassHeld(PlayerItemHeldEvent event) {
		
		Player player = event.getPlayer();
		
		if (Toolkit.inArena(player)) {
			
			ItemStack item = player.getInventory().getItem(event.getNewSlot());

			if (item != null && item.getType() == XMaterial.COMPASS.parseMaterial()) {

				if (!CacheManager.getCompassUsers().contains(player.getName())) {

					new BukkitRunnable() {

						@Override
						public void run() {

							if (player != null) {

								String[] nearestData = Toolkit.getNearestPlayer(player, resources.getConfig().getInt("PlayerTracker.TrackBelowY"));

								if (player.getWorld().getPlayers().size() > 1 && nearestData != null) {

									Player nearestPlayer = Bukkit.getPlayer(nearestData[0]);
									double nearestDistance = Double.parseDouble(nearestData[1]);

									nearestDistance = Math.round(nearestDistance * 10.0) / 10.0;

									ItemMeta meta = item.getItemMeta();
									meta.setDisplayName(resources.getConfig().getString("PlayerTracker.Message")
											.replace("%nearestplayer%", nearestPlayer.getName())
											.replace("%distance%", String.valueOf(nearestDistance)));
									item.setItemMeta(meta);

									player.setCompassTarget(nearestPlayer.getLocation());

									CacheManager.getCompassUsers().add(player.getName());

								} else {

									ItemMeta meta = item.getItemMeta();
									meta.setDisplayName(resources.getConfig().getString("PlayerTracker.NoneOnline"));
									item.setItemMeta(meta);

									cancel();

								}

							} else {

								CacheManager.getCompassUsers().remove(player.getName());
								cancel();

							}

						}

					}.runTaskTimer(Game.getInstance(), 0L, 20L);

				}

			}
			
		}
		
	}
	
}
