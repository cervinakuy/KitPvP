package com.planetgallium.kitpvp.listener;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Sounds;
import com.planetgallium.kitpvp.util.Toolkit;

public class InteractListener implements Listener {

	private Resources resources;
	
	public InteractListener(Resources resources) {
		this.resources = resources;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) {
		
		Player p = e.getPlayer();
		
		if (Toolkit.inArena(p) && Toolkit.getMainHandItem(p).getType() == Material.BLAZE_ROD && e.getRightClicked().getType() == EntityType.PLAYER) {
			
			if (p.hasPermission("kp.ability.thunderbolt")) {
				
				Player damagedPlayer = (Player) e.getRightClicked();
				
				if (Game.getInstance().getArena().getKits().hasKit(damagedPlayer.getName())) {
					
					ItemStack strike = new ItemStack(Toolkit.getMainHandItem(p).getType(), Toolkit.getMainHandItem(p).getAmount());
					ItemMeta strikeMeta = strike.getItemMeta();
					strikeMeta.setDisplayName(Config.tr(resources.getAbilities().getString("Abilities.Thunderbolt.Item.Name")));
					strike.setItemMeta(strikeMeta);
					
					p.playSound(p.getLocation(), Sounds.valueOf(resources.getAbilities().getString("Abilities.Thunderbolt.Sound.Sound")).bukkitSound(), 1, (int) resources.getAbilities().getInt("Abilities.Thunderbolt.Sound.Pitch"));
					
					if (resources.getAbilities().getBoolean("Abilities.Thunderbolt.Message.Enabled")) {
						p.sendMessage(Config.tr(resources.getAbilities().getString("Abilities.Thunderbolt.Message.Message").replace("%player%", e.getRightClicked().getName()).replace("%prefix%", Game.getInstance().getPrefix())));
					}
					
					p.getWorld().strikeLightningEffect(e.getRightClicked().getLocation());
					damagedPlayer.damage(4.0);
					damagedPlayer.setFireTicks(5 * 20);
					
					strike.setAmount(strike.getAmount() - 1);
					Toolkit.setMainHandItem(p, strike);
					
				}
				
			}
			
		} else if (Toolkit.getMainHandItem(p).getType() == Material.GHAST_TEAR && e.getRightClicked().getType() == EntityType.PLAYER) {
			
			if (p.hasPermission("kp.ability.vampire")) {
				
				Player damagedPlayer = (Player) e.getRightClicked();
				
				if (Game.getInstance().getArena().getKits().hasKit(damagedPlayer.getName())) {
					
					ItemStack suck = new ItemStack(Toolkit.getMainHandItem(p).getType(), Toolkit.getMainHandItem(p).getAmount());
					ItemMeta suckMeta = suck.getItemMeta();
					suckMeta.setDisplayName(Config.tr(resources.getAbilities().getString("Abilities.Vampire.Item.Name")));
					suck.setItemMeta(suckMeta);
					
					p.playSound(p.getLocation(), Sounds.valueOf(resources.getAbilities().getString("Abilities.Vampire.Sound.Sound")).bukkitSound(), 1, (int) resources.getAbilities().getInt("Abilities.Vampire.Sound.Pitch"));
					
					if (resources.getAbilities().getBoolean("Abilities.Vampire.Message.Enabled")) {
						p.sendMessage(Config.tr(resources.getAbilities().getString("Abilities.Vampire.Message.Message").replace("%player%", e.getRightClicked().getName()).replace("%prefix%", Game.getInstance().getPrefix())));
					}
			
					damagedPlayer.damage(4.0);
					damagedPlayer.playSound(damagedPlayer.getLocation(), Sounds.DRINK.bukkitSound(), 1, -1);
					
					if (p.getHealth() <= 16.0) {
						
						p.setHealth(p.getHealth() + 4.0);
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 2, 1));
						
					}
					
					suck.setAmount(suck.getAmount() - 1);
					Toolkit.setMainHandItem(p, suck);
					
				}
				
			}
			
		}
		
	}
	
}
