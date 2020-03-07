package com.planetgallium.kitpvp.item;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import com.planetgallium.kitpvp.util.Toolkit;

public class DamagedItem {

	private int damageAmount;
	
	public DamagedItem(int damageAmount) {
		
		this.damageAmount = damageAmount;
		
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack setDamaged(ItemStack toConvert) {
		
		ItemStack newItem = toConvert;
		ItemMeta meta = toConvert.getItemMeta();
		
		if (Toolkit.versionToNumber() < 113) {
			
			newItem.setDurability((short) damageAmount);
			
		} else if (Toolkit.versionToNumber() >= 113) {
			
			if (meta instanceof Damageable) {
				
				((Damageable) meta).setDamage(damageAmount);
				newItem.setItemMeta(meta);
				
			}
			
		}
		
		return newItem;
		
	}
	
	public int getDamageAmount() { return damageAmount; }
	
}
