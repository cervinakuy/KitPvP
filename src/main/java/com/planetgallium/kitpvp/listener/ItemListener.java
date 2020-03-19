package com.planetgallium.kitpvp.listener;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
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
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
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

import net.md_5.bungee.api.ChatColor;

public class ItemListener implements Listener {
	
	private Arena arena;
	private Resources resources;
	
	public ItemListener(Arena arena, Resources resources) {
		this.arena = arena;
		this.resources = resources;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		
		Player p = e.getPlayer();
		
		if (Toolkit.inArena(p)) {
			
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				
				if (Toolkit.getMainHandItem(p).hasItemMeta() && Toolkit.getMainHandItem(p).getItemMeta().hasDisplayName() && Toolkit.getMainHandItem(p).getType() == XMaterial.matchXMaterial(Game.getInstance().getConfig().getString("Items.Kits.Item").toUpperCase()).get().parseMaterial()) {
					
					if (Toolkit.getMainHandItem(p).getItemMeta().getDisplayName().equals(Config.getS("Items.Kits.Name"))) {
						
						Toolkit.runCommands("Items.Kits", p);
						
						if (Game.getInstance().getConfig().getBoolean("Items.Kits.Menu")) {
							
							KitMenu menu = new KitMenu(resources);
							menu.create(p);
							
						}
						 
					}
					
				} else if (Toolkit.getMainHandItem(p).getType() == XMaterial.matchXMaterial(Config.getS("Items.Leave.Item")).get().parseMaterial()) {
					
					if (Toolkit.getMainHandItem(p).getItemMeta().getDisplayName().equals(Config.getS(("Items.Leave.Name")))) {
							
						Toolkit.runCommands("Items.Leave", p);
						
					}
					
				} else if (Toolkit.getMainHandItem(p).getType() == XMaterial.TNT.parseMaterial()) {

					ItemStack tnt = Toolkit.getMainHandItem(p);

					Location handLocation = p.getLocation();
					handLocation.setY(handLocation.getY() + 1.0);
					Vector direction = handLocation.getDirection();

					Entity entity = p.getWorld().spawn(handLocation, TNTPrimed.class);
					entity.setVelocity(direction.multiply(1.5));
					
					
					((TNTPrimed)entity).setFuseTicks(Config.getI("TNT.Fuse")); // Code by Tekcno (AKA Limabean2091)
					
					tnt.setAmount(tnt.getAmount() - 1);
					Toolkit.setMainHandItem(p, tnt);
					
		            
				} else if (Toolkit.getMainHandItem(p).getType() == XMaterial.SLIME_BALL.parseMaterial()) {
					
					int amount = p.getInventory().getItem(2).getAmount();
					
					ItemStack enabled = new ItemStack(Material.MAGMA_CREAM, amount);
					ItemMeta enabledmeta = enabled.getItemMeta();
					enabledmeta.setDisplayName(Config.tr(resources.getAbilities().getString("Abilities.Archer.Item.Fire")));
					enabled.setItemMeta(enabledmeta);
					Toolkit.setMainHandItem(p, enabled);
					
					if (resources.getAbilities().getBoolean("Abilities.Archer.Message.Enabled")) {
						p.sendMessage(Config.tr(resources.getAbilities().getString("Abilities.Archer.Message.Fire").replace("%prefix%", resources.getMessages().getString("Messages.General.Prefix"))));
					}
					
					p.playSound(p.getLocation(), XSound.UI_BUTTON_CLICK.parseSound(), 1, resources.getAbilities().getInt("Abilities.Archer.Sound.Pitch"));
					
				} else if (Toolkit.getMainHandItem(p).getType() == XMaterial.MAGMA_CREAM.parseMaterial()) {
					
					int amount = p.getInventory().getItem(2).getAmount();
					
					ItemStack enabled = new ItemStack(Material.SLIME_BALL, amount);
					ItemMeta enabledmeta = enabled.getItemMeta();
					enabledmeta.setDisplayName(Config.tr(resources.getAbilities().getString("Abilities.Archer.Item.NoFire")));
					enabled.setItemMeta(enabledmeta);
					Toolkit.setMainHandItem(p, enabled);
					
					if (resources.getAbilities().getBoolean("Abilities.Archer.Message.Enabled")) {
						p.sendMessage(Config.tr(resources.getAbilities().getString("Abilities.Archer.Message.NoFire").replace("%prefix%", resources.getMessages().getString("Messages.General.Prefix"))));
					}
					
					p.playSound(p.getLocation(), XSound.UI_BUTTON_CLICK.parseSound(), 1, 1);
					
				} else if (Toolkit.getMainHandItem(p).getType() == XMaterial.SADDLE.parseMaterial()) {
					
					if (p.hasPermission("kp.ability.kangaroo")) {
						
						ItemStack launcher = new ItemStack(e.getItem().getType(), e.getItem().getAmount());
						ItemMeta launchermeta = launcher.getItemMeta();
						launchermeta.setDisplayName(Config.tr(resources.getAbilities().getString("Abilities.Kangaroo.Item.Name")));
						launcher.setItemMeta(launchermeta);
						
						p.playSound(p.getLocation(), XSound.matchXSound(resources.getAbilities().getString("Abilities.Kangaroo.Sound.Sound")).get().parseSound(), 1, (int) resources.getAbilities().getInt("Abilities.Kangaroo.Sound.Pitch"));
						
						if (resources.getAbilities().getBoolean("Abilities.Kangaroo.Message.Enabled")) {
							p.sendMessage(Config.tr(resources.getAbilities().getString("Abilities.Kangaroo.Message.Message").replace("%prefix%", resources.getMessages().getString("Messages.General.Prefix"))));
						}
						
						p.setVelocity(new Vector(0, 2, 0));
						
						launcher.setAmount(launcher.getAmount() - 1);
						Toolkit.setMainHandItem(p, launcher);
						
						if (Game.getInstance().getConfig().getBoolean("Arena.PreventFallDamage")) { p.setFallDistance(-1000000); } else { p.setFallDistance(-30); }
						
					}
					
				} else if (Toolkit.getMainHandItem(p).getType() == XMaterial.IRON_HOE.parseMaterial()) {
					
					if (p.hasPermission("kp.ability.soldier")) {
						
						ItemStack gun = new ItemStack(e.getItem().getType(), e.getItem().getAmount());
						ItemMeta gunmeta = gun.getItemMeta();
						gunmeta.setDisplayName(resources.getAbilities().getString("Abilities.Soldier.Item.Name").replaceAll("&", "§"));
						gun.setItemMeta(gunmeta);
						
						Snowball ammo = (Snowball) p.launchProjectile(Snowball.class);

						p.playSound(p.getLocation(), XSound.ENTITY_GENERIC_EXPLODE.parseSound(), 1, 2);
						
						gun.setAmount(gun.getAmount() - 1);
						Toolkit.setMainHandItem(p, gun);
						
					}
					
				} else if (Toolkit.getMainHandItem(p).getType() == XMaterial.DIAMOND_HOE.parseMaterial()) {
					
					if (p.hasPermission("kp.ability.sniper")) {
						
						ItemStack gun = new ItemStack(e.getItem().getType(), e.getItem().getAmount());
						ItemMeta gunmeta = gun.getItemMeta();
						gunmeta.setDisplayName(resources.getAbilities().getString("Abilities.Soldier.Item.Name").replaceAll("&", "§"));
						gun.setItemMeta(gunmeta);
						
						Egg ammo = (Egg) p.launchProjectile(Egg.class);
						ammo.setVelocity(p.getLocation().getDirection().multiply(10));

						p.playSound(p.getLocation(), XSound.ENTITY_GENERIC_EXPLODE.parseSound(), 1, 2);
						
						gun.setAmount(gun.getAmount() - 1);
						Toolkit.setMainHandItem(p, gun);
						
					}
					
				} else if (Toolkit.getMainHandItem(p).getType() == XMaterial.DIAMOND_HORSE_ARMOR.parseMaterial()) {
					
					if (p.hasPermission("kp.ability.shotgun")) {
						
						ItemStack gun = new ItemStack(e.getItem().getType(), e.getItem().getAmount());
						ItemMeta gunmeta = gun.getItemMeta();
						gunmeta.setDisplayName(resources.getAbilities().getString("Abilities.Soldier.Item.Name").replaceAll("&", "§"));
						gun.setItemMeta(gunmeta);
						
						int level = Game.getInstance().getArena().getLevels().getLevel(p.getUniqueId());
						
						for (int i = 0; i < 5+level; i++) { // TODO Add a bullet for each added level.
							Snowball ammo = (Snowball) p.launchProjectile(Snowball.class);
							Vector direction = p.getLocation().getDirection();
							
							Vector spread = new Vector();
                            spread.setX(0.0D + Math.random() - Math.random());
                            spread.setY(Math.random());
                            spread.setZ(0.0D + Math.random() - Math.random());
                            spread = spread.multiply(0.1);
                            
                            direction = direction.add(spread);
							direction = direction.multiply(2.5);
							
							ammo.setVelocity(direction);
						}
						

						p.playSound(p.getLocation(), XSound.ENTITY_GENERIC_EXPLODE.parseSound(), 1, 2);
						
						gun.setAmount(gun.getAmount() - 1);
						Toolkit.setMainHandItem(p, gun);
						
					}
					
				} else if (Toolkit.getMainHandItem(p).getType() == XMaterial.GLASS_BOTTLE.parseMaterial()) {
					
					if (p.hasPermission("kp.ability.witch") && arena.getKits().getKit(p.getName()).equals("Witch")) {
						
						Potion potion = new Potion(pickPotion(), 1);
						potion.setSplash(true);
						
						Toolkit.setMainHandItem(p, potion.toItemStack(1));
						
					}
					
				} else if (Toolkit.getMainHandItem(p).getType() == XMaterial.ENDER_EYE.parseMaterial()) {
					
					if (p.hasPermission("kp.ability.warper")) {
						
						if (Bukkit.getServer().getOnlinePlayers().size() > 1) {
							
							e.setCancelled(true);
							
							ItemStack tper = new ItemStack(e.getItem().getType(), e.getItem().getAmount());
							ItemMeta tpermeta = tper.getItemMeta();
							tpermeta.setDisplayName(Config.tr(resources.getAbilities().getString("Abilities.Warper.Item.Name")));
							tper.setItemMeta(tpermeta);
							
							String username = Toolkit.getNearestPlayer(p)[0];
							Location loc = Bukkit.getPlayer(username).getLocation();
							
							if (Game.getInstance().getArena().getKits().hasKit(username)) {
								
								p.teleport(loc);
								
								p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 5));
								p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 5));
								
								p.playSound(p.getLocation(), XSound.matchXSound(resources.getAbilities().getString("Abilities.Warper.Sound.Sound")).get().parseSound(), 1, (int) resources.getAbilities().getInt("Abilities.Warper.Sound.Pitch"));
								
								if (resources.getAbilities().getBoolean("Abilities.Warper.Message.Enabled")) {
									p.sendMessage(Config.tr(resources.getAbilities().getString("Abilities.Warper.Message.Message").replace("%prefix%", resources.getMessages().getString("Messages.General.Prefix"))));
								}
								
								tper.setAmount(tper.getAmount() - 1);
								Toolkit.setMainHandItem(p, tper);
								
							} else {
								
								p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Other.Players")));
								
							}
							
						} else {
							
							p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Other.Players")));
							
						}
						
					}	
					
				} else if (Toolkit.getMainHandItem(p).getType() == XMaterial.NETHER_STAR.parseMaterial()) {
					
					if (p.hasPermission("kp.ability.ninja")) {
						
						ItemStack previousHelmet = p.getInventory().getHelmet();
						ItemStack previousChestplate = p.getInventory().getChestplate();
						ItemStack previousLeggings = p.getInventory().getLeggings();
						ItemStack previousBoots = p.getInventory().getBoots();
						
						p.getInventory().setArmorContents(null);
						
						ItemStack vanish = new ItemStack(e.getItem().getType(), e.getItem().getAmount());
						ItemMeta vanishMeta = vanish.getItemMeta();
						
						vanishMeta.setDisplayName(Config.tr(resources.getAbilities().getString("Abilities.Ninja.Item.Name")));
						vanish.setItemMeta(vanishMeta);
						
						for (Entity entity : p.getNearbyEntities(3, 3, 3)) {
							
							if (entity instanceof Player) {
								
								Player nearby = (Player) entity;
								
								nearby.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
								nearby.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
								
							}
							
						}
						
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
						p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0));
						
						p.playSound(p.getLocation(), XSound.matchXSound(resources.getAbilities().getString("Abilities.Ninja.Sound.Sound")).get().parseSound(), 1, resources.getAbilities().getInt("Abilities.Ninja.Sound.Pitch"));
						
						if (resources.getAbilities().getBoolean("Abilities.Ninja.Messaage.Enabled")) {
							p.sendMessage(Config.tr(resources.getAbilities().getString("Abilities.Ninja.Message.Message").replace("%prefix%", resources.getMessages().getString("Messages.General.Prefix"))));
						}
						
						vanish.setAmount(vanish.getAmount() - 1);
						Toolkit.setMainHandItem(p, vanish);
						
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
					
				} else if (Toolkit.getMainHandItem(p).getType() == XMaterial.BLAZE_ROD.parseMaterial()) {
					
					if (arena.getKits().getKit(p.getName()).equals("Thunderbolt") && p.hasPermission("kp.ability.thunderbolt")) {
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Player")));
						
					}
					
				} else if (Toolkit.getMainHandItem(p).getType() == XMaterial.GHAST_TEAR.parseMaterial()) {
					
					if (arena.getKits().getKit(p.getName()).equals("Vampire") && p.hasPermission("kp.ability.vampire")) {
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Player")));
						
					}
					
				}
				
			} 
			
		}
		
	}
	
	@EventHandler
	public void onShoot(EntityShootBowEvent e) {
		
		if (e.getEntity() instanceof Player) {
			
			Player p = (Player) e.getEntity();
			
			if (Toolkit.inArena(p)) {
				
				if (p.hasPermission("kp.ability.archer")) {
					
					if (p.getInventory().getItem(2) != null) {
					
						int amount = p.getInventory().getItem(2).getAmount();
					
						ItemStack ammo = new ItemStack(Material.MAGMA_CREAM, amount);
						ItemMeta ammometa = ammo.getItemMeta();
						ammometa.setDisplayName(Config.tr(resources.getAbilities().getString("Abilities.Archer.Item.Fire")));
						ammo.setItemMeta(ammometa);
						
						if (p.getInventory().contains(ammo)) {
						
							e.getProjectile().setFireTicks(1000);
									
							p.playSound(p.getLocation(), XSound.matchXSound(resources.getAbilities().getString("Abilities.Archer.Sound.Sound")).get().parseSound(), 1, (int) resources.getAbilities().getInt("Abilities.Archer.Sound.Pitch"));
										
							ItemStack magma = new ItemStack(Material.MAGMA_CREAM);
							ItemMeta magmameta = ammo.getItemMeta();
							magmameta.setDisplayName(Config.tr(resources.getAbilities().getString("Abilities.Archer.Item.Fire")));
							magma.setItemMeta(magmameta);
							p.getInventory().removeItem(magma);
							
						}
							
					}
					
				}
				
			}
			
		}
		
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
							
							if (Game.getInstance().getArena().getKits().getKit(p.getName()) != null) {
								
								if (Game.getInstance().getArena().getKits().getKit(p.getName()).equals("Witch")) {
									
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
				
				if (Toolkit.inArena(damagedPlayer) && Game.getInstance().getArena().getKits().hasKit(damagedPlayer.getName())) {
					
					damagedPlayer.damage(4.5);
				}
				
			}
			
			if (e.getDamager() instanceof Egg) {
				
				Player damagedPlayer = (Player) e.getEntity();
				
				if (Toolkit.inArena(damagedPlayer) && Game.getInstance().getArena().getKits().hasKit(damagedPlayer.getName())) {
					
					damagedPlayer.damage(19);
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
