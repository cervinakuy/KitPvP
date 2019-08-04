package com.planetgallium.kitpvp.listener;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.addon.KitMenu;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Sounds;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.XMaterial;

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
				
				if (Toolkit.getMainHandItem(p).hasItemMeta() && Toolkit.getMainHandItem(p).getItemMeta().hasDisplayName() && Toolkit.getMainHandItem(p).getType() == Material.valueOf(Game.getInstance().getConfig().getString("Items.Kits.Item").toUpperCase())) {
					
					if (Toolkit.getMainHandItem(p).getItemMeta().getDisplayName().equals(Config.getS("Items.Kits.Name"))) {
						
						if (Game.getInstance().getConfig().getBoolean("Items.Kits.Menu")) {
							
							KitMenu menu = new KitMenu(resources);
							menu.create(p);
							
						}
						
						if (Config.getB("Items.Kits.Commands.Enabled")) { 
							
							for (String list : Config.getC().getStringList("Items.Kits.Commands.Commands")) {
								
								String[] command = list.split(":");
								
								if (command[0].equals("console")) {
									
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command[1].replace("%player%", p.getName()));
									
								} else if (command[0].equals("player")) {
									
									p.performCommand(command[1].replace("%player%", p.getName()));
									
								} else {
									
									Bukkit.getConsoleSender().sendMessage(Config.tr("&7[&b&lKITPVP&7] &cCommand syntax incorrect, you must specify a prefix before a command."));
									Bukkit.getConsoleSender().sendMessage(Config.tr("&7[&b&lKITPVP&7] &cIn the config, you must put either &7console: &cor &7player:&c."));
									Bukkit.getConsoleSender().sendMessage(Config.tr("&7[&b&lKITPVP&7] &cOnce the error is fixed, reload or restart your server."));
									
								}
								
							}
							
						}
						 
					}
					
				} else if (Toolkit.getMainHandItem(p).getType() == Material.valueOf(Config.getS("Items.Leave.Item").toUpperCase())) {
					
					if (Toolkit.getMainHandItem(p).getItemMeta().getDisplayName().equals(Config.getS(("Items.Leave.Name")))) {
						
						if (Config.getB("Items.Leave.Commands.Enabled")) {
							
							for (String list : Config.getC().getStringList("Items.Leave.Commands.Commands")) {
								
								String[] command = list.split(":");
								
								if (command[0].equals("console")) {
									
									Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command[1].replace("%player%", p.getName()));
									
								} else if (command[0].equals("player")) {
									
									p.performCommand(command[1].replace("%player%", p.getName()));
									
								} else {
									
									Bukkit.getConsoleSender().sendMessage(Config.tr("&7[&b&lKITPVP&7] &cCommand syntax incorrect, you must specify a prefix before a command."));
									Bukkit.getConsoleSender().sendMessage(Config.tr("&7[&b&lKITPVP&7] &cIn the config, you must put either &7console: &cor &7player:&c."));
									Bukkit.getConsoleSender().sendMessage(Config.tr("&7[&b&lKITPVP&7] &cOnce the error is fixed, reload or restart your server."));
									
								}
								
							}
							
						}
						
					}	
					
				} else if (Toolkit.getMainHandItem(p).getType() == Material.TNT) {
					
					ItemStack tnt = new ItemStack(Toolkit.getMainHandItem(p).getType(), Toolkit.getMainHandItem(p).getAmount());
					
					Location handLocation = p.getLocation();
					handLocation.setY(handLocation.getY() + 1.0);
			        Vector direction = handLocation.getDirection();
			        
			        Entity entity = p.getWorld().spawn(handLocation, TNTPrimed.class);
			        entity.setVelocity(direction.multiply(1.5));
		            
			        tnt.setAmount(tnt.getAmount() - 1);
			        Toolkit.setMainHandItem(p, tnt);
		            
				} else if (Toolkit.getMainHandItem(p).getType() == Material.SLIME_BALL) {
					
					int amount = p.getInventory().getItem(2).getAmount();
					
					ItemStack enabled = new ItemStack(Material.MAGMA_CREAM, amount);
					ItemMeta enabledmeta = enabled.getItemMeta();
					enabledmeta.setDisplayName(Config.tr(resources.getAbilities().getString("Abilities.Archer.Item.Fire")));
					enabled.setItemMeta(enabledmeta);
					Toolkit.setMainHandItem(p, enabled);
					
					if (resources.getAbilities().getBoolean("Abilities.Archer.Message.Enabled")) {
						p.sendMessage(Config.tr(resources.getAbilities().getString("Abilities.Archer.Message.Fire").replace("%prefix%", resources.getMessages().getString("Messages.General.Prefix"))));
					}
					
					p.playSound(p.getLocation(), Sounds.WOOD_CLICK.bukkitSound(), 1, resources.getAbilities().getInt("Abilities.Archer.Sound.Pitch"));
					
				} else if (Toolkit.getMainHandItem(p).getType() == Material.MAGMA_CREAM) {
					
					int amount = p.getInventory().getItem(2).getAmount();
					
					ItemStack enabled = new ItemStack(Material.SLIME_BALL, amount);
					ItemMeta enabledmeta = enabled.getItemMeta();
					enabledmeta.setDisplayName(Config.tr(resources.getAbilities().getString("Abilities.Archer.Item.NoFire")));
					enabled.setItemMeta(enabledmeta);
					Toolkit.setMainHandItem(p, enabled);
					
					if (resources.getAbilities().getBoolean("Abilities.Archer.Message.Enabled")) {
						p.sendMessage(Config.tr(resources.getAbilities().getString("Abilities.Archer.Message.NoFire").replace("%prefix%", resources.getMessages().getString("Messages.General.Prefix"))));
					}
					
					p.playSound(p.getLocation(), Sounds.WOOD_CLICK.bukkitSound(), 1, 1);
					
				} else if (Toolkit.getMainHandItem(p).getType() == Material.SADDLE) {
					
					if (p.hasPermission("kp.ability.kangaroo")) {
						
						ItemStack launcher = new ItemStack(e.getItem().getType(), e.getItem().getAmount());
						ItemMeta launchermeta = launcher.getItemMeta();
						launchermeta.setDisplayName(Config.tr(resources.getAbilities().getString("Abilities.Kangaroo.Item.Name")));
						launcher.setItemMeta(launchermeta);
						
						p.playSound(p.getLocation(), Sounds.valueOf(resources.getAbilities().getString("Abilities.Kangaroo.Sound.Sound")).bukkitSound(), 1, (int) resources.getAbilities().getInt("Abilities.Kangaroo.Sound.Pitch"));
						
						if (resources.getAbilities().getBoolean("Abilities.Kangaroo.Message.Enabled")) {
							p.sendMessage(Config.tr(resources.getAbilities().getString("Abilities.Kangaroo.Message.Message").replace("%prefix%", resources.getMessages().getString("Messages.General.Prefix"))));
						}
						
						p.setVelocity(new Vector(0, 2, 0));
						
						launcher.setAmount(launcher.getAmount() - 1);
						Toolkit.setMainHandItem(p, launcher);
						
						if (Game.getInstance().getConfig().getBoolean("Arena.PreventFallDamage")) { p.setFallDistance(-1000000); } else { p.setFallDistance(-30); }
						
					}
					
				} else if (Toolkit.getMainHandItem(p).getType() == Material.IRON_HOE) {
					
					if (p.hasPermission("kp.ability.soldier")) {
						
						ItemStack gun = new ItemStack(e.getItem().getType(), e.getItem().getAmount());
						ItemMeta gunmeta = gun.getItemMeta();
						gunmeta.setDisplayName(resources.getAbilities().getString("Abilities.Soldier.Item.Name").replaceAll("&", "§"));
						gun.setItemMeta(gunmeta);
						
						Snowball ammo = (Snowball) p.launchProjectile(Snowball.class);
						ammo.setVelocity(p.getLocation().getDirection().multiply(2.5));

						p.playSound(p.getLocation(), Sounds.EXPLODE.bukkitSound(), 1, 2);
						
						gun.setAmount(gun.getAmount() - 1);
						Toolkit.setMainHandItem(p, gun);
						
					}
					
				} else if (Toolkit.getMainHandItem(p).getType() == Material.GLASS_BOTTLE) {
					
					if (p.hasPermission("kp.ability.witch")) {
						
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
								
								p.playSound(p.getLocation(), Sounds.valueOf(resources.getAbilities().getString("Abilities.Warper.Sound.Sound")).bukkitSound(), 1, (int) resources.getAbilities().getInt("Abilities.Warper.Sound.Pitch"));
								
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
					
				} else if (Toolkit.getMainHandItem(p).getType() == Material.NETHER_STAR) {
					
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
						
						p.playSound(p.getLocation(), Sounds.valueOf(resources.getAbilities().getString("Abilities.Ninja.Sound.Sound")).bukkitSound(), 1, resources.getAbilities().getInt("Abilities.Ninja.Sound.Pitch"));
						
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
									
							p.playSound(p.getLocation(), Sounds.valueOf(resources.getAbilities().getString("Abilities.Archer.Sound.Sound")).bukkitSound(), 1, (int) resources.getAbilities().getInt("Abilities.Archer.Sound.Pitch"));
										
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
	public void onItemHeld(PlayerItemHeldEvent e) {
		
		Player p = e.getPlayer();
		
		if (Toolkit.inArena(p)) {
			
			int i = e.getNewSlot();
			ItemStack item = p.getInventory().getItem(i);
			
			if (item != null) {
				
				if (item.getType() == Material.COMPASS) {
					
					new BukkitRunnable() {
						
						@Override
						public void run() {
							
	            			if (p.getWorld().getPlayers().size() > 1) {
		                		
	            				Double distance = Double.parseDouble(Toolkit.getNearestPlayer(p)[1]);
	            				distance = Math.round(distance * 10.0) / 10.0;
		            			
	            				ItemMeta meta = item.getItemMeta();
		            			meta.setDisplayName(Config.getS("PlayerTracker.Message").replace("%nearestplayer%", Toolkit.getNearestPlayer(p)[0]).replace("%distance%", String.valueOf(distance)));
		            			item.setItemMeta(meta);
		            			p.setCompassTarget(Bukkit.getServer().getPlayer(Toolkit.getNearestPlayer(p)[0]).getLocation());
		            			
		                	} else {
		                		
		                		ItemMeta meta = item.getItemMeta();
		                		meta.setDisplayName(Game.getInstance().getConfig().getString("PlayerTracker.NoneOnline").replaceAll("&", "§"));
		                		item.setItemMeta(meta);
		                		cancel();
		                		
		                	}
							
						}
						
					}.runTaskTimer(Game.getInstance(), 0L, 20L);		
					
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
								
								p.playSound(p.getLocation(), Sounds.valueOf(resources.getAbilities().getString("Abilities.Witch.Sound.Sound")).bukkitSound(), 1, resources.getAbilities().getInt("Abliities.Witch.Sound.Pitch"));
								
							}
							
						}
						
					}
					
				}.runTaskLater(Game.getInstance(), 5 * 20);
				
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
