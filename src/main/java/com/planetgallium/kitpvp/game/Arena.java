package com.planetgallium.kitpvp.game;

import java.util.*;

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

public class Arena {

	private final Game plugin;
	private final Random random;

	private final Resources resources;
	private final Resource config;

	private final Map<String, String> hitCache;

	private final Utilities utilties;
	private final Leaderboards leaderboards;
	private final Stats stats;
	private final Kits kits;
	private final Abilities abilities;
	private final KillStreaks killstreaks;
	private final Cooldowns cooldowns;
	private final Menus menus;

	public Arena(Game plugin, Resources resources) {
		this.plugin = plugin;
		this.random = new Random();

		this.resources = resources;
		this.config = resources.getConfig();

		this.hitCache = new HashMap<>();

		this.utilties = new Utilities(plugin, this);
		this.leaderboards = new Leaderboards(plugin);
		this.stats = new Stats(plugin, this);
		this.kits = new Kits(plugin, this);
		this.abilities = new Abilities(plugin);
		this.killstreaks = new KillStreaks(resources);
		this.cooldowns = new Cooldowns(plugin, this);
		this.menus = new Menus(resources);
	}
	
	public void addPlayer(Player p, boolean toSpawn, boolean giveItems) {
		cooldowns.clearPlayerAbilityCooldowns(p.getName());

		kits.resetPlayerKit(p.getName());

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
			giveArenaItems(p);
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
		
		kits.resetPlayerKit(p.getName());

		if (config.getBoolean("Arena.ResetKillStreakOnLeave")) {
			getKillStreaks().resetStreak(p);
		}
		
		p.setExp(0f);
		p.setFoodLevel(20);

		if (resources.getScoreboard().getBoolean("Scoreboard.General.Enabled")) {
			updateScoreboards(p, true);
		}

		stats.pushCachedStatsToDatabase(p.getName(), false); // cached stats are pushed to database on death
		hitCache.remove(p.getName());
	}
	
	public void deletePlayer(Player p) {
		if (config.getBoolean("Arena.ClearInventoryOnLeave")) {
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
		}

		CacheManager.getPlayerAbilityCooldowns(p.getName()).clear();
		hitCache.remove(p.getName());
		stats.pushCachedStatsToDatabase(p.getName(), true);
	}
	
	public void giveArenaItems(Player p) {
		ConfigurationSection items = config.getConfigurationSection("Items");

		for (String identifier : items.getKeys(false)) {
			String itemPath = "Items." + identifier;

			if (config.getBoolean(itemPath + ".Enabled")) {
				ItemStack item = Toolkit.safeItemStack(config.fetchString(itemPath + ".Material"));
				ItemMeta meta = item.getItemMeta();

				meta.setDisplayName(config.fetchString(itemPath + ".Name"));
				item.setItemMeta(meta);

				p.getInventory().setItem(config.getInt(itemPath + ".Slot"), item);
			}
		}
	}

	public void toSpawn(Player p, String arenaName) {
		if (config.contains("Arenas." + arenaName)) {
			p.teleport(Toolkit.getLocationFromResource(config,
					"Arenas." + arenaName + "." + generateRandomArenaSpawn(arenaName)));
		} else {
			p.sendMessage(resources.getMessages().fetchString("Messages.Error.Arena")
					.replace("%arena%", arenaName));
		}
	}
	
	public void updateScoreboards(Player p, boolean hide) {
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		String scoreboardTitle = utilties.addPlaceholdersIfPossible(p,
				resources.getScoreboard().fetchString("Scoreboard.General.Title"));
		Infoboard scoreboard = new Infoboard(board, scoreboardTitle);
		
		if (!hide) {
			for (String line : resources.getScoreboard().getStringList("Scoreboard.Lines")) {
				scoreboard.add(utilties.addPlaceholdersIfPossible(p, line));
			}
		} else {
			scoreboard.hide();
		}

		scoreboard.update(p);
	}

	public String generateRandomArenaSpawn(String arenaName) {
		ConfigurationSection section = config.getConfigurationSection("Arenas." + arenaName);
		List<String> spawnKeys = new ArrayList<>(section.getKeys(false));

		return spawnKeys.get(random.nextInt(spawnKeys.size()));
	}

	public Map<String, String> getHitCache() { return hitCache; }

	public Stats getStats() { return stats; }

	public Utilities getUtilities() { return utilties; }

	public Leaderboards getLeaderboards() { return leaderboards; }
	
	public Kits getKits() { return kits; }

	public Abilities getAbilities() { return abilities; }
	
	public KillStreaks getKillStreaks() { return killstreaks; }
	
	public Cooldowns getCooldowns() { return cooldowns; }

	public Menus getMenus() { return menus; }
	
}
