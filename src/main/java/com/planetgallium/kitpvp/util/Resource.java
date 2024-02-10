package com.planetgallium.kitpvp.util;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.planetgallium.kitpvp.Game;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Resource extends YamlConfiguration {

	private final String name;
	private final File file;
	private final List<String> copyDefaultExemptions;

	private final Plugin plugin;
	private final String path;

	public Resource(Plugin plugin, String path) {
		this.plugin = plugin;
		this.path = path;

		this.file = new File(plugin.getDataFolder() + "/" + Paths.get(path));
		this.name = Paths.get(path).getFileName().toString();
		this.copyDefaultExemptions = new ArrayList<>();
	}

	public void load() {
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		if (!file.exists()) {
			if (plugin.getResource(path) == null) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				plugin.saveResource(path, true);
			}
		}

		try {
			super.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void copyDefaults() {
		Reader defaultConfigSearchResult = null;

		if (plugin.getResource(path) != null) {
			try {
				defaultConfigSearchResult = new InputStreamReader(plugin.getResource(path), "UTF8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		if (defaultConfigSearchResult != null) {
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigSearchResult);

			for (String valuePath : defaultConfig.getValues(true).keySet()) {
				if (!contains(valuePath)) {
					if (!Toolkit.containsAnyThatStartWith(copyDefaultExemptions, valuePath)) {
						this.set(valuePath, defaultConfig.get(valuePath));
					}
				}
			}
			save();
		}
	}

	public void addCopyDefaultExemption(String path) {
		copyDefaultExemptions.add(path);
	}
	
	public void save() {
		try {
			super.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getString(String path) {
		Toolkit.printToConsole("%prefix% &cNote: Do not use legacy resource.getString, use resource.fetchString instead");
		return super.getString(path);
	}

    public String fetchString(String path) {
		String string = super.getString(path);

		if (string != null) {
			string = ChatColor.translateAlternateColorCodes('&',
					string.replace("%prefix%", Game.getPrefix() == null ? "" : Game.getPrefix()));
		} else {
			string = "String not found";
			Toolkit.printToConsole(String.format("&7[&b&lKIT-PVP&7] &cString with path %s was not found.", path));
		}

		return string;
	}

    @Override
	public List<String> getStringList(String path) {
		return Toolkit.colorizeList(super.getStringList(path));
	}
	
	public String getName() { return name; }
	
	public File getFile() { return file; }
	
}
