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
	
	public ItemStack toItemStack() {
		
		ItemStack item = isSplash ? XMaterial.SPLASH_POTION.parseItem() : XMaterial.POTION.parseItem();
		PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
		
		potionMeta.addCustomEffect(new PotionEffect(type, duration, level), true);
		
		item.setItemMeta(potionMeta);
		
		return item;
		
	}
	
	public PotionEffectType getType() { return type; }
	
	public int getLevel() { return level; }
	
	public int getDuration() { return duration; }
	
	public boolean isSplash() { return isSplash; }
	
}
