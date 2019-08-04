package com.planetgallium.kitpvp.game;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resources;

import me.clip.placeholderapi.PlaceholderAPI;

public class Arena {
	
	private Resources resources;
	
	private List<String> players;
	private List<String> spectators;
	private List<String> users;
	
	private Stats stats;
	private Kits kits;
	private KillStreaks killstreaks;
	private Levels levels;
	
	public Arena(Game plugin, Resources resources) {
		
		this.resources = resources;
		
		this.players = new ArrayList<String>();
		this.spectators = new ArrayList<String>();
		this.users = new ArrayList<String>();
		
		this.stats = new Stats(resources);
		this.kits = new Kits(plugin, resources);
		this.killstreaks = new KillStreaks(resources);
		this.levels = new Levels(this, resources);
		
	}
	
	public void addPlayer(Player p) {
		
		if (isSpectator(p.getName())) spectators.remove(p.getName());
		
		players.add(p.getName());
		
		getKits().clearKit(p.getName());
		
		if (Config.getB("Arena.ClearPotionEffectsOnJoin")) {
			for (PotionEffect effect : p.getActivePotionEffects()) {
				p.removePotionEffect(effect.getType());	
			}	
		}
		
		if (p.getFireTicks() > 0) {
			p.setFireTicks(0);
		}
		
		if (Config.getB("Arena.ForceSurvivalOnJoin")) {
			p.setGameMode(GameMode.SURVIVAL);
		}
		
		p.setExp(0f);
		p.setHealth(20.0);
		p.setFoodLevel(20);
		
		giveItems(p);
		
		toSpawn(p);
		
		if (resources.getScoreboard().getBoolean("Scoreboard.General.Enabled")) {
			updateScoreboards(p, false);
		}
		
	}
	
	public void removePlayer(Player p) {
		
		if (isSpectator(p.getName())) spectators.remove(p.getName());
		
		players.remove(p.getName());
		
		for (PotionEffect effect : p.getActivePotionEffects()) {
			p.removePotionEffect(effect.getType());
		}
		
		getKits().clearKit(p.getName());
		getKillStreaks().resetStreak(p);
		
		p.setExp(0f);
		p.setHealth(20.0);
		p.setFoodLevel(20);
		
		if (resources.getScoreboard().getBoolean("Scoreboard.General.Enabled")) {
			updateScoreboards(p, true);
		}
		
	}
	
	public void deletePlayer(Player p) {
		
		if (Config.getB("Arena.ClearInventoryOnLeave")) {
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
		}
		
		if (isPlayer(p.getName())) players.remove(p.getName());
		if (isSpectator(p.getName())) spectators.remove(p.getName());
		
	}
	
	public void giveItems(Player p) {
		
		if (Config.getB("Items.Kits.Enabled")) {
			
			ItemStack kits = new ItemStack(Material.valueOf(Config.getS("Items.Kits.Item")));
			ItemMeta kitsmeta = kits.getItemMeta();
			
			kitsmeta.setDisplayName(Config.getS("Items.Kits.Name"));
			kits.setItemMeta(kitsmeta);
			
			p.getInventory().setItem(Config.getI("Items.Kits.Slot"), kits);
			
		}
		
		if (Config.getB("Items.Leave.Enabled")) {
			
			ItemStack leave = new ItemStack(Material.valueOf(Config.getS("Items.Leave.Item")));
			ItemMeta leavemeta = leave.getItemMeta();
			
			leavemeta.setDisplayName(Config.getS("Items.Leave.Name"));
			leave.setItemMeta(leavemeta);
			
			p.getInventory().setItem(Config.getI("Items.Leave.Slot"), leave);
			
		}
		
	}
	
	public void toSpawn(Player p) {
		
		if (Config.getC().contains("Arenas.Spawn." + p.getWorld().getName())) {
			
			Location spawn = new Location(Bukkit.getWorld(Config.getS("Arenas.Spawn." + p.getWorld().getName() + ".World")),
					Config.getI("Arenas.Spawn." + p.getWorld().getName() + ".X") + 0.5,
					Config.getI("Arenas.Spawn." + p.getWorld().getName() + ".Y"),
					Config.getI("Arenas.Spawn." + p.getWorld().getName() + ".Z") + 0.5,
					(float) Config.getC().getDouble("Arenas.Spawn." + p.getWorld().getName() + ".Yaw"),
					(float) Config.getC().getDouble("Arenas.Spawn." + p.getWorld().getName() + ".Pitch"));
			
			p.teleport(spawn);
			
		} else {
			
			p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Arena")));
			
		}
		
	}
	
	public void updateScoreboards(Player p, boolean hide) {
		
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		
		Infoboard scoreboard = new Infoboard(board, resources.getScoreboard().getString("Scoreboard.General.Title"));
		
		if (!hide) {
			
			if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
				
				for (String lines : resources.getScoreboard().getStringList("Scoreboard.Lines")) {
					
					scoreboard.add(PlaceholderAPI.setPlaceholders(p, lines.replace("%streak%", String.valueOf(this.getKillStreaks().getStreak(p.getName())))
							.replace("%max%", String.valueOf(resources.getLevels().getInt("Levels.General.Level.Maximum")))
							.replace("%player%", p.getName()).replace("%xp%", String.valueOf(this.getLevels().getExperience(p.getUniqueId())))
							.replace("%level%", String.valueOf(this.getLevels().getLevel(p.getUniqueId())))
							.replace("%kd%", String.valueOf(this.getStats().getKDRatio(p.getUniqueId())))
							.replace("%kit%", this.getKits().getKit(p.getName()))
							.replace("%player%", p.getName())
							.replace("%deaths%", String.valueOf(this.getStats().getDeaths(p.getUniqueId())))
							.replace("%kills%", String.valueOf(this.getStats().getKills(p.getUniqueId())))));
					
				}
				
				scoreboard.update(p);
				
			} else {
				
				for (String lines : resources.getScoreboard().getStringList("Scoreboard.Lines")) {
					
					scoreboard.add(lines.replace("%streak%", String.valueOf(this.getKillStreaks().getStreak(p.getName())))
							.replace("%max%", String.valueOf(resources.getLevels().getInt("Levels.General.Level.Maximum")))
							.replace("%player%", p.getName()).replace("%xp%", String.valueOf(this.getLevels().getExperience(p.getUniqueId())))
							.replace("%level%", String.valueOf(this.getLevels().getLevel(p.getUniqueId())))
							.replace("%kd%", String.valueOf(this.getStats().getKDRatio(p.getUniqueId())))
							.replace("%kit%", this.getKits().getKit(p.getName()))
							.replace("%player%", p.getName())
							.replace("%deaths%", String.valueOf(this.getStats().getDeaths(p.getUniqueId())))
							.replace("%kills%", String.valueOf(this.getStats().getKills(p.getUniqueId()))));
					
				}
				
				scoreboard.update(p);
				
			}
			
		} else {
			
			scoreboard.hide();
			scoreboard.update(p);
			
		}
		
	}
	
	
	public List<String> getPlayers() { return players; }
	
	public List<String> getSpectators() { return spectators; }
	
	public List<String> getUsers() { return users; }
	
	public Stats getStats() { return stats; }
	
	public Kits getKits() { return kits; }
	
	public KillStreaks getKillStreaks() { return killstreaks; }
	
	public Levels getLevels() { return levels; }
	
	public boolean isPlayer(String username) { return players.contains(username); }
	
	public boolean isSpectator(String username) { return spectators.contains(username); }
	
	public void removeUser(String username) { users.remove(username); }
	
}
