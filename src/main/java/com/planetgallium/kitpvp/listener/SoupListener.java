package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.util.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.planetgallium.kitpvp.Game;

public class SoupListener implements Listener {

	private final Game plugin;
	private final Resource config;
	private final int soupBoost;
	private final ItemStack soup;

	public SoupListener(Game plugin) {
		this.plugin = plugin;
		this.config = plugin.getResources().getConfig();
		// TODO: see if doing /kp reload works on this
		this.soupBoost = plugin.getConfig().getInt("Soups.RegenAmount");

		// TODO: see if doing /kp reload works on this
		this.soup = Toolkit.safeItemStack("MUSHROOM_STEW");
		ItemMeta soupMeta = soup.getItemMeta();
		soupMeta.setDisplayName(config.fetchString("Soups.Name"));
		soupMeta.setLore(Toolkit.colorizeList(config.getStringList("Soups.Lore")));
		soup.setItemMeta(soupMeta);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player victim = e.getEntity();

		if (!Toolkit.inArena(victim)) {
			return;
		}
			
		if (victim.getKiller() != null && config.getBoolean("Kill.SoupReward.Enabled")) {
			Player killer = victim.getKiller();

			if (!killer.hasPermission("kp.soupreturn")) {
				return;
			}

			new BukkitRunnable() {
				@Override
				public void run() {
					if (killer.isOnline()) {
						insertSoupRewardToInventory(killer);
					}
				}
			}.runTaskLater(plugin, config.getInt("Kill.SoupReward.Delay") * 20L);
		}
	}

	private void insertSoupRewardToInventory(Player killer) {
		for (int i = config.getInt("Kill.SoupReward.Amount"); i > 0; i--) {
			if (killer.getInventory().firstEmpty() == -1) {
				killer.sendMessage(config.fetchString("Kill.SoupReward.NoSpace")
						.replace("%amount%", String.valueOf(i)));
				break;
			} else {
				killer.getInventory().addItem(soup);
			}
		}
	}
	
	@EventHandler
	public void onSoupUse(PlayerInteractEvent e) {
		if (!config.getBoolean("Soups.Enabled")) {
			return;
		}

		Player p = e.getPlayer();

		if (Toolkit.inArena(p) &&
				(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			if (Toolkit.hasMatchingMaterial(Toolkit.getHandItemForInteraction(e), "MUSHROOM_STEW")) {
				e.setCancelled(true);

				if (p.getHealth() < 20.0) {
					p.setHealth(Math.min(p.getHealth() + (double) soupBoost, 20.0));

					Toolkit.playSoundToPlayer(p, config.fetchString("Soups.Sound"),
							config.getInt("Soups.Pitch"));
					Toolkit.setHandItemForInteraction(e,config.getBoolean("Soups.RemoveAfterUse") ?
							Toolkit.safeItemStack("AIR") : Toolkit.safeItemStack("BOWL"));
				}
			}
		}
	}
	
}
