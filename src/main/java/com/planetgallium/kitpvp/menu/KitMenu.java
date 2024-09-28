package com.planetgallium.kitpvp.menu;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.Kit;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class KitMenu {

	private final Resources resources;
	private final Game game;
	
	public KitMenu(Resources resources, Game game) {
		this.resources = resources;
		this.game = game;
	}

	public void open(Player p) {
		Menu menu = new Menu(resources.getMenu().fetchString("Menu.General.Title"), new KitHolder(), resources.getMenu().getInt("Menu.General.Size"));

		ConfigurationSection section = resources.getMenu().getConfigurationSection("Menu.Items");

		for (String slot : section.getKeys(false)) {
			String itemPath = "Menu.Items." + slot;

			if(!resources.getMenu().exists(itemPath + ".Kit")) break;
			Kit kit = game.getArena().getKits().getKitByName(resources.getMenu().fetchString(itemPath + ".Kit"));

			String name;
			Material material;
			List<String> lore;

			if(game.getArena().getKits().canUseKit(p, kit) || !game.isVaultEnabled()){
				name = resources.getMenu().fetchString(itemPath + ".Name");
				material = Toolkit.safeMaterial(resources.getMenu().fetchString(itemPath + ".Material"));
				lore = resources.getMenu().getStringList(itemPath + ".Lore");
			}else{
				name = resources.getMenu().fetchString(itemPath + ".Not-Purchased.Name");
				material = Toolkit.safeMaterial(resources.getMenu().fetchString(itemPath + ".Not-Purchased.Material"));
				lore = resources.getMenu().getStringList(itemPath + ".Not-Purchased.Lore");
			}



			menu.addItem(name, material, lore, Integer.parseInt(slot));
		}
		menu.openMenu(p);
	}
	
}
