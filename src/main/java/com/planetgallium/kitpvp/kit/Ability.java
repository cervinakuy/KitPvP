package com.planetgallium.kitpvp.kit;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.planetgallium.kitpvp.util.Resource;

public class Ability {

	private Resource kit;
	private Material activator;
	
	public Ability(Resource kit, Material activator) {
		
		this.kit = kit;
		this.activator = activator;
		
	}
	
	public String getMessage() {
		
		return kit.getString("Ability.Message.Message");
		
	}
	
	public String getSoundName() {
		
		return kit.getString("Ability.Sound.Sound");
		
	}
	
	public int getSoundPitch() {
		
		return kit.getInt("Ability.Sound.Pitch");
		
	}
	
	public List<PotionEffect> getEffects() {
		
		if (kit.contains("Ability.Effects")) {

			List<PotionEffect> potions = new ArrayList<PotionEffect>();
			ConfigurationSection section = kit.getConfigurationSection("Ability.Effects");
			
			for (String identifier : section.getKeys(false)) {

				potions.add(new PotionEffect(PotionEffectType.getByName(identifier.toUpperCase()),
						kit.getInt("Ability.Effects." + identifier + ".Duration") * 20,
						kit.getInt("Ability.Effects." + identifier + ".Amplifier") - 1));
				
			}
			
			return potions;
			
		}
		
		return null;
		
	}
	
	public List<String> getCommands() {
		
		return kit.getStringList("Ability.Commands.Commands");
		
	}
	
	public Material getActivator() { return activator; }
	
}
