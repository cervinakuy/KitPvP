package com.planetgallium.kitpvp.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.XMaterial;

@SuppressWarnings("deprecation")
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
		
		if (Toolkit.versionToNumber() == 18) {
			
			Potion potion = Potion.fromItemStack(toConvert);
			potion.setSplash(isSplash);
			
			ItemStack newItem = potion.toItemStack(toConvert.getAmount());
			newItem.setItemMeta(toConvert.getItemMeta());
			PotionMeta potionMeta = (PotionMeta) newItem.getItemMeta();
			
			potionMeta.addCustomEffect(new PotionEffect(type, duration * 20, level - 1), true);
			
			newItem.setItemMeta(potionMeta);
			
			return newItem;
			
		} else if (Toolkit.versionToNumber() >= 19) {
			
			ItemStack newItem = new ItemStack(isSplash ? XMaterial.POTION.parseMaterial() : Material.SPLASH_POTION);
			newItem.setItemMeta(toConvert.getItemMeta());
			PotionMeta potionMeta = (PotionMeta) newItem.getItemMeta();
			
			potionMeta.addCustomEffect(new PotionEffect(type, duration * 20, level - 1), true);
			
			newItem.setItemMeta(potionMeta);
			
			return newItem;
			
		}
		
		return toConvert;
		
	}
	
	public PotionEffectType getType() { return type; }
	
	public int getLevel() { return level; }
	
	public int getDuration() { return duration; }
	
	public boolean isSplash() { return isSplash; }
	
}
