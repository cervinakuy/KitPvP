package com.planetgallium.kitpvp.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.planetgallium.kitpvp.Game;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Resource extends YamlConfiguration {
	
	private String name;
	private final File file;

	public Resource(Plugin plugin, String path) {

		this.file = new File(plugin.getDataFolder() + "/" + Paths.get(path));
		this.name = Paths.get(path).getFileName().toString();

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

	}
	
	public void load() {
		
		try {
			super.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
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
        String string = super.getString(path);

		if (string != null) {
			string = ChatColor.translateAlternateColorCodes('&',
					string.replace("%prefix%", Game.getPrefix() == null ? "" : Game.getPrefix()));
		}

        return string;
    }

    @Override
	public List<String> getStringList(String path) {
		List<String> originalList = super.getStringList(path);

		if (originalList != null) {
			List<String> colorizedList = new ArrayList<>();
			for (String line : originalList) {
				colorizedList.add(ChatColor.translateAlternateColorCodes('&', line));
			}
			return colorizedList;
		}

		return originalList;

	}
	
	public String getName() { return name; }
	
	public File getFile() { return file; }
	
}
