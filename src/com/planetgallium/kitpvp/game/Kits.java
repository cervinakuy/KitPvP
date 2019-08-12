package com.planetgallium.kitpvp.game;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.PlayerSelectKitEvent;
import com.planetgallium.kitpvp.item.EffectItem;
import com.planetgallium.kitpvp.item.KitItem;
import com.planetgallium.kitpvp.kit.Kit;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.XMaterial;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;

public class Kits {

	private Game plugin;
	private Resources resources;
	
	public Kits(Game plugin, Resources resources) {
		this.plugin = plugin;
		this.resources = resources;
	}
	
	private Map<String, String> kits = new HashMap<String, String>();
	
	public void createKit(String name, Player p) {
		
		if (!isKit(name)) {
			
			resources.addKit(name);
			
			Resource kitResource = resources.getKits(name);
			kitResource.set("Kit.Permission", "kp.kit." + name.toLowerCase());
			kitResource.set("Kit.Level", 0);
			kitResource.set("Kit.Cooldown", 0);
			kitResource.save();
			
			// Armor
			
			if (p.getInventory().getHelmet() != null) {
				
				saveItem(kitResource, name, "Inventory.Armor.Helmet", p.getInventory().getHelmet(), "&fHelmet");
				
			}
			
			if (p.getInventory().getChestplate() != null) {
				
				saveItem(kitResource, name, "Inventory.Armor.Chestplate", p.getInventory().getChestplate(), "&fChestplate");
				
			}
			
			if (p.getInventory().getLeggings() != null) {
				
				saveItem(kitResource, name, "Inventory.Armor.Leggings", p.getInventory().getLeggings(), "&fLeggings");
				
			}
			
			if (p.getInventory().getBoots() != null) {
				
				saveItem(kitResource, name, "Inventory.Armor.Boots", p.getInventory().getBoots(), "&fBoots");
				
			}
			
			// Inventory Items
			
			for (int i = 0; i < 36; i++) {
				
				ItemStack item = p.getInventory().getItem(i);
				
				if (item != null) {
					
					String backupName = (item.getType() == XMaterial.MUSHROOM_STEW.parseMaterial()) ? Config.getS("Soups.Name") : "&7Item";
					saveItem(kitResource, name, "Inventory.Items." + i, p.getInventory().getItem(i), backupName);
					
				}
				
			}
			
			// Potion Effects
			
			for (PotionEffect effect : p.getActivePotionEffects()) {
				
				resources.getKits(name).set("Potions." + effect.getType().getName() + ".Level", effect.getAmplifier() + 1);
				resources.getKits(name).set("Potions." + effect.getType().getName() + ".Duration", effect.getDuration() / 20);
				resources.getKits(name).save();
				
			}
			
			// Default Ability
			resources.getKits(name).set("Ability.Activator.Name", "&aDefault Ability &7(Must be modified in kit file)");
			resources.getKits(name).set("Ability.Activator.Item", "BEDROCK");
			resources.getKits(name).set("Ability.Message.Message", "%prefix% &7You have used your ability.");
			resources.getKits(name).set("Ability.Message.Enabled", true);
			resources.getKits(name).set("Ability.Sound.Sound", "FIZZ");
			resources.getKits(name).set("Ability.Sound.Pitch", 1);
			resources.getKits(name).set("Ability.Sound.Enabled", true);
			resources.getKits(name).set("Ability.Potions.SPEED.Level", 1);
			resources.getKits(name).set("Ability.Potions.SPEED.Duration", 10);
			resources.getKits(name).set("Ability.Commands.Commands", new String[]{"console: This command is run from the console, you can use %player%", "player: This command is run from the player, you can use %player%"});
			resources.getKits(name).set("Ability.Commands.Enabled", false);
			resources.getKits(name).save();
			
			p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Commands.Create").replace("%kit%", name)));
			
		} else {
			
			p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Exists").replace("%prefix%", resources.getMessages().getString("Messages.General.Prefix"))));
			
		}
		
	}
	
	public void giveKit(String name, Player p) {
		
		if (isKit(name)) {
			
			if (p.hasPermission(resources.getKits(name).getString("Kit.Permission"))) {
				
				if (Game.getInstance().getArena().getLevels().getLevel(p.getUniqueId()) >= resources.getKits(name).getInt("Kit.Level")) {
					
					Kit kit = new Kit();
					kit.setName(name);
					
					Resource kitResource = resources.getKits(kit.getName());
					
					if (kitResource.contains("Inventory.Armor")) {
						
						if (kitResource.contains("Inventory.Armor.Helmet")) {
							
							kit.setHelmet(new KitItem(kitResource, kit.getName(), "Inventory.Armor.Helmet"));
							
						}
						
						if (kitResource.contains("Inventory.Armor.Chestplate")) {
							
							kit.setChestplate(new KitItem(kitResource, kit.getName(), "Inventory.Armor.Chestplate"));
							
						}
						
						if (kitResource.contains("Inventory.Armor.Leggings")) {
							
							kit.setLeggings(new KitItem(kitResource, kit.getName(), "Inventory.Armor.Leggings"));
							
						}
						
						if (kitResource.contains("Inventory.Armor.Boots")) {
							
							kit.setBoots(new KitItem(kitResource, kit.getName(), "Inventory.Armor.Boots"));
							
						}
						
					}
					
					ConfigurationSection items = kitResource.getConfigurationSection("Inventory.Items");
					
					for (String identifier : items.getKeys(false)) {
						
						if (!identifier.equals("Fill")) {

							kit.addItem(new KitItem(kitResource, kit.getName(), "Inventory.Items." + identifier), Integer.valueOf(identifier));
							
						} else {
							
							kit.setFill(new KitItem(kitResource, kit.getName(), "Inventory.Items.Fill"));
							
						}
						
					}
					
					if (kitResource.contains("Potions")) {
						
						ConfigurationSection potions = kitResource.getConfigurationSection("Potions");
						
						for (String identifier : potions.getKeys(false)) {
							
							kit.addEffect(new EffectItem(kitResource, identifier));
							
						}
						
					}
					
					kit.applyKit(p);
					setKit(p.getName(), name);
					
					Bukkit.getPluginManager().callEvent(new PlayerSelectKitEvent(p, kit.getName()));
					
				} else {
					
					p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Other.Needed").replace("%level%", String.valueOf(resources.getKits(name).getInt("Kit.Level")))));
					
				}
				
			} else {
				
				p.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Permission")));
				
			}
			
		} else {
			
			p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Lost")));
			
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public void saveItem(Resource resource, String kit, String path, ItemStack item, String backupName) {
		
		ItemMeta meta = item.getItemMeta();
		
		resource.set(path + ".Name", meta.hasDisplayName() ? meta.getDisplayName() : backupName);
		resource.set(path + ".Lore", meta.getLore());
		resource.set(path + ".Item", item.getType().toString());
		resource.set(path + ".Amount", item.getAmount());
		resource.save();
		
		if (item.getType() == XMaterial.LEATHER_HELMET.parseMaterial() ||
			item.getType() == XMaterial.LEATHER_CHESTPLATE.parseMaterial() ||
			item.getType() == XMaterial.LEATHER_LEGGINGS.parseMaterial() ||
			item.getType() == XMaterial.LEATHER_BOOTS.parseMaterial()) {
			
			LeatherArmorMeta dyedMeta = (LeatherArmorMeta) meta;
			
			resource.set(path + ".Dye.Red", dyedMeta.getColor().getRed());
			resource.set(path + ".Dye.Green", dyedMeta.getColor().getGreen());
			resource.set(path + ".Dye.Blue", dyedMeta.getColor().getBlue());
			resource.save();
			
		} else if (item.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) {
			
			SkullMeta skullMeta = (SkullMeta) meta;
			
			if (Toolkit.versionToNumber() < 13) {
				
				resource.set(path + ".Skull", skullMeta.getOwner());
				resource.save();
				
			} else if (Toolkit.versionToNumber() >= 13) {
				
				resource.set(path + ".Skull", skullMeta.getOwningPlayer().getName());
				resource.save();
				
			}
			
		} else if (item.getType() == XMaterial.POTION.parseMaterial()) {
				
			PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
			resource.set(path + ".Potion.Splash", false);
			resource.set(path + ".Potion.Type", potionMeta.getBasePotionData().getType().toString());
			resource.set(path + ".Potion.Level", potionMeta.getCustomEffects().get(0).getAmplifier());
			resource.set(path + ".Potion.Duration", potionMeta.getCustomEffects().get(0).getDuration());
			resource.save();
			
//				Potion potion = Potion.fromItemStack(item);
//				resource.set(path + ".Potion.Splash", potion.isSplash());
//				resource.set(path + ".Potion.Type", potion.getType().toString());
//				resource.set(path + ".Potion.Level", potion.getLevel());
//				resource.set(path + ".Potion.Duration", potion);
//				resource.save();
			
		}
		
		if (item.getEnchantments().size() > 0) {
			
			for (Enchantment enchantment : item.getEnchantments().keySet()) {
			
				resource.set(path + ".Enchantments." + enchantment.getKey() + ".Level", item.getEnchantments().get(enchantment));
				resource.save();
				
			}
			
		}
		
	}
	
	private void setKit(String username, String kit) {
		
		if (!kits.containsKey(username)) {
			
			kits.put(username, kit);
			
		}
		
	}
	
	public void clearKit(String username) {
		
		if (hasKit(username)) {
			
			kits.remove(username);
			
		}
		
	}
	
	public String getKit(String username) {

		if (hasKit(username)) {
			
			return kits.get(username);
			
		}
		
		return "None";
		
	}
	
	public boolean hasKit(String username) {
		
		return kits.containsKey(username);
		
	}
	
	public boolean isKit(String kit) {
		
		return getList().contains(kit + ".yml");
		
	}
	
	public String getPath() {
		
		return plugin.getDataFolder().getAbsolutePath() + "/kits";
		
	}
	
	public List<String> getList() {
		
		File folder = new File(plugin.getDataFolder().getAbsolutePath() + "/kits");
		
		return new ArrayList<String>(Arrays.asList(folder.list()));
		
	}
	
}
