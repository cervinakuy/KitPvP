package com.planetgallium.kitpvp.game;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.kit.Enchant;
import com.planetgallium.kitpvp.kit.Item;
import com.planetgallium.kitpvp.kit.Kit;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Materials;
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
		
		if (isKit(name)) {
			
			p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Exists").replace("%prefix%", resources.getMessages().getString("Messages.General.Prefix"))));
			return;
			
		}
		
		resources.addKit(name);
		
		Resource kitResource = resources.getKits(name);
		kitResource.set("Kit.Permission", "kp.kit." + name.toLowerCase());
		kitResource.set("Kit.Level", 0);
		kitResource.set("Kit.Cooldown", 0);
		kitResource.save();
		
		// ARMOR
		if (p.getInventory().getHelmet() != null) {
			
			saveItem(kitResource, p.getInventory().getHelmet(), "Inventory.Armor.Helmet", "&fHelmet");
			
		}
		
		if (p.getInventory().getChestplate() != null) {

			saveItem(kitResource, p.getInventory().getChestplate(), "Inventory.Armor.Chestplate", "&fChestplate");
			
		}
		
		if (p.getInventory().getLeggings() != null) {
			
			saveItem(kitResource, p.getInventory().getLeggings(), "Inventory.Armor.Leggings", "&fLeggings");
			
		}
		
		if (p.getInventory().getBoots() != null) {
			
			saveItem(kitResource, p.getInventory().getBoots(), "Inventory.Armor.Boots", "&fBoots");
			
		}
		
		// INVENTORY ITEMS
		for (int i = 0; i < 36; i++) {
			
			ItemStack item = p.getInventory().getItem(i);
			
			if (item != null) {
				
				String backupName = (item.getType() == Material.MUSHROOM_SOUP) ? Config.getS("Soups.Name") : "&7Item";
				saveItem(kitResource, p.getInventory().getItem(i), "Inventory.Items." + i, backupName);
				
			}
			
		}
		
		// POTION EFFECTS
		for (PotionEffect effect : p.getActivePotionEffects()) {
			
			resources.getKits(name).set("Potions." + effect.getType().getName() + ".Level", effect.getAmplifier() + 1);
			resources.getKits(name).set("Potions." + effect.getType().getName() + ".Duration", effect.getDuration() / 20);
			resources.getKits(name).save();
			
		}
		
		// DEFAULT ABILITY
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
		
	}
	
	public void giveKit(String name, Player p) {
		
		// GENERAL
		if (!isKit(name)) {
			
			p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Lost")));
			return;
			
		}
		
		if (!p.hasPermission(resources.getKits(name).getString("Kit.Permission"))) {
			
			p.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Permission")));
			return;
			
		}
		
		if (!(Game.getInstance().getArena().getLevels().getLevel(p.getUniqueId()) >= resources.getKits(name).getInt("Kit.Level"))) {
			
			p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Other.Needed").replace("%level%", String.valueOf(resources.getKits(name).getInt("Kit.Level")))));
			return;
			
		}
		
		Kit kit = new Kit();
		kit.setName(name);
		
		setKit(p.getName(), name);
		
		Resource kitResource = resources.getKits(kit.getName());
		
		// ARMOR
		if (kitResource.contains("Inventory.Armor")) {
			
			if (kitResource.contains("Inventory.Armor.Helmet.Item")) {
				if (kitResource.getString("Inventory.Armor.Helmet.Item").equals("SKULL_ITEM")) {
					ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
					SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
					skullMeta.setOwner(kitResource.getString("Inventory.Armor.Helmet.Skull"));
					skull.setItemMeta(skullMeta);
					kit.setHelmet(skull);
				} else {
					kit.setHelmet(new Item(Material.valueOf(kitResource.getString("Inventory.Armor.Helmet.Item").toUpperCase()), Toolkit.stringToColor(kit.getName(), "Inventory.Armor.Helmet.Dye"), kitResource.getString("Inventory.Armor.Helmet.Name"), kitResource.getStringList("Inventory.Armor.Helmet.Lore"), new Enchant(kit.getName(), "Inventory.Armor.Helmet"), 1));
				}
			}
			
			if (kitResource.contains("Inventory.Armor.Chestplate.Item")) {
				kit.setChestplate(new Item(Material.valueOf(kitResource.getString("Inventory.Armor.Chestplate.Item").toUpperCase()), Toolkit.stringToColor(kit.getName(), "Inventory.Armor.Chestplate.Dye"), kitResource.getString("Inventory.Armor.Chestplate.Name"), kitResource.getStringList("Inventory.Armor.Chestplate.Lore"), new Enchant(kit.getName(), "Inventory.Armor.Chestplate"), 1));
			}
			
			if (kitResource.contains("Inventory.Armor.Leggings.Item")) {
				kit.setLeggings(new Item(Material.valueOf(kitResource.getString("Inventory.Armor.Leggings.Item").toUpperCase()), Toolkit.stringToColor(kit.getName(), "Inventory.Armor.Leggings.Dye"), kitResource.getString("Inventory.Armor.Leggings.Name"), kitResource.getStringList("Inventory.Armor.Leggings.Lore"), new Enchant(kit.getName(), "Inventory.Armor.Leggings"), 1));
			}
			
			if (kitResource.contains("Inventory.Armor.Boots.Item")) {
				kit.setBoots(new Item(Material.valueOf(kitResource.getString("Inventory.Armor.Boots.Item").toUpperCase()), Toolkit.stringToColor(kit.getName(), "Inventory.Armor.Boots.Dye"), kitResource.getString("Inventory.Armor.Boots.Name"), kitResource.getStringList("Inventory.Armor.Boots.Lore"), new Enchant(kit.getName(), "Inventory.Armor.Boots"), 1));
			}
			
		}
		
		// INVENTORY ITEMS
		ConfigurationSection items = kitResource.getConfigurationSection("Inventory.Items");
		
		for (String identifier : items.getKeys(false)) {
			
			if (!identifier.equals("Fill")) {
				
				if (kitResource.getString("Inventory.Items." + identifier + ".Item").equals("SKULL_ITEM")) {
					ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
					SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
					skullMeta.setOwner(kitResource.getString("Inventory.Items." + identifier + ".Skull"));
					skull.setItemMeta(skullMeta);
					kit.addItem(skull, Integer.valueOf(identifier));
				} else {
					kit.addItem(new Item(Material.valueOf(kitResource.getString("Inventory.Items." + identifier + ".Item").toUpperCase()), kitResource.getString("Inventory.Items." + identifier + ".Name"), kitResource.getStringList("Inventory.Items." + identifier + ".Lore"), new Enchant(kit.getName(), "Inventory.Items." + identifier), kitResource.getInt("Inventory.Items." + identifier + ".Amount")), Integer.valueOf(identifier));
				}
				
			}
			
		}
		
		// POTION EFFECTS
		if (kitResource.contains("Potions")) {
			
			ConfigurationSection potions = kitResource.getConfigurationSection("Potions");
			
			for (String identifier : potions.getKeys(false)) {
				
				kit.addEffect(new PotionEffect(PotionEffectType.getByName(identifier.toUpperCase()), kitResource.getInt("Potions." + identifier + ".Duration"), kitResource.getInt("Potions." + identifier + ".Level") - 1));
				
			}
			
		}
		
		// FILL ITEM
		if (kitResource.contains("Inventory.Items.Fill")) {
			
			kit.setFill(new Item(Material.valueOf(kitResource.getString("Inventory.Items.Fill.Item").toUpperCase()), kitResource.getString("Inventory.Items.Fill.Name"), kitResource.getStringList("Inventory.Items.Fill.Lore"), new Enchant(kit.getName(), "Inventory.Items.Fill"), kitResource.getInt("Inventory.Items.Fill.Amount")));
			
		}
		
		kit.applyKit(p);
		
	}
	
	private void saveItem(Resource resource, ItemStack item, String path, String backupName) {
		
		if (item != null) {
			
			ItemMeta meta = item.getItemMeta();
			String name = backupName;
			List<String> lore = null;
			
			if (meta != null) {
				
				if (meta.hasDisplayName()) {
					name = meta.getDisplayName().replace("§", "&");
				}
				
				if (meta.hasLore()) {
					lore = new ArrayList<String>(meta.getLore());
					lore.replaceAll(s -> ChatColor.translateAlternateColorCodes('&', s));
				}
				
			}
			
			resource.set(path + ".Name", name);
			resource.set(path + ".Item", item.getType().toString());
			resource.set(path + ".Lore", lore); // setting this to null may cause problems look into this later
			resource.set(path + ".Amount", item.getAmount());
			resource.save();
			
			if (meta instanceof LeatherArmorMeta) {
				
				LeatherArmorMeta dyedMeta = (LeatherArmorMeta) meta;
				resource.set(path + ".Dye.Red", dyedMeta.getColor().getRed());
				resource.set(path + ".Dye.Green", dyedMeta.getColor().getGreen());
				resource.set(path + ".Dye.Blue", dyedMeta.getColor().getBlue());
				resource.save();
				
			} else if (meta instanceof SkullMeta) {
				
				SkullMeta skullMeta = (SkullMeta) meta;
				resource.set(path + ".Skull", skullMeta.getOwner());
				resource.save();
				
			}
			
			if (item.getEnchantments().size() > 0) {
				
				for (Enchantment enchant : item.getEnchantments().keySet()) {
					resource.set(path + ".Enchantments." + enchant.getName().toUpperCase(), item.getEnchantmentLevel(enchant));
					resource.save();
				}
				
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
