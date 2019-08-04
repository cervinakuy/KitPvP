package com.planetgallium.kitpvp.kit;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import com.planetgallium.kitpvp.util.Config;

public class Item {

	private LeatherArmorMeta dyedArmor;
	
	private ItemStack item;
	private ItemMeta meta;
	
	private Color color;
	private String skull;
	private String name;
	private List<String> lore;
	private Enchant enchantments;
	private int amount;
	
	public Item(Material material, String name, List<String> lore, Enchant enchantments, int amount) {
		
		this.item = new ItemStack(material);
		this.name = name;
		this.lore = lore;
		this.enchantments = enchantments;
		this.amount = amount;
		
		meta = item.getItemMeta();
		
		// ITEM NAME //
		if (name != null) {
			meta.setDisplayName(Config.tr(name));
		} else {
			// If the name given is null, then create a virtual ItemStack and get the default name
			name = new ItemStack(material).getItemMeta().getDisplayName();
		}
		
		// ITEM LORE //
		if (lore != null) {
			lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s));
			meta.setLore(lore);
		} else {
			meta.setLore(null);
		}
		
		// ITEM AMOUNT //
		if (amount > 0) {
			item.setAmount(amount);
		} else {
			item.setAmount(1);	
		}
		
		item.setItemMeta(meta);
		
		
		// ENCHANTMENTS
		if (enchantments.getEnchantments().size() > 0) {
			for (int i = 0; i < enchantments.getEnchantments().size(); i++) {
				item.addUnsafeEnchantment(Enchantment.getByName(enchantments.getEnchantments().get(i)), enchantments.getLevels().get(i));
			}
		}
		
	}
	
	public Item(Material material, Color color, String name, List<String> lore, Enchant enchantments, int amount) {
		
		this(material, name, lore, enchantments, amount);
		
		this.color = color;
		
		if (color != null) {
			
			if (material == Material.LEATHER_HELMET || material == Material.LEATHER_CHESTPLATE || material == Material.LEATHER_LEGGINGS || material == Material.LEATHER_BOOTS) {
				
				dyedArmor = (LeatherArmorMeta) item.getItemMeta();
				dyedArmor.setColor(color);
				item.setItemMeta(dyedArmor);
				
			}
			
		}
		
	}
	
	public ItemStack toItemStack() {
		
		return item;
		
	}
	
	public ItemStack getItem() { return item; }
	
	public ItemMeta getMeta() { return meta; }
	
	public String getSkull() { return skull; }
	
	public Color getColor() { return color; }
	
	public String getName() { return name; }
	
	public List<String> getLore() { return lore; }
	
	public Enchant getEnchantments() { return enchantments; }
	
	public int getAmount() { return amount; }
	
}
