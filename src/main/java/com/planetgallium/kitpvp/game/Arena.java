package com.planetgallium.kitpvp.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.XMaterial;

import me.clip.placeholderapi.PlaceholderAPI;

public class Arena {

	private Game plugin;

	private Resources resources;
	private FileConfiguration config;

	private Map<String, String> hitCache;
	
	private Stats stats;
	private Kits kits;
	private KillStreaks killstreaks;
	private Levels levels;
	private Cooldowns cooldowns;
	
	public Arena(Game plugin, Resources resources) {
		this.plugin = plugin;

		this.resources = resources;
		this.config = plugin.getConfig();

		this.hitCache = new HashMap<String, String>();
		
		this.stats = new Stats(plugin, resources);
		this.kits = new Kits(plugin, this);
		this.killstreaks = new KillStreaks(resources);
		this.levels = new Levels(this, resources);
		this.cooldowns = new Cooldowns(this, resources);
	}
	
	public void addPlayer(Player p, boolean toSpawn, boolean giveItems) {
		
		getKits().clearKit(p.getName());

		getKillStreaks().setStreak(p, 0);
		
		if (Config.getB("Arena.ClearPotionEffectsOnJoin")) {
			for (PotionEffect effect : p.getActivePotionEffects()) {
				p.removePotionEffect(effect.getType());
			}
		}
		
		if (p.getFireTicks() > 0) {
			p.setFireTicks(0);
		}

		p.setGameMode(GameMode.SURVIVAL);
		
		if (Config.getB("Arena.FancyDeath")) {
			p.setHealth(20.0);
		}
		
		p.setExp(0f);
		p.setFoodLevel(20);

		if (giveItems) {
			giveItems(p);
		}

		if (toSpawn) {
			toSpawn(p);
		}

		if (resources.getScoreboard().getBoolean("Scoreboard.General.Enabled")) {
			updateScoreboards(p, false);
		}
		
	}
	
	public void removePlayer(Player p) {
		
		for (PotionEffect effect : p.getActivePotionEffects()) {
			p.removePotionEffect(effect.getType());
		}
		
		getKits().clearKit(p.getName());

		if (Config.getB("Arena.ResetKillStreakOnLeave")) {
			getKillStreaks().resetStreak(p);
		}
		
//		if (Config.getB("Arena.FancyDeath")) {
//			p.setHealth(20.0); commenting this out fixes block glitching for some reason
//		}
		
		p.setExp(0f);
		p.setFoodLevel(20);
		
		if (resources.getScoreboard().getBoolean("Scoreboard.General.Enabled")) {
			updateScoreboards(p, true);
		}

		if (hitCache.containsKey(p.getName())) {
			hitCache.remove(p.getName());
		}
		
	}
	
	public void deletePlayer(Player p) {
		
		if (Config.getB("Arena.ClearInventoryOnLeave")) {
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
		}

		if (hitCache.containsKey(p.getName())) {
			hitCache.remove(p.getName());
		}
		
	}
	
	public void giveItems(Player p) {

		ConfigurationSection items = config.getConfigurationSection("Items");

		for (String identifier : items.getKeys(false)) {

			String itemPath = "Items." + identifier;

			if (config.getBoolean(itemPath + ".Enabled")) {

				ItemStack item = XMaterial.matchXMaterial(config.getString(itemPath + ".Item")).get().parseItem();
				ItemMeta meta = item.getItemMeta();

				meta.setDisplayName(Config.tr(config.getString(itemPath + ".Name")));
				item.setItemMeta(meta);

				p.getInventory().setItem(config.getInt(itemPath + ".Slot"), item);

			}

		}
		
	}
	
	public void toSpawn(Player p) {
		
		if (config.contains("Arenas.Spawn." + p.getWorld().getName())) {
			
			Location spawn = new Location(Bukkit.getWorld(config.getString("Arenas.Spawn." + p.getWorld().getName() + ".World")),
					config.getInt("Arenas.Spawn." + p.getWorld().getName() + ".X") + 0.5,
					config.getInt("Arenas.Spawn." + p.getWorld().getName() + ".Y"),
					config.getInt("Arenas.Spawn." + p.getWorld().getName() + ".Z") + 0.5,
					(float) config.getDouble("Arenas.Spawn." + p.getWorld().getName() + ".Yaw"),
					(float) config.getDouble("Arenas.Spawn." + p.getWorld().getName() + ".Pitch"));
			
			p.teleport(spawn);
			
		} else {
			
			p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Arena")));
			
		}
		
	}
	
	public void updateScoreboards(Player p, boolean hide) {
		
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Infoboard scoreboard = new Infoboard(board, resources.getScoreboard().getString("Scoreboard.General.Title"));
		
		if (!hide) {

			for (String line : resources.getScoreboard().getStringList("Scoreboard.Lines")) {
				scoreboard.add(addPlaceholdersIfPossible(p, line));
			}

		} else {
			scoreboard.hide();
		}

		scoreboard.update(p);

	}

	private String addPlaceholdersIfPossible(Player p, String text) {

		if (plugin.hasPlaceholderAPI()) {
			text = PlaceholderAPI.setPlaceholders(p, text);
		}

		text = text.replace("%streak%", String.valueOf(this.getKillStreaks().getStreak(p.getName())))
					.replace("%player%", p.getName())
					.replace("%xp%", String.valueOf(this.getLevels().getExperience(p.getUniqueId())))
					.replace("%level%", String.valueOf(this.getLevels().getLevel(p.getUniqueId())))
					.replace("%max_xp%", String.valueOf(resources.getLevels().getInt("Levels.General.Experience.Levelup")))
					.replace("%max_level%", String.valueOf(resources.getLevels().getInt("Levels.General.Level.Maximum")))
					.replace("%kd%", String.valueOf(this.getStats().getKDRatio(p.getUniqueId())))
					.replace("%kit%", this.getKits().getKit(p.getName()))
					.replace("%deaths%", String.valueOf(this.getStats().getDeaths(p.getUniqueId())))
					.replace("%kills%", String.valueOf(this.getStats().getKills(p.getUniqueId())));

		return text;

	}

	public Map<String, String> getHitCache() { return hitCache; }
	
	public Stats getStats() { return stats; }
	
	public Kits getKits() { return kits; }
	
	public KillStreaks getKillStreaks() { return killstreaks; }
	
	public Levels getLevels() { return levels; }
	
	public Cooldowns getCooldowns() { return cooldowns; }
	
}
