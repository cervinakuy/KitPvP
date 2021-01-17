package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.Ability;
import com.planetgallium.kitpvp.api.Kit;
import com.planetgallium.kitpvp.util.CacheManager;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import com.planetgallium.kitpvp.api.PlayerAbilityEvent;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;

public class AbilityListener implements Listener {

	private Game plugin;
	private Arena arena;
	private Resources resources;
	
	public AbilityListener(Game plugin) {
		this.plugin = plugin;
		this.arena = plugin.getArena();
		this.resources = plugin.getResources();
	}
	
	@EventHandler
	public void onAbility(PlayerAbilityEvent e) {

		Player p = e.getPlayer();

		if (plugin.hasWorldGuard()) {
			RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
			LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(e.getPlayer());
			ApplicableRegionSet set = query.getApplicableRegions(localPlayer.getLocation());

			if (!set.testState(null, Flags.PVP)) {
				p.sendMessage(resources.getMessages().getString("Messages.Error.PVP"));
				return;
			}
		}

		Kit kit = arena.getKits().getKitOfPlayer(p.getName());
		Ability ability = e.getAbility();

		String abilityPermission = "kp.ability." + kit.getName().toLowerCase();
		if (!p.hasPermission(abilityPermission)) {
			p.sendMessage(resources.getMessages().getString("Messages.General.Permission").replace("%permission%", abilityPermission));
			return;
		}

		if (arena.getCooldowns().isOnCooldown(p, ability)) {
			int timeLastUsedSeconds = CacheManager.getPlayerAbilityCooldowns(p.getName()).get(ability.getName()).intValue();
			int cooldownSeconds = ability.getCooldown().toSeconds();
			p.sendMessage(resources.getMessages().getString("Messages.Error.CooldownAbility")
					.replace("%cooldown%", arena.getCooldowns().getFormattedCooldown(timeLastUsedSeconds, cooldownSeconds)));
			return;
		}

		if (ability.getMessage() != null)
			p.sendMessage(Toolkit.translate(ability.getMessage()));

		if (ability.getSound() != null)
			p.playSound(p.getLocation(), ability.getSound(), ability.getSoundVolume(), ability.getSoundPitch());

		if (ability.getEffects().size() > 0)
			ability.getEffects().stream().forEach(effect -> p.addPotionEffect(effect));

		if (ability.getCommands().size() > 0)
			Toolkit.runCommands(p, ability.getCommands(), "none", "none");

		if (ability.getCooldown() == null) {
			if (Toolkit.getMainHandItem(p).getAmount() == 1) {
				ItemStack emptyItem = Toolkit.getMainHandItem(p);
				emptyItem.setAmount(0);
				Toolkit.setMainHandItem(p, emptyItem);
			} else {
				Toolkit.getMainHandItem(p).setAmount(Toolkit.getMainHandItem(p).getAmount() - 1);
			}
		} else {
			arena.getCooldowns().setAbilityCooldown(p.getName(), ability.getName());
		}
		
	}
	
}
