package com.planetgallium.kitpvp.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.XMaterial;

public class PotionItem {

	private PotionEffectType type;
	private int level;
	private int duration;
	private boolean isSplash;
	
	public PotionItem(Resource resource, String path) {
		
		this.type = PotionEffectType.getByName(resource.getString(path + ".Type"));
		this.level = resource.getInt(path + ".Level");
		this.duration = resource.getInt(path + ".Duration");
		this.isSplash = resource.getBoolean(path + ".Splash");
		
	}
	
	public ItemStack convertToPotion(ItemStack toConvert) {
		
		ItemStack newItem = isSplash ? XMaterial.SPLASH_POTION.parseItem() : XMaterial.POTION.parseItem();
		newItem.setItemMeta(toConvert.getItemMeta());
		PotionMeta potionMeta = (PotionMeta) newItem.getItemMeta();
		
		potionMeta.addCustomEffect(new PotionEffect(type, duration * 20, level - 1), true);
		
		newItem.setItemMeta(potionMeta);
		
		return newItem;
		
	}
	
	public PotionEffectType getType() { return type; }
	
	public int getLevel() { return level; }
	
	public int getDuration() { return duration; }
	
	public boolean isSplash() { return isSplash; }
	
}
