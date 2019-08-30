package com.planetgallium.kitpvp.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.XMaterial;

@SuppressWarnings("deprecation")
public class PotionItem {

	private String type;
	private boolean isSplash;
	
	private int level;
	private int duration;
	private boolean isExtended;
	private boolean isUpgraded;
	
	public PotionItem(Resource resource, String path) {
		
		this.type = resource.getString(path + ".Type");
		this.isSplash = resource.getBoolean(path + ".Splash");
		
		if (Toolkit.versionToNumber() == 18) {
		
			this.level = resource.getInt(path + ".Level");
			this.duration = resource.getInt(path + ".Duration");
			
		} else if (Toolkit.versionToNumber() >= 19) {
			
			this.isExtended = resource.getBoolean(path + ".Extended");
			this.isUpgraded = resource.getBoolean(path + ".Upgraded");
			
		}
		
	}
	
	public ItemStack convertToPotion(ItemStack toConvert) {
		
		if (Toolkit.versionToNumber() == 18) {
			
			Potion potion = Potion.fromItemStack(toConvert);
			potion.setSplash(isSplash);
			
			ItemStack newItem = potion.toItemStack(toConvert.getAmount());
			newItem.setItemMeta(toConvert.getItemMeta());
			PotionMeta potionMeta = (PotionMeta) newItem.getItemMeta();
			
			potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(type), duration * 20, level - 1), true);
			
			newItem.setItemMeta(potionMeta);
			
			return newItem;
			
		} else if (Toolkit.versionToNumber() >= 19) {
			
			ItemStack newItem = new ItemStack(isSplash ? XMaterial.SPLASH_POTION.parseMaterial() : XMaterial.POTION.parseMaterial());
			newItem.setItemMeta(toConvert.getItemMeta());
			PotionMeta potionMeta = (PotionMeta) newItem.getItemMeta();
			
			potionMeta.setBasePotionData(new PotionData(PotionType.valueOf(type), isExtended, isUpgraded));
			
			newItem.setItemMeta(potionMeta);
			
			return newItem;
			
		}
		
		return toConvert;
		
	}
	
	public String getType() { return type; }
	
	public boolean isSplash() { return isSplash; }
	
	public int getLevel() { return level; }
	
	public int getDuration() { return duration; }
	
	public boolean isExtended() { return isExtended; }
	
	public boolean isUpgraded() { return isUpgraded; }
	
}
