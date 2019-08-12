package com.planetgallium.kitpvp.item;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.planetgallium.kitpvp.util.Resource;

public class PotionItem {

	private PotionEffectType type;
	private int duration;
	private int level;
	
	public PotionItem(Resource resource, String type) {
		
		if (resource.contains("Potions." + type)) {
			this.type = PotionEffectType.getByName(type);
		}
		
		if (resource.contains("Potions." + type + ".Duration")) {
			this.duration = resource.getInt("Potions." + type + ".Duration");
		}
		
		if (resource.contains("Potions." + type + ".Level")) {
			this.level = resource.getInt("Potions." + type + ".Level");
		}
		
	}
	
	public PotionEffect toPotionEffect() {
		
		return new PotionEffect(type == null ? PotionEffectType.SPEED : type,
								duration == 0 ? 10 : duration,
								level == 0 ? 1 : level);
		
	}
	
}
