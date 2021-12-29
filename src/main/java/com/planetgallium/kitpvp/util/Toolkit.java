package com.planetgallium.kitpvp.util;

import java.util.ArrayList;
import java.util.List;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.planetgallium.kitpvp.Game;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class Toolkit {
	
	public static boolean inArena(World world) {

		if (Game.getInstance().getResources().getConfig().contains("Arenas")) {
			return Game.getInstance().getResources().getConfig().contains("Arenas." + world.getName());
		} else {
			Toolkit.printToConsole("&7[&b&lKIT-PVP&7] &cThere was no spawn found, please set it using /kp addspawn.");
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

 	public static void runCommands(Player p, List<String> commands, String replaceFrom, String replaceTo) {

		if (commands == null) return;

		for (String commandString : commands) {

			String[] commandPhrase = commandString.split(":", 2);

			if (commandPhrase.length == 1) {
				Toolkit.printToConsole("&7[&b&lKIT-PVP&7] &cIncorrect command format. Please see: &fhttps://bit.ly/kp-command-format");
				return;
			}

			commandPhrase[1] = commandPhrase[1].trim();

			String sender = commandPhrase[0];
			String command = commandPhrase[1];

			if (sender.equals("console")) {

				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), addPlaceholdersIfPossible(p, command.replace("%player%", p.getName()).replace(replaceFrom, replaceTo)));

			} else if (sender.equals("player")) {

				p.performCommand(addPlaceholdersIfPossible(p, command.replace("%player%", p.getName()).replace(replaceFrom, replaceTo)));

			} else {

				Toolkit.printToConsole("&7[&b&lKIT-PVP&7] &cIncorrect command format. Please see: &fhttps://bit.ly/kp-command-format");
				return;

			}

		}

	}
 	
 	public static int versionToNumber() {

		String version = Bukkit.getVersion();

		if (version.contains("1.8")) {
			return 18;
		} else if (version.contains("1.9")) {
			return 19;
		} else if (version.contains("1.10")) {
			return 110;
		} else if (version.contains("1.11")) {
			return 111;
		} else if (version.contains("1.12")) {
			return 112;
		} else if (version.contains("1.13")) {
			return 113;
		} else if (version.contains("1.14")) {
			return 114;
		} else if (version.contains("1.15")) {
			return 115;
		} else if (version.contains("1.16")) {
			return 116;
		} else if (version.contains("1.17")) {
			return 117;
		} else if (version.contains("1.18")) {
			return 118;
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

 		if (list != null) {
			for (String string : list) {
				newList.add(ChatColor.translateAlternateColorCodes('&', string));
			}
			return newList;
		}

 		return null;
 		
 	}

 	public static List<String> replaceInList(List<String> list, String find, String replace) {

		List<String> newList = new ArrayList<>();

		for (String string : list) {
			newList.add(string.replace(find, replace));
		}

		return newList;

	}

 	public static String toNormalColorCodes(String string) {

		if (string != null) {
			return string.replace("§", "&");
		}

		return null;

	}

	public static List<String> toNormalColorCodes(List<String> list) {

		List<String> newList = new ArrayList<>();

		if (list != null) {
			for (String line : list) {
				newList.add(toNormalColorCodes(line));
			}
			return newList;
		}
		return null;

	}

 	public static Player getPlayer(World world, String name) {

		for (Player player : world.getPlayers()) {
			if (player.getName().equals(name)) {
				return player;
			}
		}
		return null;

	}

	public static void saveLocationToResource(Resource resource, String path, Location location) {

		resource.set(path + ".World", location.getWorld().getName());
		resource.set(path + ".X", location.getBlockX());
		resource.set(path + ".Y", location.getBlockY());
		resource.set(path + ".Z", location.getBlockZ());
		resource.set(path + ".Yaw", location.getYaw());
		resource.set(path + ".Pitch", location.getPitch());
		resource.save();

	}

	public static Location getLocationFromResource(Resource resource, String path) {

		return new Location(Bukkit.getWorld(resource.getString(path + ".World")),
				(float) resource.getInt(path + ".X") + 0.5,
				(float) resource.getInt(path + ".Y"),
				(float) resource.getInt(path + ".Z") + 0.5,
				(float) resource.getDouble(path + ".Yaw"),
				(float) resource.getDouble(path + ".Pitch"));

	}

	public static String addPlaceholdersIfPossible(Player player, String text) {

		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			text = PlaceholderAPI.setPlaceholders(player, text);
		}

		return Toolkit.translate(text);

	}

	// TODO: eventually merge the below two methods into one
	public static double getPermissionAmountDouble(Player player, String permissionPrefix, double defaultValue) {
		double highestPermissionValue = 0.0;
		for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
			String permission = attachmentInfo.getPermission();
			if (permission.startsWith(permissionPrefix)) {
				// strips from some.permission.49 to just 49
				double permissionValue = Double.parseDouble(permission.substring(permissionPrefix.length()));
				if (permissionValue > highestPermissionValue) {
					highestPermissionValue = permissionValue;
				}
			}
		}

		return highestPermissionValue == 0.0 ? defaultValue : highestPermissionValue;

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

	public static String translate(String s) {

		return ChatColor.translateAlternateColorCodes('&', s.replace("%prefix%", Game.getPrefix()));

	}

	public static int getNextAvailable(FileConfiguration yamlConfig, String path, int limit, boolean zeroBased, int fallbackAmount) {

		for (int i = zeroBased ? 0 : 1; i < limit; i++) {
			if (!yamlConfig.contains(path + "." + i)) {
				return i;
			}
		}
		return fallbackAmount;

	}

	public static boolean containsAnyThatStartWith(List<String> list, String valueToTest) {

		for (String string : list) {
			if (valueToTest.startsWith(string)) {
				return true;
			}
		}
		return false;

	}

	public static boolean hasMatchingDisplayName(ItemStack item, String targetDisplayName) {

		ItemMeta meta = item.getItemMeta();

		if (item.hasItemMeta() && meta != null) {
			return meta.hasDisplayName() && Toolkit.translate(meta.getDisplayName()).equals(targetDisplayName);
		}

		return false;

	}

	public static boolean matchesConfigItem(ItemStack item, Resource resource, String path) {

		ItemMeta itemMeta = item.getItemMeta();
		String configItemName = resource.getString(path + ".Name");
		String configItemMaterial = resource.getString(path + ".Material");

		if (item.getType() == XMaterial.matchXMaterial(configItemMaterial).get().parseMaterial()) {
			if (itemMeta != null && itemMeta.hasDisplayName()) {
				return Toolkit.translate(itemMeta.getDisplayName()).equals(configItemName);
			}
		}
		return false;

	}

	public static void printToConsole(String message) {
		Bukkit.getConsoleSender().sendMessage(Toolkit.translate(message));
	}

	public static String capitalizeFirstChar(String word) {
		return word.substring(0, 1).toUpperCase() + word.substring(1);
	}

	public static void setMaxHealth(Player p, int amount) {

		if (Toolkit.versionToNumber() >= 19) {
			AttributeInstance healthAttribute = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
			assert healthAttribute != null;
			healthAttribute.setBaseValue(amount);
			return;
		}

		p.setMaxHealth(amount);

	}

	public static int getMaxHealth(Player p) {

		if (Toolkit.versionToNumber() >= 19) {
			AttributeInstance healthAttribute = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
			assert healthAttribute != null;
			return (int) healthAttribute.getValue();
		}

		return (int) p.getMaxHealth();

	}

}
