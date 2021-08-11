package com.planetgallium.kitpvp.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.planetgallium.kitpvp.api.Kit;
import com.planetgallium.kitpvp.util.Resource;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;

public class ItemListener implements Listener {
	
	private final Arena arena;
	private final Resources resources;
	private final Resource config;
	private final Resource abilities;
	
	public ItemListener(Game plugin) {
		this.arena = plugin.getArena();
		this.resources = plugin.getResources();
		this.config = resources.getConfig();
		this.abilities = resources.getAbilities();
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {

		Player p = e.getPlayer();

		if (Toolkit.inArena(p) && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {

			ItemStack item = Toolkit.getMainHandItem(p);
			ItemMeta meta = item.getItemMeta();

			if (item.getType() == XMaterial.SADDLE.parseMaterial()) {

				if (isAbilityItem(p, "Kangaroo", item)) {

					p.setVelocity(new Vector(0, 2, 0));
					p.setFallDistance(config.getBoolean("Arena.PreventFallDamage") ? -1000000 : -30);

					useAbilityItem(p, p, item, "Kangaroo");

				}

			} else if (item.getType() == XMaterial.IRON_HOE.parseMaterial()) {

				if (isAbilityItem(p, "Soldier", item)) {

					Snowball ammo = p.launchProjectile(Snowball.class);
					ammo.setCustomName("bullet");
					ammo.setVelocity(p.getLocation().getDirection().multiply(2.5));

					useAbilityItem(p, p, item, "Soldier");

				}

			} else if (item.getType() == XMaterial.GLASS_BOTTLE.parseMaterial()) {

				if (isAbilityItem(p, "Witch", item)) {

					Toolkit.setMainHandItem(p, createWitchPotion());

				}

			} else if (item.getType() == XMaterial.TNT.parseMaterial()) {

				if (config.getBoolean("TNT.Enabled") && Toolkit.hasMatchingDisplayName(item, config.getString("TNT.Name"))) {

					if (!arena.isCombatActionPermittedInRegion(p)) {
						return;
					}

					Location handLocation = p.getLocation();
					handLocation.setY(handLocation.getY() + 1.0);
					Vector direction = handLocation.getDirection();

					Entity entity = p.getWorld().spawn(handLocation, TNTPrimed.class);
					entity.setVelocity(direction.multiply(1.5));
					entity.setCustomName(p.getName());

					e.setCancelled(true);

					useAbilityItem(p, p, item, "none");

				}

			} else if (item.getType() == XMaterial.ENDER_EYE.parseMaterial()) {

				if (isAbilityItem(p, "Warper", item)) {

					String[] nearestData = Toolkit.getNearestPlayer(p, config.getInt("PlayerTracker.TrackBelowY"));

					if (Bukkit.getServer().getOnlinePlayers().size() > 1 && nearestData != null) {

						e.setCancelled(true);

						String nearestPlayerUsername = nearestData[0];
						Location nearestPlayerLocation = Bukkit.getPlayer(nearestPlayerUsername).getLocation();

						if (arena.getKits().hasKit(nearestPlayerUsername)) {

							p.teleport(nearestPlayerLocation);

							p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 5));
							p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 5));

							useAbilityItem(p, p, item, "Warper");

						} else {
							p.sendMessage(resources.getMessages().getString("Messages.Other.Players"));
						}

					} else {
						p.sendMessage(resources.getMessages().getString("Messages.Other.Players"));
					}

				}

			} else if (item.getType() == XMaterial.NETHER_STAR.parseMaterial()) {

				if (isAbilityItem(p, "Ninja", item)) {

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

					useAbilityItem(p, p, item, "Ninja");

					new BukkitRunnable() {

						@Override
						public void run() {

							if (arena.getKits().hasKit(p.getName())) {

								p.getInventory().setHelmet(previousHelmet);
								p.getInventory().setChestplate(previousChestplate);
								p.getInventory().setLeggings(previousLeggings);
								p.getInventory().setBoots(previousBoots);

							}

						}

					}.runTaskLater(Game.getInstance(), 100L);

				}

			} else if (item.getType() == XMaterial.COAL.parseMaterial() && item.hasItemMeta()) {

				if (isAbilityItem(p, "Bomber", item)) {

					p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 2));

					new BukkitRunnable() {

						public int t = 5;

						@Override
						public void run() {

							if (t != 0 && p.getGameMode() != GameMode.SPECTATOR && arena.getKits().hasKit(p.getName())) {

								Entity entity = p.getWorld().spawn(p.getLocation(), TNTPrimed.class);
								entity.setCustomName(p.getName());

								String bomberSound = abilities.getString("Abilities.Bomber.Sound.Sound");
								p.playSound(p.getLocation(), XSound.matchXSound(bomberSound).get().parseSound(),
										1, abilities.getInt("Abilities.Bomber.Sound.Pitch"));

								t--;

							} else {

								cancel();

							}

						}

					}.runTaskTimer(Game.getInstance(), 0L, 20L);

					useAbilityItem(p, null, item, "Bomber");

				}

			} else if (item.getType() == XMaterial.SLIME_BALL.parseMaterial() && item.hasItemMeta()) {

				if (Toolkit.hasMatchingDisplayName(item, abilities.getString("Abilities.Archer.Item.NoFire"))) {

					item.setType(XMaterial.MAGMA_CREAM.parseMaterial());
					meta.setDisplayName(Toolkit.translate(abilities.getString("Abilities.Archer.Item.Fire")));
					item.setItemMeta(meta);

					Toolkit.setMainHandItem(p, item);

					if (abilities.getBoolean("Abilities.Archer.Message.Enabled")) {
						p.sendMessage(abilities.getString("Abilities.Archer.Message.Fire"));
					}

					XSound.play(p, "UI_BUTTON_CLICK, 1, 1");

				}

			} else if (item.getType() == XMaterial.MAGMA_CREAM.parseMaterial() && item.hasItemMeta()) {

				if (Toolkit.hasMatchingDisplayName(item, abilities.getString("Abilities.Archer.Item.Fire"))) {

					item.setType(XMaterial.SLIME_BALL.parseMaterial());
					meta.setDisplayName(Toolkit.translate(abilities.getString("Abilities.Archer.Item.NoFire")));
					item.setItemMeta(meta);

					Toolkit.setMainHandItem(p, item);

					if (abilities.getBoolean("Abilities.Archer.Message.Enabled")) {
						p.sendMessage(Toolkit.translate(abilities.getString("Abilities.Archer.Message.NoFire")));
					}

					XSound.play(p, "UI_BUTTON_CLICK, 1, 1");

				}

			}

			/* Kit Item and custom Arena Items */

			if (config.contains("Items.Kits") &&
					item.getType() == XMaterial.matchXMaterial(config.getString("Items.Kits.Material")).get().parseMaterial()) {

				if (Toolkit.hasMatchingDisplayName(item, config.getString("Items.Kits.Name"))) {

					Toolkit.runCommands(p, config.getStringList("Items.Kits.Commands"), "none", "none");

					if (config.getBoolean("Items.Kits.Menu")) {
						arena.getMenus().getKitMenu().open(p);
					}

					e.setCancelled(true);

				}

			} else if (item.hasItemMeta()) {

				ConfigurationSection items = config.getConfigurationSection("Items");

				for (String identifier : items.getKeys(false)) {

					String itemPath = "Items." + identifier;

					if (config.getBoolean(itemPath + ".Enabled")) {

						if (item.getType() == XMaterial.matchXMaterial(config.getString(itemPath + ".Material")).get().parseMaterial()) {

							if (Toolkit.hasMatchingDisplayName(item, config.getString(itemPath + ".Name"))) {

								Toolkit.runCommands(p, config.getStringList(itemPath + ".Commands"), "none", "none");
								e.setCancelled(true);

							}

						}

					}

				}

			}
			
		}
		
	}

	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) {

		Player p = e.getPlayer();

		if (Toolkit.inArena(p) && e.getRightClicked().getType() == EntityType.PLAYER) {

			ItemStack item = Toolkit.getMainHandItem(p);
			Player damagedPlayer = (Player) e.getRightClicked();

			if (config.getBoolean("Arena.NoKitProtection")) {

				if (!arena.getKits().hasKit(damagedPlayer.getName())) {
					return;
				}

			}

			if (item.getType() == XMaterial.BLAZE_ROD.parseMaterial()) {

				if (isAbilityItem(p, "Thunderbolt", item)) {

					p.getWorld().strikeLightningEffect(e.getRightClicked().getLocation());
					damagedPlayer.damage(4.0);
					damagedPlayer.setFireTicks(5 * 20);

					useAbilityItem(p, damagedPlayer, item, "Thunderbolt");

				}

			} else if (item.getType() == XMaterial.GHAST_TEAR.parseMaterial()) {

				if (isAbilityItem(p, "Vampire", item)) {

					damagedPlayer.damage(4.0);
					damagedPlayer.playSound(damagedPlayer.getLocation(), XSound.ENTITY_GENERIC_DRINK.parseSound(), 1, -1);

					if (p.getHealth() <= 16.0) {

						p.setHealth(p.getHealth() + 4.0);
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 2, 1));

					}

					useAbilityItem(p, damagedPlayer, item, "Vampire");

				}

			}

		}

	}

	private boolean isAbilityItem(Player p, String kitName, ItemStack interactedItem) {

		if (arena.getKits().hasKit(p.getName())) {

			if (Toolkit.hasMatchingDisplayName(interactedItem, abilities.getString("Abilities." + kitName + ".Item.Name"))) {

				String abilityPermission = "kp.ability." + kitName.toLowerCase();

				if (p.hasPermission(abilityPermission)) {

					if (arena.isCombatActionPermittedInRegion(p)) {
						return true;
					}

				} else {

					p.sendMessage(resources.getMessages().getString("Messages.General.Permission").replace("%permission%", abilityPermission));

				}

			}

		}

		return false;

	}

	private void useAbilityItem(Player p, Player clicked, ItemStack abilityItem, String kitName) {

		String abilityPrefix = "Abilities." + kitName;

		if (abilities.getBoolean(abilityPrefix + ".Message.Enabled")) {
			String abilityMessage = abilities.getString(abilityPrefix + ".Message.Message");
			if (clicked != null) abilityMessage = abilityMessage.replace("%player%", clicked.getName());
			p.sendMessage(abilityMessage);
		}

		abilityItem.setAmount(abilityItem.getAmount() - 1);
		Toolkit.setMainHandItem(p, abilityItem);

		if (abilities.getBoolean(abilityPrefix + ".Sound.Enabled")) {
			XSound.play(p, abilities.getString(abilityPrefix + ".Sound.Sound") + ", 1, " + abilities.getInt(abilityPrefix + ".Sound.Pitch"));
		}

	}
	
	@EventHandler
	public void onShoot(EntityShootBowEvent e) {
		
		if (e.getEntity() instanceof Player) {
			
			Player p = (Player) e.getEntity();
			
			if (Toolkit.inArena(p)) {
				
				if (p.hasPermission("kp.ability.archer")) {

					int ammoSlot = getItemByMeta(Material.MAGMA_CREAM, abilities.getString("Abilities.Archer.Item.Fire"), p);

					if (ammoSlot != -1) {

						ItemStack ammo = p.getInventory().getItem(ammoSlot);

						e.getProjectile().setFireTicks(1000);
						p.playSound(p.getLocation(), XSound.matchXSound(abilities.getString("Abilities.Archer.Sound.Sound")).get().parseSound(), 1, (int) abilities.getInt("Abilities.Archer.Sound.Pitch"));

						if (ammo.getAmount() == 1) {
							p.getInventory().setItem(ammoSlot, new ItemStack(Material.AIR));
						} else {
							ammo.setAmount(ammo.getAmount() - 1);
						}

					}
					
				}
				
			}
			
		}
		
	}

	private int getItemByMeta(Material type, String displayName, Player p) {

		for (int i = 0; i < 36; i++) {
			ItemStack item = p.getInventory().getItem(i);
			if (item != null) {
				if (item.getType() == type) {
					if (Toolkit.hasMatchingDisplayName(item, displayName)) {
						return i;
					}
				}
			}
		}
		return -1;

	}
	
	@EventHandler
	public void onPotion(ProjectileLaunchEvent e) {

		if (Toolkit.inArena(e.getEntity())) {
		
			if (e.getEntity().getShooter() instanceof Player) {

				Player shooter = (Player) e.getEntity().getShooter();
				ItemStack itemThrown = Toolkit.getMainHandItem(shooter);

				if (e.getEntity().getType() == EntityType.SPLASH_POTION) {

					if (itemThrown.hasItemMeta() && itemThrown.getItemMeta().hasLore() &&
							itemThrown.getItemMeta().getLore().get(0).equals("§8X")) {

						int slot = shooter.getInventory().getHeldItemSlot();
						Kit playerKit = arena.getKits().getKitOfPlayer(shooter.getName());

						if (playerKit != null) {

							ItemStack potionStack = createWitchPotion();

							new BukkitRunnable() {

								@Override
								public void run() {

									if (!arena.getKits().hasKit(shooter.getName())) {
										return;
									}

									shooter.getInventory().setItem(slot, potionStack);

									if (abilities.getBoolean("Abilities.Witch.Message.Enabled")) {
										shooter.sendMessage(Toolkit.translate(abilities.getString("Abilities.Witch.Message.Message")));
									}

									XSound.play(shooter, abilities.getString("Abilities.Witch.Sound.Sound") + ", 1, " + abilities.getInt("Abliities.Witch.Sound.Pitch"));

								}

							}.runTaskLater(Game.getInstance(), 5 * 20);

						}

					}

				} else if (e.getEntity().getType() == EntityType.EGG) {

					if (arena.getKits().hasKit(shooter.getName()) &&
							arena.getKits().getKitOfPlayer(shooter.getName()).getName().equals("Trickster")) {
						if (isAbilityItem(shooter, "Trickster", itemThrown)) {
							e.getEntity().setCustomName("pellet");
						}
					}

				}
				
			}
			
		}
		
	}

	private ItemStack createWitchPotion() {

		Potion potion = new Potion(pickPotion(), 1);
		potion.setSplash(true);

		ItemStack potionStack = potion.toItemStack(1);
		ItemMeta potionMeta = potionStack.getItemMeta();

		List<String> lore = new ArrayList<>();
		lore.add(Toolkit.translate("&8X"));
		potionMeta.setLore(lore);

		potionStack.setItemMeta(potionMeta);
		return potionStack;

	}
	
	@EventHandler
	public void onShot(EntityDamageByEntityEvent e) {

		if (e.getEntity() instanceof Player && e.getCause() == DamageCause.PROJECTILE) {
			
			if (e.getDamager() instanceof Snowball) {
				
				Player damagedPlayer = (Player) e.getEntity();
				Snowball snowball = (Snowball) e.getDamager();

				if (snowball.getCustomName() != null && snowball.getCustomName().equals("bullet")) {

					if (Toolkit.inArena(damagedPlayer) && arena.getKits().hasKit(damagedPlayer.getName())) {

						damagedPlayer.damage(4.5);

					}

				}
				
			} else if (e.getDamager() instanceof Egg) {

				Player damagedPlayer = (Player) e.getEntity();
				Egg egg = (Egg) e.getDamager();

				if (egg.getCustomName() != null && egg.getCustomName().equals("pellet")) {

					if (Toolkit.inArena(damagedPlayer) && arena.getKits().hasKit(damagedPlayer.getName())) {

						if (egg.getShooter() instanceof Player) {

							Player shooter = (Player) egg.getShooter();
							Location shooterLocation = shooter.getLocation();

							if (!arena.isCombatActionPermittedInRegion(damagedPlayer)) {
								shooter.sendMessage(resources.getMessages().getString("Messages.Error.PVP"));
								return;
							}

							shooter.teleport(damagedPlayer);
							damagedPlayer.teleport(shooterLocation);

							if (abilities.getBoolean("Abilities.Trickster.Message.Enabled")) {
								shooter.sendMessage(Toolkit.translate(abilities.getString("Abilities.Trickster.Message.Message").replace("%player%", damagedPlayer.getName())));
							}

							if (abilities.getBoolean("Abilities.Trickster.Sound.Enabled")) {
								XSound.play(shooter, abilities.getString("Abilities.Trickster.Sound.Sound") + ", 1, " + abilities.getInt("Abilities.Trickster.Sound.Pitch"));
								XSound.play(damagedPlayer, abilities.getString("Abilities.Trickster.Sound.Sound") + ", 1, " + abilities.getInt("Abilities.Trickster.Sound.Pitch"));
							}

						}

					}

				}

			}
			
		}
		
	}
	
	private PotionType pickPotion() {
		
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

}
