package com.planetgallium.kitpvp.menu;

import java.util.ArrayList;
import java.util.List;

import com.cryptomorin.xseries.XMaterial;
import com.planetgallium.kitpvp.api.Kit;
import com.planetgallium.kitpvp.util.CacheManager;
import org.bukkit.entity.Player;

import com.planetgallium.kitpvp.util.Menu;
import org.bukkit.potion.PotionEffect;

public class PreviewMenu {

	private Menu create(Kit kit) {

		Menu previewMenu = new Menu("Previewing: " + kit.getName(), new PreviewHolder(), 54);

		//			ARMOR			//

		if (kit.getHelmet() != null)
			previewMenu.setItem(kit.getHelmet(), 0);

		if (kit.getChestplate() != null)
			previewMenu.setItem(kit.getChestplate(), 1);

		if (kit.getLeggings() != null)
			previewMenu.setItem(kit.getLeggings(), 2);

		if (kit.getBoots() != null)
			previewMenu.setItem(kit.getBoots(), 3);

		//		POTION EFFECTS		//

		List<String> effectsLore = new ArrayList<>();

		for (PotionEffect effect : kit.getEffects()) {
			String type = effect.getType().getName();
			int amplifierNonZeroBased = effect.getAmplifier() + 1;
			int durationSeconds = effect.getDuration() / 20;

			effectsLore.add("&7- " + type + " " + amplifierNonZeroBased + " (" + (durationSeconds > 10000 ? "Infinite" : (durationSeconds + "s")) + ")");
		}

		if (kit.getEffects().size() == 0) {
			effectsLore.add("&7None");
		}

		previewMenu.addItem("&a&lPotion Effects", XMaterial.BREWING_STAND.parseMaterial(), effectsLore, 4);

		//			HOTBAR			//

		for (int i = 0; i < 9; i++) {
			if (kit.getInventory().containsKey(i)) {
				previewMenu.setItem(kit.getInventory().get(i), (45 + i));
			}
		}

		//			ITEMS			//

		for (int i = 9; i < 36; i++) {
			if (kit.getInventory().containsKey(i)) {
				previewMenu.setItem(kit.getInventory().get(i), (9 + i));
			}
		}

		//			FILL			//

		if (kit.getFill() != null) {
			for (int i = 18; i < 54; i++) {
				if (previewMenu.getSlot(i) == null) {
					previewMenu.setItem(kit.getFill(), i);
				}
			}
		}

		previewMenu.addItem("&cBack to Kits", XMaterial.ARROW.parseMaterial(), new ArrayList<String>(), 8);

		CacheManager.getPreviewMenuCache().put(kit.getName(), previewMenu);

		return previewMenu;

	}

	public void open(Player p, Kit kit) {

		Menu previewMenu = CacheManager.getPreviewMenuCache().containsKey(kit.getName()) ?
				CacheManager.getPreviewMenuCache().get(kit.getName()) : create(kit);

		previewMenu.openMenu(p);

	}
	
}
