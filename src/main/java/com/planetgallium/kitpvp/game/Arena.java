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

	private final Game plugin;
	private final Random random;

	private final Resources resources;
	private final Resource config;

	private final Map<String, String> hitCache;

	private final Leaderboards leaderboards;
	private final Stats stats;
	private final Kits kits;
	private final KillStreaks killstreaks;
	private final Cooldowns cooldowns;
	private final Menus menus;
	
	public Arena(Game plugin, Resources resources) {
		this.plugin = plugin;
		this.random = new Random();

		this.resources = resources;
		this.config = resources.getConfig();

		this.hitCache = new HashMap<>();

		this.leaderboards = new Leaderboards(plugin);
		this.stats = new Stats(plugin, this);
		this.kits = new Kits(plugin, this);
		this.killstreaks = new KillStreaks(resources);
		this.cooldowns = new Cooldowns(plugin);
		this.menus = new Menus(resources);
	}
	
	public void addPlayer(Player p, boolean toSpawn, boolean giveItems) {

		CacheManager.getPlayerAbilityCooldowns(p.getName()).clear();

		kits.resetKit(p.getName());

		if (config.getBoolean("Arena.ResetKillStreakOnLeave")) {
			killstreaks.setStreak(p, 0);
		}
		
		if (config.getBoolean("Arena.ClearPotionEffectsOnJoin")) {
			for (PotionEffect effect : p.getActivePotionEffects()) {
				p.removePotionEffect(effect.getType());
			}
		}
		
		if (p.getFireTicks() > 0) {
			p.setFireTicks(0);
		}

		p.setGameMode(GameMode.SURVIVAL);
		Toolkit.setMaxHealth(p, 20);

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

		hitCache.remove(p.getName());
		
	}
	
	public void deletePlayer(Player p) {
		
		if (config.getBoolean("Arena.ClearInventoryOnLeave")) {
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
		}

		CacheManager.getPlayerAbilityCooldowns(p.getName()).clear();

		hitCache.remove(p.getName());
		
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

		return replaceBuiltInPlaceholdersIfPresent(text, p.getName());

	}

	public String replaceBuiltInPlaceholdersIfPresent(String s, String username) {

		// The reason I'm doing all these if statements rather than a more concise code solution is to reduce
		// the amount of data that is unnecessarily fetched (ex by using .replace) to improve performance
		// no longer constantly fetching stats from database for EACH line of scoreboard on update and player join

		if (s.contains("%streak%")) {
			s = s.replace("%streak%", String.valueOf(getKillStreaks().getStreak(username)));
		}

		if (s.contains("%player%")) {
			s = s.replace("%player%", username);
		}

		if (s.contains("%xp%")) {
			s = s.replace("%xp%", String.valueOf(stats.getStat("experience", username)));
		}

		if (s.contains("%level%")) {
			s = s.replace("%level%", String.valueOf(stats.getStat("level", username)));
		}

		if (s.contains("%max_xp%")) {
			s = s.replace("%max_xp%", String.valueOf(stats.getRegularOrRelativeNeededExperience(username)));
		}

		if (s.contains("%max_level%")) {
			s = s.replace("%max_level%", String.valueOf(resources.getLevels().getInt("Levels.Options.Maximum-Level")));
		}

		if (s.contains("%kd%")) {
			s = s.replace("%kd%", String.valueOf(getStats().getKDRatio(username)));
		}

		if (s.contains("%kills%")) {
			s = s.replace("%kills%", String.valueOf(stats.getStat("kills", username)));
		}

		if (s.contains("%deaths%")) {
			s = s.replace("%deaths%", String.valueOf(stats.getStat("deaths", username)));
		}

		if (s.contains("%kit%")) {
			if (getKits().getKitOfPlayer(username) != null) {
				s = s.replace("%kit%", getKits().getKitOfPlayer(username).getName());
			} else {
				s = s.replace("%kit%", "None");
			}
		}

		return s;

	}

	public String generateRandomArenaSpawn(String arenaName) {
		ConfigurationSection section = config.getConfigurationSection("Arenas." + arenaName);
		List<String> spawnKeys = new ArrayList<>(section.getKeys(false));

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

	public Leaderboards getLeaderboards() { return leaderboards; }
	
	public Kits getKits() { return kits; }
	
	public KillStreaks getKillStreaks() { return killstreaks; }
	
	public Cooldowns getCooldowns() { return cooldowns; }

	public Menus getMenus() { return menus; }
	
}
