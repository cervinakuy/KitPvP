package com.planetgallium.kitpvp.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.planetgallium.kitpvp.Game;

public class Resources {

	private Game plugin;
	private Map<String, Resource> kits;
	
	private Resource abilities;
	private Resource killstreaks;
	private Resource levels;
	private Resource menu;
	private Resource messages;
	private Resource scoreboard;
	private Resource stats;
	private Resource signs;
	
	public Resources(Game plugin) {
		
		this.plugin = plugin;
		this.kits = new HashMap<String, Resource>();
		
		if (!plugin.getDataFolder().exists()) {
			kits.put("Fighter.yml", new Resource(plugin, "kits/Fighter.yml", "Fighter.yml"));
			kits.put("Archer.yml", new Resource(plugin, "kits/Archer.yml", "Archer.yml"));
			kits.put("Tank.yml", new Resource(plugin, "kits/Tank.yml", "Tank.yml"));
			kits.put("Soldier.yml", new Resource(plugin, "kits/Soldier.yml", "Soldier.yml"));
			kits.put("Bomber.yml", new Resource(plugin, "kits/Bomber.yml", "Bomber.yml"));
			kits.put("Kangaroo.yml", new Resource(plugin, "kits/Kangaroo.yml", "Kangaroo.yml"));
			kits.put("Warper.yml", new Resource(plugin, "kits/Warper.yml", "Warper.yml"));
			kits.put("Witch.yml", new Resource(plugin, "kits/Witch.yml", "Witch.yml"));
			kits.put("Ninja.yml", new Resource(plugin, "kits/Ninja.yml", "Ninja.yml"));
			kits.put("Thunderbolt.yml", new Resource(plugin, "kits/Thunderbolt.yml", "Thunderbolt.yml"));
			kits.put("Vampire.yml", new Resource(plugin, "kits/Vampire.yml", "Vampire.yml"));
			kits.put("Witch.yml", new Resource(plugin, "kits/Witch.yml", "Witch.yml"));
			kits.put("Rhino.yml", new Resource(plugin, "kits/Rhino.yml", "Rhino.yml"));
			kits.put("Example.yml", new Resource(plugin, "kits/Example.yml", "Example.yml"));
		}
		
		abilities = new Resource(plugin, "abilities.yml");
		killstreaks = new Resource(plugin, "killstreaks.yml");
		levels = new Resource(plugin, "levels.yml");
		menu = new Resource(plugin, "menu.yml");
		messages = new Resource(plugin, "messages.yml");
		scoreboard = new Resource(plugin, "scoreboard.yml");
		stats = new Resource(plugin, "stats.yml");
		signs = new Resource(plugin, "signs.yml");
		
		for (String kit : this.getKitList()) {
			kits.put(kit, new Resource(plugin, "kits/" + kit, kit));
		}
		
	}
	
	public void load() {
		
		abilities.load();
		killstreaks.load();
		levels.load();
		menu.load();
		messages.load();
		scoreboard.load();
		stats.load();
		signs.load();
		
		for (String key : kits.keySet()) {
			kits.get(key).load();
		}
		
	}
	
	public void reload() {
		
		load();
		
	}
	
	public void save() {
		
		abilities.save();
		killstreaks.save();
		levels.save();
		menu.save();
		messages.save();
		scoreboard.save();
		stats.save();
		signs.save();
		
		for (String key : kits.keySet()) {
			kits.get(key).save();
		}
		
	}
	
	public void addKit(String fileName) {
		
		kits.put(fileName + ".yml", new Resource(plugin, "kits/" + fileName, fileName + ".yml", true));
		kits.get(fileName + ".yml").load();
		
	}
	
	public void removeKit(String fileName) {
		
		kits.get(fileName + ".yml").getFile().delete();
		kits.remove(fileName + ".yml");
		
	}
	
	public Resource getKits(String kit) {
		
		return kits.get(kit + ".yml");
		
	}
	
	private List<String> getKitList() {
		
		File folder = new File(plugin.getDataFolder().getAbsolutePath() + "/kits");
		
		if (folder.exists()) {
			return new ArrayList<String>(Arrays.asList(folder.list()));
		}
		return new ArrayList<String>();
		
	}
	
	public Resource getAbilities() { return abilities; }
	
	public Resource getKillStreaks() { return killstreaks; }
	
	public Resource getLevels() { return levels; }
	
	public Resource getMenu() { return menu; }
	
	public Resource getMessages() { return messages; }
	
	public Resource getScoreboard() { return scoreboard; }
	
	public Resource getStats() { return stats; }
	
	public Resource getSigns() { return signs; }
	
}
