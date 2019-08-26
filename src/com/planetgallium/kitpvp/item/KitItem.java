package com.planetgallium.kitpvp.item;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.Material;
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
	private Material material;
	private int amount;
	private Color color;
	private SkullItem skull;
	private PotionItem potion;
	private EnchantedItem enchant;
	private DamagedItem damaged;
	
	public KitItem(Resource resource, String kit, String path) {
		
		if (resource.contains(path + ".Name")) {
			this.name = resource.getString(path + ".Name");
		}
		
		if (resource.contains(path + ".Lore")) {
			this.lore = new ArrayList<String>(resource.getStringList(path + ".Lore"));
		}
		
		if (resource.contains(path + ".Item")) {
			this.material = XMaterial.matchXMaterial(resource.getString(path + ".Item")).parseMaterial();
		}
		
		if (resource.contains(path + ".Amount")) {
			this.amount = resource.getInt(path + ".Amount");
		}
		
		if (resource.contains(path + ".Durability")) {
			this.damaged = new DamagedItem(resource.getInt(path + ".Durability"));
		}
		
		if (resource.contains(path + ".Dye")) {
			this.color = Toolkit.stringToColor(kit, path + ".Dye");
		}
		
		if (resource.contains(path + ".Skull")) {
			this.skull = new SkullItem(resource.getString(path + ".Skull"));
		}
		
		if (resource.contains(path + ".Potion")) {
			this.potion = new PotionItem(resource, path + ".Potion");
		}
		
		if (resource.contains(path + ".Enchantments")) {
			this.enchant = new EnchantedItem(resource, path + ".Enchantments");
		}
		
	}
	
	public ItemStack toItemStack() {
		
		ItemStack item = new ItemStack(material != null ? material : XMaterial.BEDROCK.parseMaterial());
		ItemMeta meta = item.getItemMeta();
		
		item.setAmount(amount != 0 ? amount : 1);
		
		meta.setDisplayName(Config.tr(name != null ? name : "&7Item"));
		meta.setLore(lore != null ? lore : new ArrayList<String>());
		
		item.setItemMeta(meta);
		
		if (enchant != null) {
			
			item = enchant.convertToEnchantedItem(item);
			
		}
		
		if (damaged != null) {
			
			item = damaged.setDamaged(item);
			
		}
		
		if (item.getType() == XMaterial.LEATHER_HELMET.parseMaterial() ||
			item.getType() == XMaterial.LEATHER_CHESTPLATE.parseMaterial() ||
			item.getType() == XMaterial.LEATHER_LEGGINGS.parseMaterial() ||
			item.getType() == XMaterial.LEATHER_BOOTS.parseMaterial()) {
			
			if (color != null) {
				
				LeatherArmorMeta dyedMeta = (LeatherArmorMeta) item.getItemMeta();
				dyedMeta.setColor(color);
				item.setItemMeta(dyedMeta);
				
			}
			
		} else if (item.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) {
			
			if (skull != null) {
				
				item = skull.convertToSkull(item);
				
			}
			
		} else if (item.getType() == XMaterial.POTION.parseMaterial() || item.getType() == XMaterial.SPLASH_POTION.parseMaterial()) {
			
			if (potion != null) {
				
				item = potion.convertToPotion(item);
				
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
	
	public PotionItem getPotion() { return potion; }
	
	public EnchantedItem getEnchant() { return enchant; }
	
}
