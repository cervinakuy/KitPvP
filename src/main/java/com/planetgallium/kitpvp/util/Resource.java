package com.planetgallium.kitpvp.util;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class Resource extends YamlConfiguration {

    private String name;
    private final File file;

    public Resource(Plugin plugin, String name) {

        this.name = name;

        file = new File(plugin.getDataFolder(), name);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            plugin.saveResource(name, true);
        }

    }

    public Resource(Plugin plugin, String resourcePath, String name) {

        this.name = name;

        file = new File(plugin.getDataFolder().getAbsolutePath() + "/kits", name);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            plugin.saveResource(resourcePath, true);
        }

    }

    public Resource(Plugin plugin, String resourcePath, String name, boolean custom) {

        this.name = name;

        file = new File(plugin.getDataFolder().getAbsolutePath() + "/kits", name);

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
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

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

}
