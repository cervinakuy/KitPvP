package com.planetgallium.kitpvp.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.planetgallium.kitpvp.util.XMaterial;

public class SkullItem {

	private String owner;
	
	public SkullItem(String owner) {
		
		this.owner = owner;
		
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack convertToSkull(ItemStack toConvert) {
		
		ItemStack newSkull = XMaterial.PLAYER_HEAD.parseItem();
		newSkull.setItemMeta(toConvert.getItemMeta());
		
		SkullMeta skullMeta = (SkullMeta) newSkull.getItemMeta();
		skullMeta.setOwner(owner);
		
		newSkull.setItemMeta(skullMeta);
			
		return newSkull;
		
	}
	
}
