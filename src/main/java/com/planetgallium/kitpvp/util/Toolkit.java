package com.planetgallium.kitpvp.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.planetgallium.kitpvp.Game;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class Toolkit {
	
	public static boolean inArena(World world) {

		if (Config.getC().contains("Arenas")) {

			if (Config.getC().contains("Arenas." + world.getName())) {

				return true;

			}

		} else {

			Bukkit.getConsoleSender().sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &cThere was no spawn found, please set it using /kp addspawn."));

		}

		return false;

	}
	
	public static boolean inArena(Entity entity) {
		
		return inArena(entity.getWorld());
		
	}
	
 	public static String[] getNearestPlayer(Player player, int maxY) {
		
		String nearest = "player:100000.0";
		
		for (Player all : Bukkit.getWorld(player.getWorld().getName()).getPlayers()) {
			
			String[] list = nearest.split(":");
			double cal = player.getLocation().distance(all.getLocation());
			
			if (cal <= Double.parseDouble(list[1]) && all != player) {

				if (all.getLocation().getBlockY() < maxY) {

					if (all.getGameMode() != GameMode.SPECTATOR) {

						nearest = all.getName() + ":" + cal;

					}

				}
				
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
 	
 	public static Color getColorFromConfig(FileConfiguration config, String path) {
 		
 		return Color.fromRGB(config.getInt(path + ".Red"),
				config.getInt(path + ".Green"),
				config.getInt(path + ".Blue"));
 		
 	}

 	public static void runCommands(FileConfiguration config, String path, Player p, String replaceFrom, String replaceTo) {

		if (config.getBoolean(path + ".Commands.Enabled")) {

			for (String list : config.getStringList(path + ".Commands.Commands")) {

				String[] commandPhrase = list.split(":", 2);
				commandPhrase[1] = commandPhrase[1].trim();

				String sender = commandPhrase[0];
				String command = commandPhrase[1];

				if (sender.equals("console")) {

					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), addPlaceholdersIfPossible(p, command.replace("%player%", p.getName()).replace(replaceFrom, replaceTo)));

				} else if (sender.equals("player")) {

					p.performCommand(addPlaceholdersIfPossible(p, command.replace("%player%", p.getName()).replace(replaceFrom, replaceTo)));

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
 	
 	public static int versionToNumber() {
 		
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
 			
 		} else if (Bukkit.getVersion().contains("1.15")) {
 			
 			return 115;
 			
 		} else if (Bukkit.getVersion().contains("1.16")) {

 			return 116;

		}

 		return 500;
 		
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
 			
 		} else {
 			
 			p.setItemInHand(item);
 			
 		}
 		
 	}
 	
 	@SuppressWarnings("deprecation")
	public static ItemStack getOffhandItem(Player p) {
 		
 		if (versionToNumber() == 18) {
 			
 			return p.getItemInHand();
 			
 		} else if (versionToNumber() > 18) {
 			
 			return p.getInventory().getItemInOffHand();
 			
 		}
 		
 		return p.getItemInHand();
 		
 	}
 	
 	@SuppressWarnings("deprecation")
	public static void setOffhandItem(Player p, ItemStack item) {
 		
 		if (versionToNumber() == 18) {
 			
 			p.setItemInHand(item);
 			
 		} else if (versionToNumber() > 18) {
 			
 			p.getInventory().setItemInOffHand(item);
 			
 		} else {
 			
 			p.setItemInHand(item);
 			
 		}
 		
 	}
 	
 	public static List<String> colorizeList(List<String> list) {
 		
 		List<String> newList = new ArrayList<String>();
 		
 		for (String string : list) {
 			
 			newList.add(ChatColor.translateAlternateColorCodes('&', string));
 			
 		}
 		
 		return newList;
 		
 	}

 	public static Player getPlayer(World world, String name) {

		for (Player player : world.getPlayers()) {

			if (player.getName().equals(name)) {

				return player;

			}

		}

		return null;

	}

	public static void saveLocationToConfig(Plugin plugin, FileConfiguration config, String path, Location location) {

		config.set(path + ".World", location.getWorld().getName());
		config.set(path + ".X", location.getBlockX());
		config.set(path + ".Y", location.getBlockY());
		config.set(path + ".Z", location.getBlockZ());
		config.set(path + ".Yaw", location.getYaw());
		config.set(path + ".Pitch", location.getPitch());
		plugin.saveConfig();

	}

	public static Location getLocationFromConfig(FileConfiguration config, String path) {

		return new Location(Bukkit.getWorld(config.getString(path + ".World")),
				(float) config.getInt(path + ".X") + 0.5,
				(float) config.getInt(path + ".Y"),
				(float) config.getInt(path + ".Z") + 0.5,
				(float) config.getDouble(path + ".Yaw"),
				(float) config.getDouble(path + ".Pitch"));

	}

	private static String addPlaceholdersIfPossible(Player player, String text) {

		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			text = PlaceholderAPI.setPlaceholders(player, text);
		}

		return text;

	}

	public static int getPermissionAmount(Player player, String permissionPrefix, int defaultValue) {
//		String permissionPrefix = "some.permission.here.";
		if (!player.isOp()) {
			for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
				if (attachmentInfo.getPermission().startsWith(permissionPrefix)) {
					String permission = attachmentInfo.getPermission();
					return Integer.parseInt(permission.substring(permission.lastIndexOf(".") + 1));
				}
			}
		} else {
			return Integer.MAX_VALUE;
		}

		return defaultValue;
	}
	
}
