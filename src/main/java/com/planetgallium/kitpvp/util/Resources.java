package com.planetgallium.kitpvp.util;

import java.io.File;
import java.util.*;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.Ability;

public class Resources {

	private final Game plugin;
	private final Map<String, Resource> kitToResource;
	private final Map<String, Resource> abilityToResource;

	private final Resource config, abilities, killstreaks,
							levels, menu, messages, scoreboard, signs;
	
	public Resources(Game plugin) {
		this.plugin = plugin;
		this.kitToResource = new HashMap<>();
		this.abilityToResource = new HashMap<>();

		Toolkit.printToConsole("&7[&b&lKIT-PVP&7] &7Loading configuration files...");
		this.config = new Resource(plugin, "config.yml");
		this.abilities = new Resource(plugin, "abilities.yml");
		this.killstreaks = new Resource(plugin, "killstreaks.yml");
		this.levels = new Resource(plugin, "levels.yml");
		this.menu = new Resource(plugin, "menu.yml");
		this.messages = new Resource(plugin, "messages.yml");
		this.scoreboard = new Resource(plugin, "scoreboard.yml");
		this.signs = new Resource(plugin, "signs.yml");

		if (!plugin.getDataFolder().exists()) {
			kitToResource.put("Fighter.yml", new Resource(plugin, "kits/Fighter.yml"));
			kitToResource.put("Archer.yml", new Resource(plugin, "kits/Archer.yml"));
			kitToResource.put("Tank.yml", new Resource(plugin, "kits/Tank.yml"));
			kitToResource.put("Soldier.yml", new Resource(plugin, "kits/Soldier.yml"));
			kitToResource.put("Bomber.yml", new Resource(plugin, "kits/Bomber.yml"));
			kitToResource.put("Kangaroo.yml", new Resource(plugin, "kits/Kangaroo.yml"));
			kitToResource.put("Warper.yml", new Resource(plugin, "kits/Warper.yml"));
			kitToResource.put("Witch.yml", new Resource(plugin, "kits/Witch.yml"));
			kitToResource.put("Ninja.yml", new Resource(plugin, "kits/Ninja.yml"));
			kitToResource.put("Thunderbolt.yml", new Resource(plugin, "kits/Thunderbolt.yml"));
			kitToResource.put("Vampire.yml", new Resource(plugin, "kits/Vampire.yml"));
			kitToResource.put("Rhino.yml", new Resource(plugin, "kits/Rhino.yml"));
			kitToResource.put("Example.yml", new Resource(plugin, "kits/Example.yml"));
			kitToResource.put("Trickster.yml", new Resource(plugin, "kits/Trickster.yml"));

			abilityToResource.put("HealthPack.yml", new Resource(plugin, "abilities/HealthPack.yml"));
			abilityToResource.put("ExampleAbility.yml", new Resource(plugin, "abilities/ExampleAbility.yml"));
			abilityToResource.put("ExampleAbility2.yml", new Resource(plugin, "abilities/ExampleAbility2.yml"));
			abilityToResource.put("SpeedBoost.yml", new Resource(plugin, "abilities/SpeedBoost.yml"));
			abilityToResource.put("Stampede.yml", new Resource(plugin, "abilities/Stampede.yml"));
		}

		Toolkit.printToConsole("&7[&b&lKIT-PVP&7] &7Loading kit files...");
		load();
	}
	
	public void load() {
		config.load();
		abilities.load();
		killstreaks.load();
		levels.load();
		menu.load();
		messages.load();
		scoreboard.load();
		signs.load();

		messages.addCopyDefaultExemption("Messages.Stats.Message");
		messages.copyDefaults();

		menu.addCopyDefaultExemption("Menu.Items");
		menu.copyDefaults();

		levels.addCopyDefaultExemption("Levels.Levels.10.Commands");
		levels.addCopyDefaultExemption("Levels.Levels.10.Experience-To-Level-Up");
		levels.copyDefaults();

		scoreboard.addCopyDefaultExemption("Scoreboard.Lines");
		scoreboard.copyDefaults();

		config.addCopyDefaultExemption("Items.Kits.Commands");
		config.addCopyDefaultExemption("Items.Leave.Commands");
		config.copyDefaults();

		abilities.copyDefaults();
		signs.copyDefaults();

		// load new kits & abilities that have been added through file system (when doing /kp reload)
		for (String fileName : getPluginDirectoryFiles("kits", true)) {
			if (!kitToResource.containsKey(fileName) && !fileName.startsWith(".")) {
				kitToResource.put(fileName, new Resource(plugin, "kits/" + fileName));
			}
		}

		for (String fileName : getPluginDirectoryFiles("abilities", true)) {
			if (!abilityToResource.containsKey(fileName) && !fileName.startsWith(".")) {
				abilityToResource.put(fileName, new Resource(plugin, "abilities/" + fileName));
			}
		}

		// Reload all kitName.yml, abilityName.yml
		kitToResource.values().forEach(Resource::load);
		abilityToResource.values().forEach(Resource::load);
	}
	
	public void reload() {
		load();
	}

	public void addResource(String fileName, Resource resource) {
		kitToResource.put(fileName, resource);
		kitToResource.get(fileName).load();
	}
	
	public void removeResource(String fileName) {
		kitToResource.get(fileName).getFile().delete();
		kitToResource.remove(fileName);
	}

	public void addAbilityResource(Ability ability) {
		String abilityName = ability.getName();
		Resource abilityResource = new Resource(plugin, "abilities/" + abilityName + ".yml");
		ability.toResource(abilityResource);

		abilityToResource.put(abilityName, abilityResource);
		abilityToResource.get(abilityName).load();
	}

	public Resource getKit(String kitName) {
		if (kitToResource.containsKey(kitName + ".yml")) {
			return kitToResource.get(kitName + ".yml");
		}
		return null;
	}

	public List<String> getPluginDirectoryFiles(String directoryName, boolean withFileEndings) {
		File folder = new File(plugin.getDataFolder().getAbsolutePath() + "/" + directoryName);
		List<String> fileList = new ArrayList<>();

		if (folder.exists() && folder.list() != null) {
			for (String fileName : folder.list()) {
				fileList.add(withFileEndings ? fileName : fileName.split(".yml")[0]);
			}
		}
		return fileList;
	}

	public Resource getConfig() { return config; }

	public Resource getAbilities() { return abilities; }
	
	public Resource getKillStreaks() { return killstreaks; }
	
	public Resource getLevels() { return levels; }
	
	public Resource getMenu() { return menu; }
	
	public Resource getMessages() { return messages; }
	
	public Resource getScoreboard() { return scoreboard; }
	
	public Resource getSigns() { return signs; }

	public Collection<Resource> getAbilityResources() { return abilityToResource.values(); }
	
}
