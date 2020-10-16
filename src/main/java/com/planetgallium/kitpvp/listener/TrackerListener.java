package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.util.CacheManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.XMaterial;

public class TrackerListener implements Listener {

	private FileConfiguration config;
	
	public TrackerListener(Game plugin) {
		this.config = plugin.getConfig();
	}
	
	@EventHandler
	public void onCompassHeld(PlayerItemHeldEvent event) {
		
		Player player = event.getPlayer();
		
		if (Toolkit.inArena(player)) {
			
			ItemStack item = player.getInventory().getItem(event.getNewSlot());

			if (item != null && item.getType() == XMaterial.COMPASS.parseMaterial().get()) {

				if (!CacheManager.getCompassUsers().contains(player.getName())) {

					new BukkitRunnable() {

						@Override
						public void run() {

							if (player != null) {

								String[] nearestData = Toolkit.getNearestPlayer(player, config.getInt("PlayerTracker.TrackBelowY"));

								if (player.getWorld().getPlayers().size() > 1 && nearestData != null) {

									Player nearestPlayer = Bukkit.getPlayer(nearestData[0]);
									double nearestDistance = Double.parseDouble(nearestData[1]);

									nearestDistance = Math.round(nearestDistance * 10.0) / 10.0;

									ItemMeta meta = item.getItemMeta();
									meta.setDisplayName(Toolkit.translate(config.getString("PlayerTracker.Message"))
											.replace("%nearestplayer%", nearestPlayer.getName())
											.replace("%distance%", String.valueOf(nearestDistance)));
									item.setItemMeta(meta);

									player.setCompassTarget(nearestPlayer.getLocation());

									CacheManager.getCompassUsers().add(player.getName());

								} else {

									ItemMeta meta = item.getItemMeta();
									meta.setDisplayName(Toolkit.translate(config.getString("PlayerTracker.NoneOnline")));
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
