package com.planetgallium.kitpvp.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Toolkit;

public class EnchantedItem {
	
	private Map<Enchantment, Integer> enchantments;
	
	@SuppressWarnings("deprecation")
	public EnchantedItem(Resource resource, String path) {
		
		this.enchantments = new HashMap<Enchantment, Integer>();
		
		ConfigurationSection section = resource.getConfigurationSection(path);
		
		if (Toolkit.versionToNumber() < 112) {
			
			for (String identifier : section.getKeys(false)) {
				
				enchantments.put(Enchantment.getByName(identifier.toUpperCase()), resource.getInt(path + "." + identifier + ".Level"));
				
			}
			
		} else if (Toolkit.versionToNumber() >= 113) {
			
			for (String identifier : section.getKeys(false)) {
				
				enchantments.put(Enchantment.getByKey(NamespacedKey.minecraft(identifier.toLowerCase())), resource.getInt(path + "." + identifier + ".Level"));
				
			}
			
		}
		
	}
	
	public ItemStack convertToEnchantedItem(ItemStack toConvert) {
		
		ItemStack newEnchanted = toConvert;
		newEnchanted.addUnsafeEnchantments(enchantments);
		
		return newEnchanted;
		
	}
	
	public Map<Enchantment, Integer> getEnchantments() { return enchantments; }
	
}
