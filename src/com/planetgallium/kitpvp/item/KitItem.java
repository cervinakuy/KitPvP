package com.planetgallium.kitpvp.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.XMaterial;

public class KitItem {

	private String name;
	private List<String> lore;
	private Map<Enchantment, Integer> enchantments;
	private Material material;
	private int amount;
	private Color color;
	private SkullItem skull;
	
	public KitItem(Resource resource, String kit, String path) {
		
		if (resource.contains(path + ".Name")) {
			this.name = resource.getString(resource.getString(path + ".Name"));
		}
		
		if (resource.contains(path + ".Lore")) {
			this.lore = new ArrayList<String>(resource.getStringList(path + ".Lore"));
		}
		
		if (resource.contains(path + ".Item")) {
			this.material = Material.valueOf(resource.getString(path + ".Item"));
		}
		
		if (resource.contains(path + ".Amount")) {
			this.amount = resource.getInt(path + ".Amount");
		}
		
		if (resource.contains(path + ".Dye")) {
			this.color = Toolkit.stringToColor(kit, path + ".Dye");
		}
		
		if (resource.contains(path + ".Skull")) {
			this.skull = new SkullItem(resource.getString(path + ".Skull"));
		}
		
		if (resource.contains(path + ".Enchantments")) {
			this.enchantments = new HashMap<Enchantment, Integer>();
			
			ConfigurationSection section = resource.getConfigurationSection(path + ".Enchantments");
			
			for (String identifier : section.getKeys(false)) {
				//enchantments.put(Enchantment.getByKey(NamespacedKey.minecraft(identifier.toUpperCase())), resource.getInt(path + ".Enchantments.Level"));
				enchantments.put(Enchantment.getByKey(NamespacedKey.minecraft(identifier)), resource.getInt(path + ".Enchantments.Level"));
			}
			
		}
		
	}
	
	public ItemStack toItemStack() {
		
		ItemStack item = new ItemStack(material != null ? material : Material.BEDROCK);
		ItemMeta meta = item.getItemMeta();
		
		item.setAmount(amount != 0 ? amount : 1);
		item.addUnsafeEnchantments(enchantments);
		
		meta.setDisplayName(Config.tr(name != null ? name : "&7Item"));
		meta.setLore(lore != null ? lore : new ArrayList<String>());
		
		item.setItemMeta(meta);
		
		if (item.getType() == XMaterial.LEATHER_HELMET.parseMaterial() ||
			item.getType() == XMaterial.LEATHER_CHESTPLATE.parseMaterial() ||
			item.getType() == XMaterial.LEATHER_LEGGINGS.parseMaterial() ||
			item.getType() == XMaterial.LEATHER_BOOTS.parseMaterial()) {
			
			if (color != null) {
				
				LeatherArmorMeta dyedMeta = (LeatherArmorMeta) item.getItemMeta();
				dyedMeta.setColor(color);
				item.setItemMeta(dyedMeta);
				
			}
			
		}
		
		if (item.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) {
			
			if (skull != null) {
				
				item = skull.convertToSkull(item);
				
			}
			
		}
		
		return item;
		
	}
	
	public String getName() { return name; }
	
	public List<String> getLore() { return lore; }

	public Material getType() { return material; }
	
	public int getAmount() { return amount; }
	
	public Color getColor() { return color; }
	
	public SkullItem getSkull() { return skull; } 
	
}
