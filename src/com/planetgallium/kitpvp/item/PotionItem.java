package com.planetgallium.kitpvp.item;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.XPotion;

@SuppressWarnings("deprecation")
public class PotionItem {
	
	private List<PotionEffect> effects;
	private PotionData potionData;
	
	private String type;
	
	public PotionItem(Resource resource, String path) {

		this.effects = new ArrayList<PotionEffect>();
		this.type = resource.getString(path + ".Type");
		
		for (String identifier : resource.getConfigurationSection(path).getKeys(false)) {

			if (!identifier.equals("Type")) {
				
				String name = identifier;
				
				if (Toolkit.versionToNumber() >= 19 && resource.contains(path + "." + name + ".Upgraded")) {
					boolean extended = resource.getBoolean(path + "." + name + ".Extended");
					boolean upgraded = resource.getBoolean(path + "." + name + ".Upgraded");
					potionData = new PotionData(PotionType.valueOf(name), extended, upgraded);
				} else {
					int level = resource.getInt(path + "." + name + ".Level");
					int duration = resource.getInt(path + "." + name + ".Duration") * 20;
					effects.add(new PotionEffect(XPotion.matchPotionType(name), duration, level - 1));
				}
				
			}
			
		}
		
	}
	
	public ItemStack convertToPotion(ItemStack toConvert) {
		
		if (Toolkit.versionToNumber() == 18) {
			
			Potion potion = Potion.fromItemStack(toConvert);
			potion.setSplash(type.equals("SPLASH_POTION"));
			
			ItemStack newItem = potion.toItemStack(toConvert.getAmount());
			newItem.setItemMeta(toConvert.getItemMeta());
			PotionMeta potionMeta = (PotionMeta) newItem.getItemMeta();
			
			for (PotionEffect effect : effects) {
				potionMeta.addCustomEffect(effect, true);
			}
			
			newItem.setItemMeta(potionMeta);
			
			return newItem;
			
		} else if (Toolkit.versionToNumber() >= 19) {
			
			ItemStack newItem = new ItemStack(toConvert.getType());
			newItem.setItemMeta(toConvert.getItemMeta());
			PotionMeta potionMeta = (PotionMeta) newItem.getItemMeta();
			
			if (potionData == null) {
				for (PotionEffect effect : effects) {
					potionMeta.addCustomEffect(effect, true);
				}
			} else {
				potionMeta.setBasePotionData(potionData);
			}
			
			newItem.setItemMeta(potionMeta);
			
			return newItem;
			
		}
		
		return toConvert;
		
	}
	
}
