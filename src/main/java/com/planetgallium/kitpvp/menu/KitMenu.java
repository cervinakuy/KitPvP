package com.planetgallium.kitpvp.menu;

import com.planetgallium.kitpvp.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import com.planetgallium.kitpvp.Game;

import me.clip.placeholderapi.PlaceholderAPI;

import java.util.List;

public class KitMenu {

	private Menu menu;
	private FileConfiguration menuConfig;
	
	public KitMenu(Resources resources) {
		this.menuConfig = resources.getMenu();
		create();
	}

	private void create() {

		this.menu = new Menu(menuConfig.getString("Menu.General.Title"), new KitHolder(), menuConfig.getInt("Menu.General.Size"));

		ConfigurationSection section = menuConfig.getConfigurationSection("Menu.Items");

		for (String slot : section.getKeys(false)) {

			String itemPath = "Menu.Items." + slot;
			String name = menuConfig.getString(itemPath + ".Name");
			Material material = XMaterial.matchXMaterial(menuConfig.getString(itemPath + ".Material")).get().parseMaterial().get();
			List<String> lore = menuConfig.getStringList(itemPath + ".Lore");

			menu.addItem(name, material, lore, Integer.valueOf(slot));

		}

	}

	public void open(Player p) {

		menu.openMenu(p);

	}
	
}
