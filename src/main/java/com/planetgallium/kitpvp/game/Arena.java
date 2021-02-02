package com.planetgallium.kitpvp.game;

import java.util.*;

import com.cryptomorin.xseries.XMaterial;
import com.planetgallium.kitpvp.util.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

import com.planetgallium.kitpvp.Game;

import me.clip.placeholderapi.PlaceholderAPI;

public class Arena {

	private Game plugin;
	private Random random;

	private Resources resources;
	private Resource config;

	private Map<String, String> hitCache;
	
	private Stats stats;
	private Kits kits;
	private KillStreaks killstreaks;
	private Levels levels;
	private Cooldowns cooldowns;
	private Menus menus;
	
	public Arena(Game plugin, Resources resources) {
		this.plugin = plugin;
		this.random = new Random();

		this.resources = resources;
		this.config = resources.getConfig();

		this.hitCache = new HashMap<>();
		
		this.stats = new Stats(plugin, resources);
		this.kits = new Kits(plugin, this);
		this.killstreaks = new KillStreaks(resources);
		this.levels = new Levels(this, resources);
		this.cooldowns = new Cooldowns(this, resources);
		this.menus = new Menus(resources);
	}
	
	public void addPlayer(Player p, boolean toSpawn, boolean giveItems) {

		CacheManager.getPlayerAbilityCooldowns(p.getName()).clear();

		kits.resetKit(p.getName());
		killstreaks.setStreak(p, 0);
		
		if (config.getBoolean("Arena.ClearPotionEffectsOnJoin")) {
			for (PotionEffect effect : p.getActivePotionEffects()) {
				p.removePotionEffect(effect.getType());
			}
		}
		
		if (p.getFireTicks() > 0) {
			p.setFireTicks(0);
		}

		p.setGameMode(GameMode.SURVIVAL);
		
		if (config.getBoolean("Arena.FancyDeath")) {
			p.setHealth(20.0);
		}
		
		p.setExp(0f);
		p.setFoodLevel(20);

		if (giveItems) {
			giveItems(p);
		}

		if (toSpawn) {
			toSpawn(p, p.getWorld().getName());
		}

		if (resources.getScoreboard().getBoolean("Scoreboard.General.Enabled")) {
			updateScoreboards(p, false);
		}
		
	}
	
	public void removePlayer(Player p) {

		CacheManager.getPlayerAbilityCooldowns(p.getName()).clear();
		CacheManager.getWitchPotionUsers().remove(p.getName());

		for (PotionEffect effect : p.getActivePotionEffects()) {
			p.removePotionEffect(effect.getType());
		}
		
		kits.resetKit(p.getName());

		if (config.getBoolean("Arena.ResetKillStreakOnLeave")) {
			getKillStreaks().resetStreak(p);
		}
		
//		if (config.getBoolean("Arena.FancyDeath")) {
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
		
		if (config.getBoolean("Arena.ClearInventoryOnLeave")) {
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
		}

		CacheManager.getPlayerAbilityCooldowns(p.getName()).clear();

		if (hitCache.containsKey(p.getName())) {
			hitCache.remove(p.getName());
		}
		
	}
	
	public void giveItems(Player p) {

		ConfigurationSection items = config.getConfigurationSection("Items");

		for (String identifier : items.getKeys(false)) {

			String itemPath = "Items." + identifier;

			if (config.getBoolean(itemPath + ".Enabled")) {

				ItemStack item = new ItemStack(XMaterial.matchXMaterial(config.getString(itemPath + ".Material")).get().parseMaterial());
				ItemMeta meta = item.getItemMeta();

				meta.setDisplayName(config.getString(itemPath + ".Name"));
				item.setItemMeta(meta);

				p.getInventory().setItem(config.getInt(itemPath + ".Slot"), item);

			}

		}
		
	}

	public void toSpawn(Player p, String arenaName) {

		if (config.contains("Arenas." + arenaName)) {

			p.teleport(Toolkit.getLocationFromResource(config, "Arenas." + arenaName + "." + generateRandomArenaSpawn(arenaName)));

		} else {

			p.sendMessage(resources.getMessages().getString("Messages.Error.Arena").replace("%arena%", arenaName));

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
					.replace("%max_xp%", String.valueOf(resources.getLevels().getInt("Levels.Options.Experience-To-Level-Up")))
					.replace("%max_level%", String.valueOf(resources.getLevels().getInt("Levels.Options.Maximum-Level")))
					.replace("%kd%", String.valueOf(this.getStats().getKDRatio(p.getUniqueId())))
					.replace("%deaths%", String.valueOf(this.getStats().getDeaths(p.getUniqueId())))
					.replace("%kills%", String.valueOf(this.getStats().getKills(p.getUniqueId())));

		if (getKits().getKitOfPlayer(p.getName()) != null) {
			text = text.replace("%kit%", getKits().getKitOfPlayer(p.getName()).getName());
		} else {
			text = text.replace("%kit%", "None");
		}

		return text;

	}

	public String generateRandomArenaSpawn(String arenaName) {

		ConfigurationSection section = config.getConfigurationSection("Arenas." + arenaName);
		List<String> spawnKeys = new ArrayList<String>();

		for (String identifier : section.getKeys(false)) {
			spawnKeys.add(identifier);
		}

		return spawnKeys.get(random.nextInt(spawnKeys.size()));

	}

	public boolean isCombatActionPermittedInRegion(Player p) {

		if (plugin.hasWorldGuard()) {

			if (WorldGuardAPI.getInstance().allows(p, WorldGuardFlag.PVP.getFlag())) {
				return true;
			}

			p.sendMessage(resources.getMessages().getString("Messages.Error.PVP"));
			return false;

		}

		return true;

	}

	public Map<String, String> getHitCache() { return hitCache; }
	
	public Stats getStats() { return stats; }
	
	public Kits getKits() { return kits; }
	
	public KillStreaks getKillStreaks() { return killstreaks; }
	
	public Levels getLevels() { return levels; }
	
	public Cooldowns getCooldowns() { return cooldowns; }

	public Menus getMenus() { return menus; }
	
}
