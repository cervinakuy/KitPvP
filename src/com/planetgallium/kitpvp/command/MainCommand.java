package com.planetgallium.kitpvp.command;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.menu.KitMenu;
import com.planetgallium.kitpvp.menu.PreviewMenu;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.XSound;

import me.clip.placeholderapi.PlaceholderAPI;

public class MainCommand implements CommandExecutor {

	private List<String> spawnUsers = new ArrayList<String>();
	
	private boolean isFirst = false;
	
	private Game game;
	private Arena arena;
	private Resources resources;
	
	public MainCommand(Game game, Arena arena, Resources resources) {
		this.game = game;
		this.arena = arena;
		this.resources = resources;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
			
		if (sender instanceof Player) {
			
			Player p = (Player) sender;
			
			if (args.length == 0) {
			
				p.sendMessage(Config.tr("&7[&b&lKIT-PVP&7]"));
				p.sendMessage(Config.tr("&7Version: &b" + Game.getInstance().getDescription().getVersion()));
				p.sendMessage(Config.tr("&7Developer: &bCervinakuy"));
				p.sendMessage(Config.tr("&7Commands: &b/kp help"));
				p.sendMessage(Config.tr("&7Download: &bbit.ly/KP-Download"));
				return true;
				
			} else if (args.length == 1) {
				
				if (args[0].equalsIgnoreCase("help")) {
					
					p.sendMessage(Config.tr("&3&m           &r &b&lKIT-PVP &3Created by Cervinakuy &3&m             "));
					p.sendMessage(Config.tr(" "));
					p.sendMessage(Config.tr("&7- &b/kp &7Displays information about KitPvP."));
					p.sendMessage(Config.tr("&7- &b/kp help &7Displays the help message."));
					p.sendMessage(Config.tr("&7- &b/kp reload &7Reloads the configuration."));
					p.sendMessage(Config.tr("&7- &b/kp debug &7Prints debug information."));
					p.sendMessage(Config.tr("&7- &b/kp addspawn &7Adds an arena."));
					p.sendMessage(Config.tr("&7- &b/kp delspawn &7Removes an arena."));
					p.sendMessage(Config.tr("&7- &b/kp spawn &7Teleports you to the Spawn in your current arena."));
					p.sendMessage(Config.tr("&7- &b/kp spawn [arena] &7Teleport to a different KitPvP arena."));
					p.sendMessage(Config.tr("&7- &b/kp create [kitname] &7Creates a kit from your inventory."));
					p.sendMessage(Config.tr("&7- &b/kp delete [kitname] &7Deletes an existing kit."));
					p.sendMessage(Config.tr("&7- &b/kp preview [kitname] &7Preview the contents of a kit."));
					p.sendMessage(Config.tr("&7- &b/kp kit [kitname] &7Select a kit."));
					p.sendMessage(Config.tr("&7- &b/kp kits &7Lists all available kits."));
					p.sendMessage(Config.tr("&7- &b/kp clear &7Clears your current kit."));
					p.sendMessage(Config.tr("&7- &b/kp stats &7View your stats."));
					p.sendMessage(Config.tr("&7- &b/kp menu &7Displays the kits menu."));
					p.sendMessage(Config.tr("&7- &b/kp import &7Imports all stats from the MySQL database."));
					p.sendMessage(Config.tr("&7- &b/kp export &7Exports all stats to the MySQL database."));
					p.sendMessage(Config.tr(" "));
					p.sendMessage(Config.tr("&3&m                                                                               "));
					return true;
					
				} else if (args[0].equalsIgnoreCase("reload")) {
					
					if (p.hasPermission("kp.command.reload")) {
						
						game.reloadConfig();
						resources.reload();
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Commands.Reload")));
						
					} else {
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Permission")));
						
					}
					
				} else if (args[0].equalsIgnoreCase("debug")) {
					
					if (p.hasPermission("kp.command.debug")) {
						
						String names = "";
						for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
							names += plugin.getName() + " ";
						}
						
						p.sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aServer Version: &7" + Bukkit.getBukkitVersion()) + " " + (Bukkit.getVersion().contains("Spigot") ? "(Spigot)" : "(Other)"));
						p.sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aPlugin Version: &7" + Game.getInstance().getDescription().getVersion()) + " " + (Game.getInstance().needsUpdate() ? "(Requires Update)" : "(Latest Version)"));
						p.sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aSpawn Set: &7" + (Config.getC().contains("Arenas.Spawn") ? "Configured" : "Unconfigured")));
						p.sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aSupport Discord: &7https://discord.gg/GtXQKZ6"));
						p.sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aPlugin List: &7" + names));
						
					} else {
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Permission")));
						
					}
					
				} else if (args[0].equalsIgnoreCase("addspawn")) {
					
					if (p.hasPermission("kp.command.addspawn")) {
						
						Config.getC().set("Arenas.Spawn." + p.getLocation().getWorld().getName() + ".World", p.getLocation().getWorld().getName());
						Config.getC().set("Arenas.Spawn." + p.getLocation().getWorld().getName() + ".X", p.getLocation().getBlockX());
						Config.getC().set("Arenas.Spawn." + p.getLocation().getWorld().getName() + ".Y", p.getLocation().getBlockY());
						Config.getC().set("Arenas.Spawn." + p.getLocation().getWorld().getName() + ".Z", p.getLocation().getBlockZ());
						Config.getC().set("Arenas.Spawn." + p.getLocation().getWorld().getName() + ".Yaw", Float.valueOf(p.getLocation().getYaw()));
						Config.getC().set("Arenas.Spawn." + p.getLocation().getWorld().getName() + ".Pitch", Float.valueOf(p.getLocation().getPitch()));
						game.saveConfig();

						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Commands.Added")).replace("%arena%", p.getLocation().getWorld().getName()));
						p.playSound(p.getLocation(), XSound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR.parseSound(), 1, 1);
						
					} else {
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Permission")));
						
					}
					
				} else if (args[0].equalsIgnoreCase("delspawn")) {
					
					if (p.hasPermission("kp.command.delspawn")) {
						
						if (Config.getC().contains("Arenas.Spawn." + p.getWorld().getName())) {
							
							ConfigurationSection section = Config.getC().getConfigurationSection("Arenas.Spawn");
							
							if (section.getKeys(false).size() == 1) {
								Config.getC().set("Arenas", null);
								game.saveConfig();
							} else {
								Config.getC().set("Arenas.Spawn." + p.getWorld().getName(), null);
								game.saveConfig();
							}

							p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Commands.Removed")).replace("%arena%", p.getLocation().getWorld().getName()));
							p.playSound(p.getLocation(), XSound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR.parseSound(), 1, 1);
							
						} else {
							
							p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Arena")));
							
						}
						
					} else {
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Permission")));
						
					}
					
				} else if (args[0].equalsIgnoreCase("spawn")) {
					
					if (p.hasPermission("kp.command.spawn")) {
						
						if (Config.getC().contains("Arenas.Spawn." + p.getWorld().getName())) {
							
							if (!spawnUsers.contains(p.getName())) {
								
								spawnUsers.add(p.getName());
								
								p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Commands.Teleporting")));
								p.playSound(p.getLocation(), XSound.ENTITY_ITEM_PICKUP.parseSound(), 1, -1);
								
								Location beforeLocation = p.getLocation();
								
								new BukkitRunnable() {
									
									public int t = Config.getI("Spawn.Time") + 1;
									
									@Override
									public void run() {
										
										t--;
										
										if (t != 0) {
											
											if (p.getGameMode() != GameMode.SPECTATOR) {
												
												p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Commands.Time").replace("%time%", String.valueOf(t))));
												p.playSound(p.getLocation(), XSound.BLOCK_NOTE_BLOCK_SNARE.parseSound(), 1, 1);
												
												if (beforeLocation.getBlockX() != p.getLocation().getBlockX() || beforeLocation.getBlockY() != p.getLocation().getBlockY() || beforeLocation.getBlockZ() != p.getLocation().getBlockZ()) {
													
													p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Moved")));
													spawnUsers.remove(p.getName());
													cancel();
													
												}
												
											} else {
												
												spawnUsers.remove(p.getName());
												cancel();
												
											}
											
										} else {
			
											p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Commands.Teleport")));
											
											arena.toSpawn(p);
											
											if (Config.getB("Arena.ClearKitOnCommandSpawn")) {
												
												for (PotionEffect effect : p.getActivePotionEffects()) {
													p.removePotionEffect(effect.getType());
												}
												
												p.getInventory().setArmorContents(null);
												p.getInventory().clear();
												
												arena.giveItems(p);
												
												arena.getKits().clearKit(p.getName());
												
											}
											
											spawnUsers.remove(p.getName());
											
											p.playSound(p.getLocation(), XSound.ENTITY_ENDERMAN_TELEPORT.parseSound(), 1, 1);
											
											cancel();
											
										}
										
									}
									
								}.runTaskTimer(game, 0L, 20L);
								
							}
						
						} else {
							
							p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Arena")));
							
						}
						
					} else {
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Permission")));
						
					}
					
				} else if (args[0].equalsIgnoreCase("kits")) {
					
					if (p.hasPermission("kp.command.kits")) {
						
						String message = "";
						
						for (String kitName : arena.getKits().getList()) {
							
							String[] fullName = kitName.split(".ym", 2);
							fullName[0] = fullName[0].trim();
							
							if (arena.getKits().getList().size() == 1) {
								
								message += fullName[0];
								
							} else {
								
								if (!isFirst) {
									isFirst = true;
									message += fullName[0];
								} else {
									message += ", " + fullName[0];
								}
								
							}

						}
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Commands.Kits").replace("%kits%", message)));
						isFirst = false;
						
					} else {
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Permission")));
						
					}
					
				} else if (args[0].equalsIgnoreCase("clear")) {
					
					if (p.hasPermission("kp.command.clear")) {
						
						p.getInventory().setArmorContents(null);
						p.getInventory().clear();
						
						for (PotionEffect effect : p.getActivePotionEffects()) {
							p.removePotionEffect(effect.getType());
						}
						
						if (Config.getB("Arena.GiveItemsOnClear")) {
							arena.giveItems(p);
						}
						
						arena.getKits().clearKit(p.getName());
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Commands.Cleared")));
						
					} else {
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Permission")));
						
					}
					
				} else if (args[0].equalsIgnoreCase("stats")) {
					
					if (p.hasPermission("kp.command.stats")) {
						
						if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
							
							for (String msg : resources.getMessages().getStringList("Messages.Stats.Message")) {
								
								p.sendMessage(PlaceholderAPI.setPlaceholders(p, Config.tr(msg
										.replace("%player%", p.getName())
										.replace("%kills%", String.valueOf(arena.getStats().getKills(p.getUniqueId())))
										.replace("%deaths%", String.valueOf(arena.getStats().getDeaths(p.getUniqueId())))
										.replace("%level%", String.valueOf(arena.getLevels().getLevel(p.getUniqueId())))
										.replace("%experience%", String.valueOf(arena.getLevels().getExperience(p.getUniqueId()))))));
										
							}
							
						} else {
							
							for (String msg : resources.getMessages().getStringList("Messages.Stats.Message")) {
								
								p.sendMessage(Config.tr(msg
										.replace("%player%", p.getName())
										.replace("%kills%", String.valueOf(arena.getStats().getKills(p.getUniqueId())))
										.replace("%deaths%", String.valueOf(arena.getStats().getDeaths(p.getUniqueId())))
										.replace("%level%", String.valueOf(arena.getLevels().getLevel(p.getUniqueId())))
										.replace("%experience%", String.valueOf(arena.getLevels().getExperience(p.getUniqueId())))));
								
							}
							
						}
						
						p.playSound(p.getLocation(), XSound.ENTITY_ITEM_PICKUP.parseSound(), 1, 1);
						
					} else {
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Permission")));
						
					}
					
				} else if (args[0].equalsIgnoreCase("menu")) {
					
					if (p.hasPermission("kp.command.menu")) {
						
						KitMenu menu = new KitMenu(resources);
						menu.create(p);
						
					} else {
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Permission")));
						
					}
					
				} else if (args[0].equalsIgnoreCase("import")) {

					if (p.hasPermission("kp.command.import")) {

						if (game.getDatabase().isEnabled()) {
							
							p.sendMessage(Config.tr("%prefix% &7Importing data, please wait..."));
						
							game.getDatabase().importData(resources);

							p.sendMessage(Config.tr("%prefix% &aDatabase data has successfully been exported to the stats.yml."));

						} else {

							p.sendMessage(Config.tr("%prefix% &cImporting is unnecessary, you can switch your storage type and import."));
							
						}

					} else {
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Permission")));
						
					}
					
				} else if (args[0].equalsIgnoreCase("export")) {

					if (p.hasPermission("kp.command.export")) {

						if (game.getDatabase().isEnabled()) {
							
							p.sendMessage(Config.tr("%prefix% &7Exporting data, please wait..."));
							
							game.getDatabase().exportData(resources);
							
							p.sendMessage(Config.tr("%prefix% &aStats successfully exported to database."));

						} else {

							p.sendMessage(Config.tr("%prefix% &cYou are using YAML storage, so exporting is not possible. Change your settings in the config.yml."));

						}
						
					} else {
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Permission")));
						
					}

				}
				
			} else if (args.length == 2) {
				
				if (args[0].equalsIgnoreCase("kit")) {
					
					if (Toolkit.inArena(p)) {
						
						if (!arena.getKits().hasKit(p.getName())) {
								
							arena.getKits().giveKit(args[1], p);
							
						} else {
							
							p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Selected")));
							p.playSound(p.getLocation(), XSound.ENTITY_ENDER_DRAGON_HURT.parseSound(), 1, 1);
							
						}
						
					} else {
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Location")));
						
					}
					
				} else if (args[0].equalsIgnoreCase("create")) {
					
					if (p.hasPermission("kp.command.create")) {
						
						if (Toolkit.inArena(p)) {
							
							arena.getKits().createKit(args[1], p);
							
						} else {

							p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Location")));
							
						}
						
					} else {
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Permission")));
						
					}
					
				} else if (args[0].equalsIgnoreCase("delete")) {
					
					if (p.hasPermission("kp.command.delete")) {
						
						if (Toolkit.inArena(p)) {
							
							if (arena.getKits().getList().contains(args[1] + ".yml")) {
								
								resources.removeKit(args[1]);
								p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Commands.Delete").replace("%kit%", args[1])));
								
							} else {
								
								p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Lost")));
								
							}
							
						}
						
					} else {
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Permission")));
						
					}
					
				} else if (args[0].equalsIgnoreCase("preview")) {
					
					if (p.hasPermission("kp.command.preview")) {
						
						if (Toolkit.inArena(p)) {
							
							if (arena.getKits().isKit(args[1])) {

								PreviewMenu preview = new PreviewMenu(args[1], resources.getKits(args[1]));
								preview.open(p);
								
							} else {
								
								p.sendMessage(Config.tr(Config.tr(resources.getMessages().getString("Messages.Error.Lost"))));
								
							}
							
						}
						
					} else {
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Permission")));
						
					}
					
				} else if (args[0].equalsIgnoreCase("spawn")) {
					
					String arena = args[1];
					
					if (Config.getC().contains("Arenas.Spawn." + arena)) {
						
						if (!game.getArena().getKits().hasKit(p.getName())) {
							
							Location spawn = new Location(Bukkit.getWorld(Config.getS("Arenas.Spawn." + arena + ".World")),
									Config.getI("Arenas.Spawn." + arena + ".X"),
									Config.getI("Arenas.Spawn." + arena + ".Y"),
									Config.getI("Arenas.Spawn." + arena + ".Z"));
							
							p.teleport(spawn);
							p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Commands.Teleport")));
							
						} else {
							
							p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.KitInvalid")));
							
						}
						
					} else {
						
						p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.ExistsArena")));
						
					}
					
				}
				
			}

		}
		
		return false;
		
	}
	
}
