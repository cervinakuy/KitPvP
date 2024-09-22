package com.planetgallium.kitpvp.listener;

import com.cryptomorin.xseries.XMaterial;
import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.Kit;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.game.Utilities;
import com.planetgallium.kitpvp.util.CacheManager;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class ItemListener implements Listener {

	private final Game plugin;
	private final Arena arena;
	private final Utilities utilities;
	private final Resources resources;
	private final Resource config;
	private final Resource abilities;
	
	public ItemListener(Game plugin) {
		this.plugin = plugin;
		this.arena = plugin.getArena();
		this.utilities = plugin.getArena().getUtilities();
		this.resources = plugin.getResources();
		this.config = resources.getConfig();
		this.abilities = resources.getAbilities();
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (Toolkit.inArena(p) &&
				(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {

			ItemStack interactedItem = Toolkit.getHandItemForInteraction(e);
			ItemMeta interactedItemMeta = interactedItem.getItemMeta();

			if (isAbility(p, interactedItem, "Kangaroo", "SADDLE")) {
				kangarooAbility(e, p, interactedItem);

			} else if (isAbility(p, interactedItem, "Soldier", "IRON_HOE")) {
				soldierAbility(e, p, interactedItem);

			} else if (isAbility(p, interactedItem, "Warper", "ENDER_EYE")) {
				warperAbility(e, p, interactedItem);

			} else if (isAbility(p, interactedItem, "Ninja", "NETHER_STAR")) {
				ninjaAbility(e, p, interactedItem);

			} else if (isAbility(p, interactedItem, "Bomber", "COAL")) {
				bomberAbility(e, p, interactedItem);

			} else if (isAbility(p, interactedItem, "Witch", "GLASS_BOTTLE")) {
				e.setCancelled(true);
				CacheManager.getPotionSwitcherUsers().add(p.getName());
				Toolkit.setHandItemForInteraction(e, createWitchPotion());

			} else if (isSplashPotion(interactedItem)) {
				witchAbility(e, p, interactedItem, interactedItemMeta);

			} else if ((Toolkit.hasMatchingMaterial(interactedItem, "SLIME_BALL") ||
					Toolkit.hasMatchingMaterial(interactedItem, "MAGMA_CREAM"))
						&& interactedItem.hasItemMeta()) {
				archerAbility(e, p, interactedItem, interactedItemMeta);

			} else if (Toolkit.hasMatchingMaterial(interactedItem, "TNT")) {
				specialTNT(e, p, interactedItem);

			}

			/* Kit Item and custom Arena Items */

			if (config.contains("Items.Kits") &&
					Toolkit.hasMatchingMaterial(interactedItem, config.fetchString("Items.Kits.Material"))) {

				if (Toolkit.hasMatchingDisplayName(interactedItem, config.fetchString("Items.Kits.Name"))) {
					Toolkit.runCommands(p, config.getStringList("Items.Kits.Commands"),
							"none", "none");

					if (config.getBoolean("Items.Kits.Menu")) {
						arena.getMenus().getKitMenu().open(p);
					}

					e.setCancelled(true);
				}

			} else if (interactedItem.hasItemMeta()) {
				ConfigurationSection items = config.getConfigurationSection("Items");

				for (String identifier : items.getKeys(false)) {
					String itemPath = "Items." + identifier;

					if (!config.getBoolean(itemPath + ".Enabled")) {
						return;
					}

					String itemMaterialName = config.fetchString(itemPath + ".Material");
					if (Toolkit.hasMatchingMaterial(interactedItem, itemMaterialName)) {
						if (Toolkit.hasMatchingDisplayName(interactedItem, config.fetchString(itemPath + ".Name"))) {
							Toolkit.runCommands(p, config.getStringList(itemPath + ".Commands"),
									"none", "none");
							e.setCancelled(true);
						}
					}
				}
			}
			
		}
	}

	public boolean isAbility(Player p, ItemStack interactedItem, String kitName, String abilityMaterialName) {
		return interactedItem.getType() == Toolkit.safeMaterial(abilityMaterialName) &&
				isBuiltInAbilityItem(p, kitName, interactedItem);
	}

	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) {
		Player damager = e.getPlayer();

		if (Toolkit.inArena(damager) && e.getRightClicked().getType() == EntityType.PLAYER) {
			ItemStack interactedItem = Toolkit.getHandItemForInteraction(e);
			Player damagedPlayer = (Player) e.getRightClicked();

			if (config.getBoolean("Arena.NoKitProtection")) {
				if (!arena.getKits().playerHasKit(damagedPlayer.getName())) {
					return;
				}
			}

			if (isAbility(damager, interactedItem, "Thunderbolt", "BLAZE_ROD")) {
				thunderboltAbility(e, damager, damagedPlayer, interactedItem);

			} else if (isAbility(damager, interactedItem, "Vampire", "GHAST_TEAR")) {
				vampireAbliity(e, damager, damagedPlayer, interactedItem);
			}
		}
	}

	private boolean isBuiltInAbilityItem(Player p, String kitName, ItemStack interactedItem) {
		if (Toolkit.hasMatchingDisplayName(interactedItem,
				abilities.fetchString("Abilities." + kitName + ".Item.Name"))) {
			String abilityPermission = "kp.ability." + kitName.toLowerCase();

			if (p.hasPermission(abilityPermission)) {
				if (config.getBoolean("Arena.AbilitiesRequireKit") && !arena.getKits().playerHasKit(p.getName())) {
					p.sendMessage(resources.getMessages().fetchString("Messages.Error.Kit"));
					return false;
				}

				return utilities.isCombatActionPermittedInRegion(p);
			} else {
				p.sendMessage(resources.getMessages().fetchString("Messages.General.Permission")
						.replace("%permission%", abilityPermission));
			}
		}

		return false;
	}

	private void useBuiltInAbilityItem(Event e, Player p, Player clicked, ItemStack abilityItem,
									   String kitName) {
		String abilityPrefix = "Abilities." + kitName;

		if (abilities.getBoolean(abilityPrefix + ".Message.Enabled")) {
			String abilityMessage = abilities.fetchString(abilityPrefix + ".Message.Message");
			if (clicked != null) abilityMessage = abilityMessage.replace("%player%", clicked.getName());
			p.sendMessage(abilityMessage);
		}

		abilityItem.setAmount(abilityItem.getAmount() - 1);
		Toolkit.setHandItemForInteraction(e, abilityItem);

		if (abilities.getBoolean(abilityPrefix + ".Sound.Enabled")) {
			Toolkit.playSoundToPlayer(p, abilities.fetchString(abilityPrefix + ".Sound.Sound"),
					abilities.getInt(abilityPrefix + ".Sound.Pitch"));
		}
	}

	private void kangarooAbility(PlayerInteractEvent e, Player p, ItemStack abilityItem) {
		p.setVelocity(new Vector(0, 2, 0));
		p.setFallDistance(config.getBoolean("Arena.PreventFallDamage") ? -1000000 : -30);

		useBuiltInAbilityItem(e, p, p, abilityItem, "Kangaroo");
	}

	private void soldierAbility(PlayerInteractEvent e, Player p, ItemStack abilityItem) {
		Snowball ammo = p.launchProjectile(Snowball.class);
		ammo.setCustomName("bullet");
		ammo.setVelocity(p.getLocation().getDirection().multiply(2.5));

		useBuiltInAbilityItem(e, p, p, abilityItem, "Soldier");
	}

	private void specialTNT(PlayerInteractEvent e, Player p, ItemStack abilityItem) {
		if (config.getBoolean("TNT.Enabled") &&
				Toolkit.hasMatchingDisplayName(abilityItem, config.fetchString("TNT.Name"))) {

			if (!utilities.isCombatActionPermittedInRegion(p)) {
				return;
			}

			Location handLocation = p.getLocation();
			handLocation.setY(handLocation.getY() + 1.0);
			Vector direction = handLocation.getDirection();

			Entity entity = p.getWorld().spawn(handLocation, TNTPrimed.class);
			entity.setVelocity(direction.multiply(1.5));
			entity.setCustomName(p.getName());

			e.setCancelled(true);

			useBuiltInAbilityItem(e, p, p, abilityItem, "none");
		}
	}

	private void warperAbility(PlayerInteractEvent e, Player p, ItemStack abilityItem) {
		e.setCancelled(true);

		String[] nearestData = Toolkit.getNearestPlayer(p, config.getInt("PlayerTracker.TrackBelowY"));

		if (Bukkit.getServer().getOnlinePlayers().size() > 1 && nearestData != null) {
			String nearestPlayerUsername = nearestData[0];
			Location nearestPlayerLocation = Bukkit.getPlayer(nearestPlayerUsername).getLocation();

			if (arena.getKits().playerHasKit(nearestPlayerUsername)) {
				new BukkitRunnable() {
					@Override
					public void run() {
						p.teleport(nearestPlayerLocation);
					}
				}.runTaskLater(plugin, 5L);

				p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 5));
				p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 5));

				useBuiltInAbilityItem(e, p, p, abilityItem, "Warper");
			} else {
				p.sendMessage(resources.getMessages().fetchString("Messages.Other.Players"));
			}
		} else {
			p.sendMessage(resources.getMessages().fetchString("Messages.Other.Players"));
		}
	}

	private void ninjaAbility(PlayerInteractEvent e, Player p, ItemStack abilityItem) {
		ItemStack previousHelmet = p.getInventory().getHelmet();
		ItemStack previousChestplate = p.getInventory().getChestplate();
		ItemStack previousLeggings = p.getInventory().getLeggings();
		ItemStack previousBoots = p.getInventory().getBoots();

		p.getInventory().setArmorContents(null);

		for (Entity entity : p.getNearbyEntities(3, 3, 3)) {
			if (entity instanceof Player) {
				Player nearby = (Player) entity;
				nearby.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
				nearby.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
			}
		}

		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 0));

		useBuiltInAbilityItem(e, p, p, abilityItem, "Ninja");

		new BukkitRunnable() {
			@Override
			public void run() {
				if (arena.getKits().playerHasKit(p.getName())) {
					p.getInventory().setHelmet(previousHelmet);
					p.getInventory().setChestplate(previousChestplate);
					p.getInventory().setLeggings(previousLeggings);
					p.getInventory().setBoots(previousBoots);
				}
			}
		}.runTaskLater(plugin, 100L);
	}

	private void bomberAbility(PlayerInteractEvent e, Player p, ItemStack abilityItem) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 2));

		new BukkitRunnable() {
			public int t = 5;

			@Override
			public void run() {
				if (t != 0 && p.getGameMode() != GameMode.SPECTATOR && arena.getKits().playerHasKit(p.getName())) {
					Entity entity = p.getWorld().spawn(p.getLocation(), TNTPrimed.class);
					entity.setCustomName(p.getName());

					Toolkit.playSoundToPlayer(p, abilities.fetchString("Abilities.Bomber.Sound.Sound"),
							abilities.getInt("Abilities.Bomber.Sound.Pitch"));
					t--;
				} else {
					cancel();
				}
			}
		}.runTaskTimer(plugin, 0L, 20L);

		useBuiltInAbilityItem(e, p, null, abilityItem, "Bomber");
	}

	private void archerAbility(PlayerInteractEvent e, Player p, ItemStack abilityItem, ItemMeta abilityItemMeta) {
		boolean shouldToggleOn = Toolkit.hasMatchingMaterial(abilityItem, "SLIME_BALL");
		String firePath = shouldToggleOn ? "Fire" : "NoFire";
		String fireItem = shouldToggleOn ? "MAGMA_CREAM" : "SLIME_BALL";

		if (Toolkit.hasMatchingDisplayName(abilityItem, abilities.fetchString("Abilities.Archer.Item.Fire")) ||
				Toolkit.hasMatchingDisplayName(abilityItem, abilities.fetchString("Abilities.Archer.Item.NoFire"))) {
			abilityItem.setType(XMaterial.matchXMaterial(fireItem).get().parseMaterial());
			abilityItemMeta.setDisplayName(abilities.fetchString("Abilities.Archer.Item." + firePath));
			abilityItem.setItemMeta(abilityItemMeta);

			Toolkit.setHandItemForInteraction(e, abilityItem);

			if (abilities.getBoolean("Abilities.Archer.Message.Enabled")) {
				p.sendMessage(abilities.fetchString("Abilities.Archer.Message." + firePath));
			}

			Toolkit.playSoundToPlayer(p, "UI_BUTTON_CLICK", 1);
		}
	}

	private void witchAbility(PlayerInteractEvent e, Player p, ItemStack potionItem, ItemMeta abilityItemMeta) {
		if (potionItem.hasItemMeta() && abilityItemMeta.hasLore() &&
				Toolkit.singleLineLoreMatches(potionItem, "X")) {

			Toolkit.SlotWrapper slotUsed = Toolkit.getSlotUsedForInteraction(e);
			ItemStack randomizedWitchPotion = createWitchPotion();

			new BukkitRunnable() {
				@Override
				public void run() {
					Kit playerKit = arena.getKits().getKitOfPlayer(p.getName());
					if (playerKit != null && CacheManager.getPotionSwitcherUsers().contains(p.getName())) {
						slotUsed.placeItemInSlot(p, randomizedWitchPotion);

						if (abilities.getBoolean("Abilities.Witch.Message.Enabled")) {
							p.sendMessage(abilities.fetchString("Abilities.Witch.Message.Message"));
						}

						Toolkit.playSoundToPlayer(p, abilities.fetchString("Abilities.Witch.Sound.Sound"),
								abilities.getInt("Abliities.Witch.Sound.Pitch"));

						CacheManager.getPotionSwitcherUsers().add(p.getName());
					}
				}
			}.runTaskLater(plugin, 5 * 20L);
		}
	}

	private void thunderboltAbility(PlayerInteractEntityEvent e, Player damager, Player damagedPlayer,
									ItemStack abilityItem) {
		damager.getWorld().strikeLightningEffect(e.getRightClicked().getLocation());
		damagedPlayer.damage(4.0);
		damagedPlayer.setFireTicks(5 * 20);

		useBuiltInAbilityItem(e, damager, damagedPlayer, abilityItem, "Thunderbolt");
	}

	private void vampireAbliity(PlayerInteractEntityEvent e, Player damager, Player damagedPlayer,
								ItemStack abilityItem) {
		damagedPlayer.damage(4.0);
		Toolkit.playSoundToPlayer(damagedPlayer, "ENTITY_GENERIC_DRINK", -1);

		if (damager.getHealth() <= 16.0) {
			damager.setHealth(damager.getHealth() + 4.0);
			damager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 2, 1));
		}

		useBuiltInAbilityItem(e, damager, damagedPlayer, abilityItem, "Vampire");
	}

	@EventHandler
	public void onProjectileHitsEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getCause() == DamageCause.PROJECTILE) {

			if (e.getDamager() instanceof Snowball) {
				Player damagedPlayer = (Player) e.getEntity();
				Snowball snowball = (Snowball) e.getDamager();
				hitBySnowball(damagedPlayer, snowball);

			} else if (e.getDamager() instanceof Egg) {
				Player damagedPlayer = (Player) e.getEntity();
				Egg egg = (Egg) e.getDamager();
				hitByEgg(damagedPlayer, egg);
			}
		}
	}

	private void hitBySnowball(Player damagedPlayer, Snowball snowball) {
		if (snowball.getCustomName() != null && snowball.getCustomName().equals("bullet")) {
			if (Toolkit.inArena(damagedPlayer) && arena.getKits().playerHasKit(damagedPlayer.getName())) {
				damagedPlayer.damage(4.5);
			}
		}
	}

	private void hitByEgg(Player damagedPlayer, Egg egg) {
		if (Toolkit.hasMatchingDisplayName(egg.getItem(), abilities.fetchString("Abilities.Trickster.Item.Name"))) {
			if (Toolkit.inArena(damagedPlayer) && arena.getKits().playerHasKit(damagedPlayer.getName())) {

				if (egg.getShooter() instanceof Player) {
					Player shooter = (Player) egg.getShooter();
					Location shooterLocation = shooter.getLocation();

					if (!utilities.isCombatActionPermittedInRegion(damagedPlayer)) {
						shooter.sendMessage(resources.getMessages().fetchString("Messages.Error.PVP"));
						return;
					}

					shooter.teleport(damagedPlayer);
					damagedPlayer.teleport(shooterLocation);

					if (abilities.getBoolean("Abilities.Trickster.Message.Enabled")) {
						shooter.sendMessage(abilities.fetchString("Abilities.Trickster.Message.Message")
								.replace("%player%", damagedPlayer.getName()));
					}

					if (abilities.getBoolean("Abilities.Trickster.Sound.Enabled")) {
						Toolkit.playSoundToPlayer(shooter,
								abilities.fetchString("Abilities.Trickster.Sound.Sound"),
								abilities.getInt("Abilities.Trickster.Sound.Pitch"));
						Toolkit.playSoundToPlayer(damagedPlayer,
								abilities.fetchString("Abilities.Trickster.Sound.Sound"),
								abilities.getInt("Abilities.Trickster.Sound.Pitch"));
					}
				}
			}
		}
	}

	@EventHandler
	public void onBowShot(EntityShootBowEvent e) {
		if (e.getEntity() instanceof Player && Toolkit.inArena(e.getEntity())) {
			Player p = (Player) e.getEntity();

			if (!p.hasPermission("kp.ability.archer")) {
				return;
			}

			int ammoSlot = getItemByMeta(Material.MAGMA_CREAM,
					abilities.fetchString("Abilities.Archer.Item.Fire"), p);

			if (ammoSlot != -1) {
				ItemStack ammo = p.getInventory().getItem(ammoSlot);

				e.getProjectile().setFireTicks(1000);
				Toolkit.playSoundToPlayer(p, abilities.fetchString("Abilities.Archer.Sound.Sound"),
						abilities.getInt("Abilities.Archer.Sound.Pitch"));

				if (ammo.getAmount() == 1) {
					p.getInventory().setItem(ammoSlot, new ItemStack(Material.AIR));
				} else {
					ammo.setAmount(ammo.getAmount() - 1);
				}
			}
		}
	}

	private int getItemByMeta(Material type, String displayName, Player p) {
		int inventorySize = Toolkit.versionToNumber() == 18 ? 39 : 45;
		for (int i = 0; i <= inventorySize; i++) {
			ItemStack item = p.getInventory().getItem(i);
			if (item != null) {
				if (item.getType() == type && Toolkit.hasMatchingDisplayName(item, displayName)) {
					return i;
				}
			}
		}
		return -1;
	}

	private ItemStack createWitchPotion() {
		PotionType randomPotionType = randomPotionType();

		if (Toolkit.versionToNumber() >= 121) { // 1.21+
			ItemStack potionStack = new ItemStack(Material.SPLASH_POTION);
			Toolkit.appendToLore(potionStack, "X");
			PotionMeta potionMeta = (PotionMeta) potionStack.getItemMeta();

			if (potionMeta != null) {
				potionMeta.setBasePotionType(randomPotionType);
				potionStack.setItemMeta(potionMeta);
			}

			return potionStack;
		} else { // pre 1.21
			Potion potion = new Potion(randomPotionType, 1);
			potion.setSplash(true);

			ItemStack potionStack = potion.toItemStack(1);
			Toolkit.appendToLore(potionStack, "X");

			return potionStack;
		}
	}
	
	private PotionType randomPotionType() {
		PotionType potion = null;
		
		Random ran = new Random();
		int chance = ran.nextInt(100);
		
		if (chance < 10) {
			potion = PotionType.INSTANT_DAMAGE;
		} else if (chance < 20) {
			potion = PotionType.INSTANT_HEAL;
		} else if (chance < 40) {
			potion = PotionType.POISON;
		} else if (chance < 60) {
			potion = PotionType.REGEN;
		} else if (chance < 80) {
			potion = PotionType.SPEED;
		} else if (chance < 100) {
			potion = PotionType.SLOWNESS;
		}
		return potion;
	}

	public boolean isSplashPotion(ItemStack item) {
		int serverVersion = Toolkit.versionToNumber();
		return Toolkit.hasMatchingMaterial(item, serverVersion == 18 ? "POTION" : "SPLASH_POTION");
	}

}
