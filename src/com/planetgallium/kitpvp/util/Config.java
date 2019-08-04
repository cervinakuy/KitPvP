package com.planetgallium.kitpvp.util;

import org.bukkit.configuration.file.FileConfiguration;

import com.planetgallium.kitpvp.Game;

import net.md_5.bungee.api.ChatColor;

public class Config {
	
	public static String tr(String s) {
		
		return ChatColor.translateAlternateColorCodes('&', s.replace("%prefix%", Game.getInstance().getPrefix()));
		
	}
	
	public static String getS(String path) {
		
		return ChatColor.translateAlternateColorCodes('&', Game.getInstance().getConfig().getString(path).replace("%prefix%", Game.getInstance().getPrefix()));
		
	}
	
	public static boolean getB(String path) {
		
		return Game.getInstance().getConfig().getBoolean(path);
		
	}
	
	public static int getI(String path) {
		
		return Game.getInstance().getConfig().getInt(path);
		
	}
	
	public static FileConfiguration getC() {
		
		return Game.getInstance().getConfig();
		
	}
	
}
