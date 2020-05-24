package com.planetgallium.kitpvp.listener;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
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
import com.planetgallium.kitpvp.menu.KitMenu;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.XMaterial;
import com.planetgallium.kitpvp.util.XSound;

public class ItemListener implements Listener {
	
	private Arena arena;
	private Resources resources;
	private FileConfiguration config;
	private FileConfiguration abilConfig;
	
	public ItemListener(Game plugin, Arena arena, Resources resources) {
		this.arena = arena;
		this.resources = resources;
		this.config = plugin.getConfig();
		this.abilConfig = resources.getAbilities();
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {

		Player p = e.getPlayer();
		
		if (Toolkit.inArena(p) && e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

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

					Potion potion = new Potion(pickPotion(), 1);
					potion.setSplash(true);

					Toolkit.setMainHandItem(p, potion.toItemStack(1));

				}

			} else if (item.getType() == XMaterial.TNT.parseMaterial()) {

				if (Config.getB("TNT.Enabled") && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(Config.getS("TNT.Name"))) {

					Location handLocation = p.getLocation();
					handLocation.setY(handLocation.getY() + 1.0);
					Vector direction = handLocation.getDirection();

					Entity entity = p.getWorld().spawn(handLocation, TNTPrimed.class);
					entity.setVelocity(direction.multiply(1.5));
					entity.setCustomName(p.getName());

					useAbilityItem(p, p, item, "none");

				}

			} else if (item.getType() == XMaterial.ENDER_EYE.parseMaterial()) {

				if (isAbilityItem(p, "Warper", item)) {

					String[] nearestData = Toolkit.getNearestPlayer(p, Config.getI("PlayerTracker.TrackBelowY"));

					if (Bukkit.getServer().getOnlinePlayers().size() > 1 && nearestData != null) {

						e.setCancelled(true);

						String username = nearestData[0];
						Location loc = Bukkit.getPlayer(username).getLocation();

						if (arena.getKits().hasKit(username)) {

							p.teleport(loc);

							p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 5));
							p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 5));

							useAbilityItem(p, p, item, "Warper");

						} else {
							p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Other.Players")));
						}

					} else {
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Other.Players")));
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

							p.getInventory().setHelmet(previousHelmet);
							p.getInventory().setChestplate(previousChestplate);
							p.getInventory().setLeggings(previousLeggings);
							p.getInventory().setBoots(previousBoots);

						}

					}.runTaskLater(Game.getInstance(), 100L);

				}

			} else if (item.getType() == XMaterial.SLIME_BALL.parseMaterial()) {

				item.setType(XMaterial.MAGMA_CREAM.parseMaterial());
				meta.setDisplayName(Config.tr(resources.getAbilities().getString("Abilities.Archer.Item.Fire")));
				item.setItemMeta(meta);

				Toolkit.setMainHandItem(p, item);

				if (resources.getAbilities().getBoolean("Abilities.Archer.Message.Enabled")) {
					p.sendMessage(Config.tr(resources.getAbilities().getString("Abilities.Archer.Message.Fire")));
				}

				XSound.playSoundFromString(p, "UI_BUTTON_CLICK, 1, 1");

			} else if (item.getType() == XMaterial.MAGMA_CREAM.parseMaterial()) {

				item.setType(XMaterial.SLIME_BALL.parseMaterial());
				meta.setDisplayName(Config.tr(resources.getAbilities().getString("Abilities.Archer.Item.NoFire")));
				item.setItemMeta(meta);

				Toolkit.setMainHandItem(p, item);

				if (resources.getAbilities().getBoolean("Abilities.Archer.Message.Enabled")) {
					p.sendMessage(Config.tr(resources.getAbilities().getString("Abilities.Archer.Message.NoFire")));
				}

				XSound.playSoundFromString(p, "UI_BUTTON_CLICK, 1, 1");

			} else if (item.getType() == XMaterial.matchXMaterial(config.getString("Items.Kits.Item")).get().parseMaterial()) {

				if (meta.getDisplayName().equals(Config.getS("Items.Kits.Name"))) {

					Toolkit.runCommands("Items.Kits", p);

					if (Game.getInstance().getConfig().getBoolean("Items.Kits.Menu")) {

						KitMenu menu = new KitMenu(resources);
						menu.create(p);

					}

				}

			} else if (item.hasItemMeta()) {

				ConfigurationSection items = config.getConfigurationSection("Items");

				for (String identifier : items.getKeys(false)) {

					String itemPath = "Items." + identifier;

					if (config.getBoolean(itemPath + ".Enabled")) {

						if (item.getType() == XMaterial.matchXMaterial(config.getString(itemPath + ".Item")).get().parseMaterial()) {

							if (item.getItemMeta().getDisplayName().equals(Config.tr(config.getString(itemPath + ".Name")))) {

								Toolkit.runCommands(itemPath, p);

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

			if (Config.getB("Arena.NoKitProtection")) {

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

			if (interactedItem.hasItemMeta()) {

				ItemMeta meta = interactedItem.getItemMeta();

				if (Config.tr(meta.getDisplayName()).equals(abilConfig.getString("Abilities." + kitName + ".Item.Name"))) {

					if (p.hasPermission("kp.ability." + kitName.toLowerCase())) {

						return true;

					} else {

						p.sendMessage(resources.getMessages().getString("Messages.General.Permission"));

					}

				}

			}

		}

		return false;

	}

	private void useAbilityItem(Player p, Player clicked, ItemStack abilityItem, String kitName) {

		String abilityPrefix = "Abilities." + kitName;

		if (abilConfig.getBoolean(abilityPrefix + ".Message.Enabled")) {
			p.sendMessage(Config.tr(abilConfig.getString(abilityPrefix + ".Message.Message").replace("%player%", clicked.getName())));
		}

		abilityItem.setAmount(abilityItem.getAmount() - 1);
		Toolkit.setMainHandItem(p, abilityItem);

		if (abilConfig.getBoolean(abilityPrefix + ".Sound.Enabled")) {
			XSound.playSoundFromString(p, abilConfig.getString(abilityPrefix + ".Sound.Sound") + ", 1, " + abilConfig.getInt(abilityPrefix + ".Sound.Pitch"));
		}

	}
	
	@EventHandler
	public void onShoot(EntityShootBowEvent e) {
		
		if (e.getEntity() instanceof Player) {
			
			Player p = (Player) e.getEntity();
			
			if (Toolkit.inArena(p)) {
				
				if (p.hasPermission("kp.ability.archer")) {

					int ammoSlot = getItemByMeta(Material.MAGMA_CREAM, resources.getAbilities().getString("Abilities.Archer.Item.Fire"), p);

					if (ammoSlot != -1) {

						ItemStack ammo = p.getInventory().getItem(ammoSlot);

						e.getProjectile().setFireTicks(1000);
						p.playSound(p.getLocation(), XSound.matchXSound(resources.getAbilities().getString("Abilities.Archer.Sound.Sound")).get().parseSound(), 1, (int) resources.getAbilities().getInt("Abilities.Archer.Sound.Pitch"));

						ammo.setAmount(ammo.getAmount() - 1);
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
					if (Config.tr(item.getItemMeta().getDisplayName()).equals(displayName)) {
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
		
			if (e.getEntity().getShooter() instanceof Player && e.getEntity().getType() == EntityType.SPLASH_POTION) {
				
				Player p = (Player) e.getEntity().getShooter();
				int slot = p.getInventory().getHeldItemSlot();
				
				if (arena.getKits().getKit(p.getName()).equals("Witch")) {
					
					Potion potion = new Potion(pickPotion(), 1);
					potion.setSplash(true);
					
					new BukkitRunnable() {
						
						@Override
						public void run() {
							
							if (arena.getKits().getKit(p.getName()) != null) {
								
								if (arena.getKits().getKit(p.getName()).equals("Witch")) {
									
									p.getInventory().setItem(slot, potion.toItemStack(1));
									
									if (resources.getAbilities().getBoolean("Abilities.Witch.Message.Enabled")) {
										p.sendMessage(Config.tr(resources.getAbilities().getString("Abilities.Witch.Message.Message").replace("%prefix%", resources.getMessages().getString("Messages.General.Prefix"))));
									}
									
									p.playSound(p.getLocation(), XSound.matchXSound(resources.getAbilities().getString("Abilities.Witch.Sound.Sound")).get().parseSound(), 1, resources.getAbilities().getInt("Abliities.Witch.Sound.Pitch"));
									
								}
								
							}
							
						}
						
					}.runTaskLater(Game.getInstance(), 5 * 20);
					
				}
				
			}
			
		}
		
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
