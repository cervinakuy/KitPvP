package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Resources;
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

	private final Game plugin;
	private final Arena arena;
	private final Resources resources;
	
	public TrackerListener(Game plugin) {
		this.plugin = plugin;
		this.arena = plugin.getArena();
		this.resources = plugin.getResources();
	}
	
	@EventHandler
	public void onCompassHeld(PlayerItemHeldEvent e) {
		if (!Toolkit.inArena(e.getPlayer())) {
			return;
		}

		Player p = e.getPlayer();
		ItemStack itemHeld = p.getInventory().getItem(e.getNewSlot());

		if (itemHeld != null && Toolkit.hasMatchingMaterial(itemHeld, "COMPASS")) {

			new BukkitRunnable() {
				@Override
				public void run() {
					// if the player using the compass leaves the server or no longer has a kit
					if (!p.isOnline() || !arena.getKits().playerHasKit(p.getName())) {
						cancel();
						return;
					}

					if (resources.getConfig().getBoolean("PlayerTracker.RefreshOnlyWhenHeld")) {
						if (!Toolkit.eitherHandHasMaterial(p, "COMPASS")) {
							cancel();
							return;
						}
					}

					String[] nearestPlayerData = null;
					if (p.getWorld().getPlayers().size() == 1) {
						cancel();
					} else {
						nearestPlayerData = Toolkit.getNearestPlayer(p,
												 resources.getConfig().getInt("PlayerTracker.TrackBelowY"));
					}

					updateTrackingCompass(p, itemHeld, nearestPlayerData);
				}
			}.runTaskTimer(plugin, 0L, 20L);
		}
	}

	private void updateTrackingCompass(Player player, ItemStack compass, String[] nearestPlayerData) {
		ItemMeta compassMeta = compass.getItemMeta();

		if (nearestPlayerData != null) {
			Player nearestPlayer = Toolkit.getPlayer(player.getWorld(), nearestPlayerData[0]);
			double nearestPlayerDistance = Toolkit.round(Double.parseDouble(nearestPlayerData[1]), 1);

			if (nearestPlayer != null && nearestPlayer.isOnline()) {
				compassMeta.setDisplayName(resources.getConfig().fetchString("PlayerTracker.Message")
												   .replace("%nearestplayer%", nearestPlayer.getName())
												   .replace("%distance%", String.valueOf(nearestPlayerDistance)));
				player.setCompassTarget(nearestPlayer.getLocation());
			}
		} else {
			compassMeta.setDisplayName(resources.getConfig().fetchString("PlayerTracker.NoneOnline"));
		}

		compass.setItemMeta(compassMeta);
	}
	
}
