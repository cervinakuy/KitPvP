package com.planetgallium.kitpvp.menu;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.planetgallium.kitpvp.item.KitItem;
import com.planetgallium.kitpvp.util.Menu;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.XMaterial;

public class PreviewMenu {

	private Menu menu;
	private String kit;
	private Resource kitResource;
	
	public PreviewMenu(String kit, Resource kitResource) {
		this.kit = kit;
		this.kitResource = kitResource;
	}
	
	public void open(Player p) {
		
		menu = new Menu("Previewing: " + kit, new PreviewHolder(), 54);
		
		// Armor
		if (kitResource.contains("Inventory.Armor.Helmet")) {
			menu.addItem(new KitItem(kitResource, kit, "Inventory.Armor.Helmet"), 0);
		}
		
		if (kitResource.contains("Inventory.Armor.Chestplate")) {
			menu.addItem(new KitItem(kitResource, kit, "Inventory.Armor.Chestplate"), 1);
		}
		
		if (kitResource.contains("Inventory.Armor.Leggings")) {
			menu.addItem(new KitItem(kitResource, kit, "Inventory.Armor.Leggings"), 2);
		}
		
		if (kitResource.contains("Inventory.Armor.Boots")) {
			menu.addItem(new KitItem(kitResource, kit, "Inventory.Armor.Boots"), 3);
		}
		
		// Hotbar
		for (int i = 0; i < 9; i++) {
			if (kitResource.contains("Inventory.Items." + i)) {
				menu.addItem(new KitItem(kitResource, kit, "Inventory.Items." + i), (45 + i));
			}
		}
		
		// Items
		for (int i = 9; i < 36; i++) {
			if (kitResource.contains("Inventory.Items." + i)) {
				menu.addItem(new KitItem(kitResource, kit, "Inventory.Items." + i), (i + 9));
			}
		}
		
		// Fill
		if (kitResource.contains("Inventory.Items.Fill")) {
			for (int i = 18; i < 54; i++) {
				if (menu.getSlot(i) == null) {
					menu.addItem(new KitItem(kitResource, kit, "Inventory.Items.Fill"), i);
				}
			}
		}
		
		menu.addItem("&cBack to Kits", XMaterial.ARROW.parseMaterial().get(), new ArrayList<String>(), 8);
		
		menu.openMenu(p);
		
	}
	
}
