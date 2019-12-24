package com.planetgallium.kitpvp.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.XEnchantment;

public class EnchantedItem {
	
	private Map<Enchantment, Integer> enchantments;
	
	public EnchantedItem(Resource resource, String path) {
		
		this.enchantments = new HashMap<Enchantment, Integer>();
		
		ConfigurationSection section = resource.getConfigurationSection(path);
		
		for (String identifier : section.getKeys(false)) {
			
			enchantments.put(XEnchantment.matchEnchantment(identifier), resource.getInt(path + "." + identifier + ".Level"));
			
		}
		
	}
	
	public ItemStack convertToEnchantedItem(ItemStack toConvert) {
		
		ItemStack newEnchanted = toConvert;
		newEnchanted.addUnsafeEnchantments(enchantments);
		
		return newEnchanted;
		
	}
	
	public Map<Enchantment, Integer> getEnchantments() { return enchantments; }
	
}
