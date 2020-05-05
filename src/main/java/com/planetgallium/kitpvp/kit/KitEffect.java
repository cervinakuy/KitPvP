package com.planetgallium.kitpvp.kit;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.XPotion;

public class KitEffect {

	private PotionEffectType type;
	private int duration;
	private int amplifier;
	
	public KitEffect(Resource resource, String type) {

		if (resource.contains("Effects." + type)) {
			this.type = XPotion.matchXPotion(type).get().parsePotionEffectType();
		}
		
		if (resource.contains("Effects." + type + ".Duration")) {
			this.duration = resource.getInt("Effects." + type + ".Duration");
		}
		
		if (resource.contains("Effects." + type + ".Amplifier")) {
			this.amplifier = resource.getInt("Effects." + type + ".Amplifier");
		}
		
	}
	
	public PotionEffect toPotionEffect() {
		
		return new PotionEffect(type == null ? PotionEffectType.CONFUSION : type,
								duration == 0 ? 10 : duration,
								amplifier == 0 ? 1 : amplifier - 1);
		
	}
	
}
