package com.planetgallium.kitpvp.api;

import com.planetgallium.kitpvp.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Toolkit;;

public class EventListener implements Listener {

	private Arena arena;
	
	public EventListener(Game plugin) {
		this.arena = plugin.getArena();
	}
	
	@EventHandler
	public void onAbility(PlayerInteractEvent e) {

		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

			if (Toolkit.inArena(e.getPlayer())) {

				Player p = e.getPlayer();
				
				if (arena.getKits().hasKit(p.getName())) {

					ItemStack currentItem = Toolkit.getMainHandItem(p);
					Kit kit = arena.getKits().getKitOfPlayer(p.getName());

					if (currentItem.hasItemMeta() && currentItem.getItemMeta().hasDisplayName()) {

						Ability ability = kit.getAbilityFromActivator(currentItem);

						if (ability != null) {
							Bukkit.getPluginManager().callEvent(new PlayerAbilityEvent(p, ability));
							e.setCancelled(true);
						}

					}	
					
				}
				
			}
			
		}
		
	}

}
