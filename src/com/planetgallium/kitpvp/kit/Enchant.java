package com.planetgallium.kitpvp.kit;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import com.planetgallium.kitpvp.Game;

public class Enchant {

	private List<String> enchantments;
	private List<Integer> levels;
	
	public Enchant(String kit, String path) {
		
		this.enchantments = new ArrayList<String>();
		this.levels = new ArrayList<Integer>();
		
		if (Game.getInstance().getResources().getKits(kit).contains(path + ".Enchantments")) {
			
			ConfigurationSection section = Game.getInstance().getResources().getKits(kit).getConfigurationSection(path + ".Enchantments");
			
			for (String identifier : section.getKeys(false)) {
				
				enchantments.add(identifier);
				levels.add(Game.getInstance().getResources().getKits(kit).getInt(path + ".Enchantments." + identifier));
				
			}
			
		}
		
	}
	
	public List<String> getEnchantments() { return enchantments; }
	
	public List<Integer> getLevels() { return levels; }
	
}
