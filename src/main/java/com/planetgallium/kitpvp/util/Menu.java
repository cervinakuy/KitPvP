package com.planetgallium.kitpvp.util;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Menu {

	private final String title;
	private final int size;
	private final Inventory menu;
	private final InventoryHolder owner;
	
	public Menu(String title, InventoryHolder owner, int size) {
		this.menu = Bukkit.createInventory(owner, size, Toolkit.translate(title));
		this.title = title;
		this.size = size;
		this.owner = owner;
	}
	
	public void addItem(String name, Material material, List<String> lore, int slot) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();

		Toolkit.colorizeList(lore);
		
		meta.setDisplayName(Toolkit.translate(name));
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		menu.setItem(slot, item);
	}
	
	public void addItem(String name, Material material, List<String> lore, int amount, int slot) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();

		Toolkit.colorizeList(lore);

		meta.setDisplayName(Toolkit.translate(name));
		meta.setLore(lore);
		item.setAmount(amount > 0 ? amount : 1);
		item.setItemMeta(meta);
		
		menu.setItem(slot, item);
	}

	public void setItem(ItemStack item, int slot) {
		menu.setItem(slot, item);
	}
	
	public void openMenu(Player p) {
		p.openInventory(menu);
	}
	
	public void closeMenu(Player p) {
		p.closeInventory();
	}
	
	public ItemStack getSlot(int slot) { return menu.getItem(slot); }
	
	public String getTitle() { return title; }
	
	public InventoryHolder getOwner() { return owner; }
	
	public int getSize() { return size; }
	
}
