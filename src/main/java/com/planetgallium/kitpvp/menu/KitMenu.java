package com.planetgallium.kitpvp.menu;

import com.cryptomorin.xseries.XMaterial;
import com.planetgallium.kitpvp.util.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class KitMenu {

	private Menu menu;
	private Resources resources;
	
	public KitMenu(Resources resources) {
		this.resources = resources;
		create();
	}

	private void create() {

		this.menu = new Menu(resources.getMenu().getString("Menu.General.Title"), new KitHolder(), resources.getMenu().getInt("Menu.General.Size"));

		ConfigurationSection section = resources.getMenu().getConfigurationSection("Menu.Items");

		for (String slot : section.getKeys(false)) {

			String itemPath = "Menu.Items." + slot;

			String name = resources.getMenu().getString(itemPath + ".Name");
			Material material = XMaterial.matchXMaterial(resources.getMenu().getString(itemPath + ".Material")).get().parseMaterial();
			List<String> lore = resources.getMenu().getStringList(itemPath + ".Lore");

			menu.addItem(name, material, lore, Integer.valueOf(slot));

		}

	}

	public void clearCache() {
		create();
	}

	public void open(Player p) {
		menu.openMenu(p);
	}
	
}
