package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.util.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.planetgallium.kitpvp.Game;

public class SoupListener implements Listener {

	private Game plugin;
	private Resources resources;
	private int soupBoost;
	
	public SoupListener(Game plugin) {
		this.plugin = plugin;
		this.resources = plugin.getResources();

		this.soupBoost = plugin.getConfig().getInt("Soups.RegenAmount");
	}
	
	@EventHandler
	public void onDamage(PlayerDeathEvent e) {
		
		Player victim = e.getEntity();
		
		if (Toolkit.inArena(victim)) {
			
			if (victim.getKiller() != null && victim.getKiller() instanceof Player) {
				
				Player killer = victim.getKiller();
				
				if (Config.getB("GiveSoupOnKill.Enabled")) {
					
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
									
									if (count < Config.getI("GiveSoupOnKill.Amount")) {
										killer.sendMessage(Config.getS("GiveSoupOnKill.NoSpace").replace("%amount%", String.valueOf((Config.getI("GiveSoupOnKill.Amount") - count))));
									}
									else{
										count = Config.getI("GiveSoupOnKill.Amount");
									}
									
									for (int r = 0; r < count; r++) {
										killer.getInventory().addItem(new ItemStack(XMaterial.MUSHROOM_STEW.parseItem()));
									}
									
								}
								
							}
							
						}.runTaskLater(plugin, Config.getI("GiveSoupOnKill.Delay") * 20);
						
					}
					
				}
				
			}
			
		}
		
	}
	
	@EventHandler
	public void useSoup(PlayerInteractEvent e) {
	    
		if (Config.getB("Soups.Enabled")) {
			
			Player p = e.getPlayer();
			
			if (Toolkit.inArena(p)) {

				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

					if (Toolkit.getMainHandItem(p).getType() == XMaterial.MUSHROOM_STEW.parseMaterial() || Toolkit.getOffhandItem(p).getType() == XMaterial.MUSHROOM_STEW.parseMaterial()) {

						e.setCancelled(true);

						if (p.getHealth() < 20.0) {

							p.setHealth(p.getHealth() + (double) soupBoost >= 20.0 ? 20.0 : p.getHealth() + (double) soupBoost);
							p.playSound(p.getLocation(), XSound.matchXSound(Config.getS("Soups.Sound")).get().parseSound(), 1, (float) Config.getI("Soups.Pitch"));

							if (Toolkit.getMainHandItem(p).getType() == XMaterial.MUSHROOM_STEW.parseMaterial()) {

								if (Config.getB("Soups.RemoveAfterUse")) {

									Toolkit.setMainHandItem(p, new ItemStack(XMaterial.AIR.parseItem()));

								} else {

									Toolkit.setMainHandItem(p, new ItemStack(XMaterial.BOWL.parseItem()));

								}

							} else if (Toolkit.getOffhandItem(p).getType() == XMaterial.MUSHROOM_STEW.parseMaterial()) {

								if (Config.getB("Soups.RemoveAfterUse")) {

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
