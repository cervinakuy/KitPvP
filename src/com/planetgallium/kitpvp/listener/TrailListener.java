package com.planetgallium.kitpvp.listener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Sounds;
import com.planetgallium.kitpvp.util.Toolkit;

public class TrailListener implements Listener {

	private Resources resources;
	
	public TrailListener(Resources resources) {
		this.resources = resources;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
	
		Player p = e.getPlayer();
		
		if (Toolkit.inArena(p) && Toolkit.getMainHandItem(p).getType() == Material.COAL) {
			
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
				if (p.hasPermission("kp.ability.bomber")) {
				
					if (resources.getAbilities().getBoolean("Abilities.Bomber.Message.Enabled")) {
						p.sendMessage(Config.tr(resources.getAbilities().getString("Abilities.Bomber.Message.Message")));
					}
				
					p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
					
					//
					
					ItemStack coal = new ItemStack(Material.COAL, Toolkit.getMainHandItem(p).getAmount());
					ItemMeta coalMeta = coal.getItemMeta();
					
					coalMeta.setDisplayName(Config.tr(resources.getAbilities().getString("Abilities.Bomber.Item.Name")));
					
					coal.setItemMeta(coalMeta);
					coal.setAmount(coal.getAmount() - 1);
					
					Toolkit.setMainHandItem(p, coal);
					
					//
					
					new BukkitRunnable() {
				    	
						public int t = 5;
					
						@Override
						public void run() {
								
							if (t != 0 && p.getGameMode() != GameMode.SPECTATOR) {
									
								Location loc = p.getLocation();
								World world = loc.getWorld();
													
								world.spawn(p.getLocation(), TNTPrimed.class);
								
								p.playSound(p.getLocation(), Sounds.valueOf(resources.getAbilities().getString("Abilities.Bomber.Sound.Sound")).bukkitSound(), 1, (int) resources.getAbilities().getInt("Abilities.Bomber.Sound.Pitch"));
								
								t--;
									
							} else {
								
								cancel();
								
							}
							
						}
						
					}.runTaskTimer(Game.getInstance(), 0L, 20L);
					
				}
				
			}
			
		}
		
	}
	
}
