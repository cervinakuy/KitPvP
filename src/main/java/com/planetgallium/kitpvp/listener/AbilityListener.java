package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.Ability;
import com.planetgallium.kitpvp.util.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import com.planetgallium.kitpvp.api.PlayerAbilityEvent;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;

public class AbilityListener implements Listener {

	private final Arena arena;
	private final Resources resources;
	
	public AbilityListener(Game plugin) {
		this.arena = plugin.getArena();
		this.resources = plugin.getResources();
	}
	
	@EventHandler
	public void onAbility(PlayerAbilityEvent e) {
		Player p = e.getPlayer();

		if (!arena.getUtilities().isCombatActionPermittedInRegion(p)) {
			return;
		}

		Ability ability = e.getAbility();

		String abilityPermission = "kp.ability." + ability.getName().toLowerCase();
		if (!p.hasPermission(abilityPermission)) {
			p.sendMessage(resources.getMessages().fetchString("Messages.General.Permission")
					.replace("%permission%", abilityPermission));
			return;
		}

		Cooldown cooldownRemaining = arena.getCooldowns().getRemainingCooldown(p, ability);
		if (cooldownRemaining.toSeconds() > 0) {
			p.sendMessage(resources.getMessages().fetchString("Messages.Error.CooldownAbility")
					.replace("%cooldown%", cooldownRemaining.formatted(false)));
			return;
		}

		if (ability.getMessage() != null)
			p.sendMessage(Toolkit.translate(ability.getMessage()));

		if (ability.getSound() != null)
			p.playSound(p.getLocation(), ability.getSound(), ability.getSoundVolume(), ability.getSoundPitch());

		if (ability.getEffects().size() > 0)
			ability.getEffects().forEach(p::addPotionEffect);

		if (ability.getCommands().size() > 0)
			Toolkit.runCommands(p, ability.getCommands(), "none", "none");

		if (ability.getCooldown() == null) {
			ItemStack abilityItem = Toolkit.getHandItemForInteraction(e.getOriginalInteractionEvent());
			abilityItem.setAmount(abilityItem.getAmount() - 1);
		} else {
			arena.getCooldowns().setAbilityCooldown(p.getName(), ability.getName());
		}
	}

}
