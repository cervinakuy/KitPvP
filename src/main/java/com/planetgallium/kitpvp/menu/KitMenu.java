package com.planetgallium.kitpvp.menu;

import com.planetgallium.kitpvp.util.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class KitMenu {

	private Menu menu;
	private final Resources resources;
	
	public KitMenu(Resources resources) {
		this.resources = resources;
		rebuildCache();
	}

	private void create() {
		this.menu = new Menu(resources.getMenu().fetchString("Menu.General.Title"), new KitHolder(), resources.getMenu().getInt("Menu.General.Size"));

		ConfigurationSection section = resources.getMenu().getConfigurationSection("Menu.Items");

		for (String slot : section.getKeys(false)) {
			String itemPath = "Menu.Items." + slot;

			String name = resources.getMenu().fetchString(itemPath + ".Name");
			Material material = Toolkit.safeMaterial(resources.getMenu().fetchString(itemPath + ".Material"));
			List<String> lore = resources.getMenu().getStringList(itemPath + ".Lore");

			menu.addItem(name, material, lore, Integer.parseInt(slot));
		}
	}

	public void rebuildCache() {
		create();
	}

	public void open(Player p) {
		menu.openMenu(p);
	}
	
}
