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
	public ItemStack setDamaged(ItemStack original) {

		ItemMeta meta = original.getItemMeta();
		
		if (Toolkit.versionToNumber() < 113) {
			
			original.setDurability((short) damageAmount);
			
		} else if (Toolkit.versionToNumber() >= 113) {
			
			if (meta instanceof Damageable) {
				
				((Damageable) meta).setDamage(damageAmount);
				original.setItemMeta(meta);
				
			}
			
		}
		
		return original;
		
	}
	
	public int getDamageAmount() { return damageAmount; }
	
}
