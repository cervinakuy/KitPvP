package com.planetgallium.kitpvp.util;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.planetgallium.kitpvp.Game;

import me.clip.placeholderapi.PlaceholderAPI;

public class Toolkit {
	
	public static boolean inArena(World world) {
		
		if (Config.getC().contains("Arenas.Spawn")) {
			
			if (Config.getC().contains("Arenas.Spawn." + world.getName())) {
				
				return true;
				
			}
			
		} else {
			
			Bukkit.getConsoleSender().sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &cThere was no spawn found, please set it using /kp addspawn."));
			
		}
		
		return false;
		
	}	
	
	public static boolean inArena(Entity en) {
		
		if (Config.getC().contains("Arenas.Spawn")) {
			
			if (Config.getC().contains("Arenas.Spawn." + en.getWorld().getName())) {
				
				return true;
				
			}
			
		} else {
			
			Bukkit.getConsoleSender().sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &cThere was no spawn found, please set it using /kp addspawn."));
			
		}
		
		return false;
		
	}	
	
	public static boolean inArena(Player p) {
		
		if (Config.getC().contains("Arenas.Spawn")) {
			
			if (Config.getC().contains("Arenas.Spawn." + p.getWorld().getName())) {
				
				return true;
				
			}
			
		} else {
			
			Bukkit.getConsoleSender().sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &cThere was no spawn found, please set it using /kp addspawn."));
			
		}
		
		return false;
		
	}
	
 	public static String[] getNearestPlayer(Player player) {
		
		String nearest = "player:100000.0";
		
		for (Player all : Bukkit.getWorld(player.getWorld().getName()).getPlayers()) {
			
			String[] list = nearest.split(":");
			double cal = player.getLocation().distance(all.getLocation());
			
			if (cal <= Double.parseDouble(list[1]) && all != player) {
				
				nearest = all.getName() + ":" + cal;
				
			}
			
		}
		
		if (nearest.equals("player:100000.0")) {
			
			return null;
                
		}
		
		return nearest.split(":");
		
	}
 	
 	public static double round(double value, int precision) {
 		
 	    int scale = (int) Math.pow(10, precision);
 	    return (double) Math.round(value * scale) / scale;
 	    
 	}
 	
 	public static Color stringToColor(String kitName, String path) {
 		
 		return Color.fromRGB(Game.getInstance().getResources().getKits(kitName).getInt(path + ".Red"), Game.getInstance().getResources().getKits(kitName).getInt(path + ".Green"), Game.getInstance().getResources().getKits(kitName).getInt(path + ".Blue"));
 		
 	}
 	
 	public static void runCommands(Resource resource, String path, Player p) {
			
 		if (resource.getBoolean(path + ".Commands.Enabled")) {
 			
 			for (String list : resource.getStringList(path + ".Commands.Commands")) {
 				
 				String[] command = list.split(":", 2);
 			    command[1] = command[1].trim();
 				
 			    if (command[0].equals("console")) {
 					
 					if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
 						
 						String withPlaceholders = PlaceholderAPI.setPlaceholders(p, command[1].replace("%player%", p.getName()));
 						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), withPlaceholders);
 						
 					} else {
 						
 						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command[1].trim().replace("%player%", p.getName()));
 						
 					}
 					
 				} else if (command[0].equals("player")) {
 					
 					if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
 						
 						String withPlaceholders = PlaceholderAPI.setPlaceholders(p, command[1].trim().replace("%player%", p.getName()));
 						p.performCommand(withPlaceholders);
 						
 					} else {
 						
 						p.performCommand(command[1].replace("%player%", p.getName()));
 						
 					}

 				}
 				
 	        }
 			
 		}
 		
 	}
 	
 	public static void runCommands(Resource resource, String path, Player p, String place, String placeTo) {
		
 		if (resource.getBoolean(path + ".Commands.Enabled")) {
 			
 			for (String list : resource.getStringList(path + ".Commands.Commands")) {
 				
 				String[] command = list.split(":", 2);
 			    command[1] = command[1].trim();
 				
 			    if (command[0].equals("console")) {
 					
 					if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
 						
 						String withPlaceholders = PlaceholderAPI.setPlaceholders(p, command[1].replace("%player%", p.getName()).replace(place, placeTo));
 						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), withPlaceholders);
 						
 					} else {
 						
 						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command[1].trim().replace("%player%", p.getName()).replace(place, placeTo));
 						
 					}
 					
 				} else if (command[0].equals("player")) {
 					
 					if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
 						
 						String withPlaceholders = PlaceholderAPI.setPlaceholders(p, command[1].trim().replace("%player%", p.getName()).replace(place, placeTo));
 						p.performCommand(withPlaceholders);
 						
 					} else {
 						
 						p.performCommand(command[1].replace("%player%", p.getName()).replace(place, placeTo));
 						
 					}

 				}
 				
 	        }
 			
 		}
 		
 	}
 	
 	public static void runCommands(String path, Player p) {
		
 		if (Config.getB(path + ".Commands.Enabled")) {
 			
 			for (String list : Config.getC().getStringList(path + ".Commands.Commands")) {
 				
 				String[] command = list.split(":", 2);
 			    command[1] = command[1].trim();
 				
 			    if (command[0].equals("console")) {
 					
 					if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
 						
 						String withPlaceholders = PlaceholderAPI.setPlaceholders(p, command[1].replace("%player%", p.getName()));
 						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), withPlaceholders);
 						
 					} else {
 						
 						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command[1].trim().replace("%player%", p.getName()));
 						
 					}
 					
 				} else if (command[0].equals("player")) {
 					
 					if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
 						
 						String withPlaceholders = PlaceholderAPI.setPlaceholders(p, command[1].trim().replace("%player%", p.getName()));
 						p.performCommand(withPlaceholders);
 						
 					} else {
 						
 						p.performCommand(command[1].replace("%player%", p.getName()));
 						
 					}

 				}
 				
 	        }
 			
 		}
 		
 	}
 	
	public static void runCommands(String path, Player p, String replace, String replaceTo) {
		
 		if (Config.getB(path + ".Commands.Enabled")) {
 			
 			for (String list : Config.getC().getStringList(path + ".Commands.Commands")) {
 				
 				String[] command = list.split(":", 2);
 			    command[1] = command[1].trim();
 				
 			    if (command[0].equals("console")) {
 					
 					if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
 						
 						String withPlaceholders = PlaceholderAPI.setPlaceholders(p, command[1].replace("%player%", p.getName()).replace(replace, replaceTo));
 						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), withPlaceholders);
 						
 					} else {
 						
 						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command[1].trim().replace("%player%", p.getName()).replace(replace, replaceTo));
 						
 					}
 					
 				} else if (command[0].equals("player")) {
 					
 					if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
 						
 						String withPlaceholders = PlaceholderAPI.setPlaceholders(p, command[1].trim().replace("%player%", p.getName()).replace(replace, replaceTo));
 						p.performCommand(withPlaceholders);
 						
 					} else {
 						
 						p.performCommand(command[1].replace("%player%", p.getName()).replace(replace, replaceTo));
 						
 					}

 				}
 				
 	        }
 			
 		}
 		
 	}
	
	public static void runKillCommands(Player victim, Player killer) {
		
 		if (Config.getB("Kill.Commands.Enabled")) {
 			
 			for (String list : Config.getC().getStringList("Kill.Commands.Commands")) {
 				
 				String[] command = list.split(":", 2);
 			    command[1] = command[1].trim();
 				
 			    if (command[0].equals("console")) {
 					
 					if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
 						
 						String withPlaceholders = PlaceholderAPI.setPlaceholders(killer, command[1].replace("%victim%", victim.getName()).replace("%killer%", killer.getName()));
 						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), withPlaceholders);
 						
 					} else {
 						
 						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command[1].trim().replace("%victim%", victim.getName()).replace("%killer%", killer.getName()));
 						
 					}
 					
 				} else if (command[0].equals("player")) {
 					
 					if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
 						
 						String withPlaceholders = PlaceholderAPI.setPlaceholders(killer, command[1].trim().replace("%victim%", victim.getName()).replace("%killer%", killer.getName()));
 						killer.performCommand(withPlaceholders);
 						
 					} else {
 						
 						killer.performCommand(command[1].replace("%victim%", victim.getName()).replace("%killer%", killer.getName()));
 						
 					}

 				}
 				
 	        }
 			
 		}
 		
	}
 	
 	public static boolean hasPlaceholders() {
 		
 		return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
 		
 	}
 	
 	private static int versionToNumber() {
 		
 		if (Bukkit.getVersion().contains("1.8")) {
 			
 			return 18;
 			
 		} else if (Bukkit.getVersion().contains("1.9")) {
 			
 			return 19;
 			
 		} else if (Bukkit.getVersion().contains("1.10")) {
 			
 			return 110;
 			
 		} else if (Bukkit.getVersion().contains("1.11")) {
 			
 			return 111;
 			
 		} else if (Bukkit.getVersion().contains("1.12")) {
 			
 			return 112;
 			
 		} else if (Bukkit.getVersion().contains("1.13")) {
 			
 			return 113;
 			
 		} else if (Bukkit.getVersion().contains("1.14")) {
 			
 			return 114;
 			
 		}
 		
 		return -1;
 		
 	}
 	
 	@SuppressWarnings("deprecation")
	public static ItemStack getMainHandItem(Player p) {
 		
 		if (versionToNumber() == 18) {
 			
 			return p.getItemInHand();
 			
 		} else if (versionToNumber() > 18) {
 			
 			return p.getInventory().getItemInMainHand();
 			
 		}
 		
 		return p.getItemInHand();
 		
 	}
 	
 	@SuppressWarnings("deprecation")
	public static void setMainHandItem(Player p, ItemStack item) {
 		
 		if (versionToNumber() == 18) {
 			
 			p.setItemInHand(item);
 			
 		} else if (versionToNumber() > 18) {
 			
 			p.getInventory().setItemInMainHand(item);
 			
 		}
 		
 		p.setItemInHand(item);
 		
 	}
 	
 	@SuppressWarnings("deprecation")
	public static ItemStack getOffhandItem(Player p) {
 		
 		if (versionToNumber() > 18) {
 			
 			return p.getInventory().getItemInOffHand();
 			
 		} else {
 			
 			return p.getItemInHand();
 			
 		}
 		
 	}
	
}
