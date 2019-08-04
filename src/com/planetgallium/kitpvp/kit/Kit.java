package com.planetgallium.kitpvp.kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Sounds;

public class Kit {

	private String name;
	
	private ItemStack helmet;
	private ItemStack chestplate;
	private ItemStack leggings;
	private ItemStack boots;
	private ItemStack fill;
	
	private List<PotionEffect> potions = new ArrayList<PotionEffect>();
	private Map<Integer, ItemStack> inventory = new HashMap<Integer, ItemStack>();
	
	public void setName(String name) {
		
		this.name = name;
		
	}
	
	public void setHelmet(ItemStack skull) {
		
		this.helmet = skull;
		
	}
	
	public void setHelmet(Item helmet) {
		
		this.helmet = helmet.toItemStack();
		
	}
	
	public void setChestplate(Item chestplate) {
		
		this.chestplate = chestplate.toItemStack();
	
	}
	
	public void setLeggings(Item leggings) {
		
		this.leggings = leggings.toItemStack();
		
	}
	
	public void setBoots(Item boots) {
		
		this.boots = boots.toItemStack();
		
	}
	
	public void setFill(Item fill) {
		
		this.fill = fill.toItemStack();
		
	}
	
	public void addEffect(PotionEffect effect) {
		
		potions.add(effect);
		
	}
	
	public void addItem(ItemStack item, int slot) {
		
		inventory.put(slot, item);
		
	}
	
	public void addItem(Item item, int slot) {
		
		inventory.put(slot, item.toItemStack());
		
	}
	
	public void applyKit(Player p) {
		
		if (Config.getB("Arena.ClearInventoryOnKit")) {
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
		}
		
		if (helmet != null) p.getInventory().setHelmet(helmet);
		if (chestplate != null) p.getInventory().setChestplate(chestplate);
		if (leggings != null) p.getInventory().setLeggings(leggings);
		if (boots != null) p.getInventory().setBoots(boots);
		
		// POTION EFFECT
		
		for (PotionEffect effect : potions) {
			
			p.addPotionEffect(effect);
			
		}
		
		// SET INVENTORY ITEMS
		
		for (int i = 0; i < 36; i++) {
			
			if (inventory.get(i) != null) {
				
				p.getInventory().setItem(i, inventory.get(i));
				
			}
			
		}
		
		// ITEM FILL
		
		for (int i = 0; i < 36; i++) {
			
			if (p.getInventory().getItem(i) == null) {
				
				p.getInventory().setItem(i, fill);
				
			}
			
		}

		p.sendMessage(Config.tr(Game.getInstance().getResources().getMessages().getString("Messages.Commands.Kit").replace("%kit%", getName())));
		p.playSound(p.getLocation(), Sounds.HORSE_ARMOR.bukkitSound(), 1, 1);
		
	}
	
	public String getName() { return name; }
	
}
