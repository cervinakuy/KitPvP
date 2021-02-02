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

	private Resource config;
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
		this.kits = new HashMap<>();

		if (!plugin.getDataFolder().exists()) {
			kits.put("Fighter.yml", new Resource(plugin, "kits/Fighter.yml"));
			kits.put("Archer.yml", new Resource(plugin, "kits/Archer.yml"));
			kits.put("Tank.yml", new Resource(plugin, "kits/Tank.yml"));
			kits.put("Soldier.yml", new Resource(plugin, "kits/Soldier.yml"));
			kits.put("Bomber.yml", new Resource(plugin, "kits/Bomber.yml"));
			kits.put("Kangaroo.yml", new Resource(plugin, "kits/Kangaroo.yml"));
			kits.put("Warper.yml", new Resource(plugin, "kits/Warper.yml"));
			kits.put("Witch.yml", new Resource(plugin, "kits/Witch.yml"));
			kits.put("Ninja.yml", new Resource(plugin, "kits/Ninja.yml"));
			kits.put("Thunderbolt.yml", new Resource(plugin, "kits/Thunderbolt.yml"));
			kits.put("Vampire.yml", new Resource(plugin, "kits/Vampire.yml"));
			kits.put("Rhino.yml", new Resource(plugin, "kits/Rhino.yml"));
			kits.put("Example.yml", new Resource(plugin, "kits/Example.yml"));
			kits.put("Trickster.yml", new Resource(plugin, "kits/Trickster.yml"));
		}

		config = new Resource(plugin, "config.yml");
		abilities = new Resource(plugin, "abilities.yml");
		killstreaks = new Resource(plugin, "killstreaks.yml");
		levels = new Resource(plugin, "levels.yml");
		menu = new Resource(plugin, "menu.yml");
		messages = new Resource(plugin, "messages.yml");
		scoreboard = new Resource(plugin, "scoreboard.yml");
		stats = new Resource(plugin, "stats.yml");
		signs = new Resource(plugin, "signs.yml");

		for (String fileName : this.getKitList()) {
			kits.put(fileName, new Resource(plugin, "kits/" + fileName));
		}
		
	}
	
	public void load() {

		config.load();
		abilities.load();
		killstreaks.load();
		levels.load();
		menu.load();
		messages.load();
		scoreboard.load();
		stats.load();
		signs.load();

		messages.addCopyDefaultExemption("Messages.Stats.Message");
		messages.copyDefaults();

		menu.addCopyDefaultExemption("Menu.Items");
		menu.copyDefaults();

		levels.addCopyDefaultExemption("Levels.Levels.10.Commands");
		levels.copyDefaults();

		scoreboard.addCopyDefaultExemption("Scoreboard.Lines");
		scoreboard.copyDefaults();

		config.addCopyDefaultExemption("Items.Kits");
		config.addCopyDefaultExemption("Items.Leave");
		config.copyDefaults();

		abilities.copyDefaults();
		signs.copyDefaults();
		
		for (String fileName : kits.keySet()) {
			kits.get(fileName).load();
		}

		// load new kits that have been added through file system (when doing /kp reload)
		for (String fileName : getKitList()) {
			if (!kits.containsKey(fileName)) {
				kits.put(fileName, new Resource(plugin, "kits/" + fileName));
			}
		}
		
	}
	
	public void reload() {
		
		load();
		
	}
	
	public void save() {

		config.save();
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

	public void addResource(String fileName, Resource resource) {

		kits.put(fileName, resource);
		kits.get(fileName).load();

	}
	
	public void removeResource(String fileName) {

		kits.get(fileName).getFile().delete();
		kits.remove(fileName);
		
	}

	public Resource getKit(String kitName) {

		if (kits.containsKey(kitName + ".yml")) {
			return kits.get(kitName + ".yml");
		}

		return null;

	}

	private List<String> getKitList() {

		File folder = new File(plugin.getDataFolder().getAbsolutePath() + "/kits");

		if (folder.exists()) {
			return new ArrayList<String>(Arrays.asList(folder.list()));
		}
		return new ArrayList<String>();

	}

	public Resource getConfig() { return config; }

	public Resource getAbilities() { return abilities; }
	
	public Resource getKillStreaks() { return killstreaks; }
	
	public Resource getLevels() { return levels; }
	
	public Resource getMenu() { return menu; }
	
	public Resource getMessages() { return messages; }
	
	public Resource getScoreboard() { return scoreboard; }
	
	public Resource getStats() { return stats; }
	
	public Resource getSigns() { return signs; }
	
}
