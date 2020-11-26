package com.planetgallium.kitpvp.listener;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
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
	
	private Resource config;
	private int soupBoost;
	
	public SoupListener(Game plugin) {
		this.config = plugin.getResources().getConfig();
		this.soupBoost = plugin.getConfig().getInt("Soups.RegenAmount");
	}
	
	@EventHandler
	public void onDamage(PlayerDeathEvent e) {
		
		Player victim = e.getEntity();
		
		if (Toolkit.inArena(victim)) {
			
			if (victim.getKiller() != null && victim.getKiller() instanceof Player) {
				
				Player killer = victim.getKiller();
				
				if (config.getBoolean("Kill.SoupReward.Enabled")) {
					
					if (killer.hasPermission("kp.soupreturn")) {
					
						new BukkitRunnable() {
							
							@Override
							public void run() {
								
								if (killer != null) { // incase they logged off
									
									int count = 0;
									for (int i = 0; i < 36; i++) {
										if (killer.getInventory().getItem(i) == null) {
											count++;
										}
									}
									
									if (count < config.getInt("Kill.SoupReward.Amount")) {
										killer.sendMessage(config.getString("Kill.SoupReward.NoSpace").replace("%amount%", String.valueOf((config.getInt("Kill.SoupReward.Amount") - count))));
									} else {
										count = config.getInt("Kill.SoupReward.Amount");
									}

									ItemStack soup = XMaterial.MUSHROOM_STEW.parseItem();
									ItemMeta soupMeta = soup.getItemMeta();

									soupMeta.setDisplayName(config.getString("Soups.Name"));
									soupMeta.setLore(Toolkit.colorizeList(config.getStringList("Soups.Lore")));

									soup.setItemMeta(soupMeta);

									for (int r = 0; r < count; r++) {
										killer.getInventory().addItem(soup);
									}
									
								}
								
							}
							
						}.runTaskLater(Game.getInstance(), config.getInt("Kill.SoupReward.Delay") * 20);
						
					}
					
				}
				
			}
			
		}
		
	}
	
	@EventHandler
	public void useSoup(PlayerInteractEvent e) {
	    
		if (config.getBoolean("Soups.Enabled")) {
			
			Player p = e.getPlayer();
			
			if (Toolkit.inArena(p)) {

				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

					if (Toolkit.getMainHandItem(p).getType() == XMaterial.MUSHROOM_STEW.parseMaterial() || Toolkit.getOffhandItem(p).getType() == XMaterial.MUSHROOM_STEW.parseMaterial()) {

						e.setCancelled(true);

						if (p.getHealth() < 20.0) {

							p.setHealth(p.getHealth() + (double) soupBoost >= 20.0 ? 20.0 : p.getHealth() + (double) soupBoost);
							p.playSound(p.getLocation(), XSound.matchXSound(config.getString("Soups.Sound")).get().parseSound(), 1, (float) config.getInt("Soups.Pitch"));

							if (Toolkit.getMainHandItem(p).getType() == XMaterial.MUSHROOM_STEW.parseMaterial()) {

								if (config.getBoolean("Soups.RemoveAfterUse")) {

									Toolkit.setMainHandItem(p, new ItemStack(XMaterial.AIR.parseItem()));

								} else {

									Toolkit.setMainHandItem(p, new ItemStack(XMaterial.BOWL.parseItem()));

								}

							} else if (Toolkit.getOffhandItem(p).getType() == XMaterial.MUSHROOM_STEW.parseMaterial()) {

								if (config.getBoolean("Soups.RemoveAfterUse")) {

									Toolkit.setOffhandItem(p, new ItemStack(XMaterial.AIR.parseItem()));

								} else {

									Toolkit.setOffhandItem(p, new ItemStack(XMaterial.BOWL.parseItem()));

								}

							}

						}

					}

				}
				
			}
			
		}
		
	}
	
}
