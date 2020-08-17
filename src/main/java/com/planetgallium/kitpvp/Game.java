package com.planetgallium.kitpvp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.planetgallium.kitpvp.api.EventListener;
import com.planetgallium.kitpvp.command.*;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.listener.*;
import com.planetgallium.kitpvp.menu.KitMenu;
import com.planetgallium.kitpvp.util.*;

import net.md_5.bungee.api.ChatColor;

public class Game extends JavaPlugin implements Listener {
	
	private static Game instance;
	
	private Arena arena;
	private Database database;
	private Resources resources = new Resources(this);
	
	private String updateVersion = "Error";
	public String storageType;
	private boolean needsUpdate = false;
	private boolean hasPlaceholderAPI = false;
	
	@Override
	public void onEnable() {
		
		instance = this;
		
		resources.load();
		database = new Database(this, "Storage.MySQL");
		arena = new Arena(this, resources);
		
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(this, this);
		pm.registerEvents(new EventListener(arena), this);
		pm.registerEvents(new ArenaListener(this, arena, resources), this);
		pm.registerEvents(new JoinListener(this, arena), this);
		pm.registerEvents(new LeaveListener(this, arena), this);
		pm.registerEvents(new ArrowListener(), this);
		pm.registerEvents(new DeathListener(this, arena), this);
		pm.registerEvents(new HitListener(this), this);
		pm.registerEvents(new AttackListener(resources), this);
		pm.registerEvents(new ItemListener(this, arena, resources), this);
		pm.registerEvents(new SoupListener(this), this);
		pm.registerEvents(new TrailListener(resources), this);
		pm.registerEvents(new ChatListener(resources), this);
		pm.registerEvents(new SignListener(arena, resources), this);
		pm.registerEvents(new AliasCommand(), this);
		pm.registerEvents(new AbilityListener(arena, resources), this);
		pm.registerEvents(new TrackerListener(this), this);
		pm.registerEvents(new MenuListener(arena, resources), this);
		pm.registerEvents(getArena().getKillStreaks(), this);
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	    getCommand("kitpvp").setExecutor(new MainCommand(this));
	    getCommand("ckit").setExecutor(new KitCommand(resources));
	    getCommand("cspawn").setExecutor(new SpawnCommand());
	    getCommand("ckits").setExecutor(new KitsCommand());
	    getCommand("cstats").setExecutor(new StatsCommand());
	    
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&b&lKIT-PVP&7] &7Enabling &bKitPvP &7version &b" + this.getDescription().getVersion() + "&7..."));
		
		if (Config.getC().getString("Storage.Type").equalsIgnoreCase("mysql")) {
			storageType = "mysql";
			
			database.setup();
			database.holdConnection();
			database.createData();
		} else {
			storageType = "yaml";
		}
		
		new Metrics(this);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				checkUpdate();
				
			}
			
		}.runTaskAsynchronously(this);
		
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[&b&lKIT-PVP&7] &7Discovered &bPlaceholderAPI&7, now hooking into it."));
			new Placeholders(this).register();
			hasPlaceholderAPI = true;
		}
		
		Bukkit.getConsoleSender().sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aDone!"));
		
	}

	@Override
	public void onDisable() {
		for (Player all : Bukkit.getOnlinePlayers()) {
			database.saveAndRemovePlayer(all);
		}
	}

	private void checkUpdate() {

		Updater.of(this).resourceId(27107).handleResponse((versionResponse, version) -> {
			switch (versionResponse) {
				case FOUND_NEW:
					Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&b&lKIT-PVP&7] &aNew version found! Please update to v" + version + " on the Spigot page."));
					needsUpdate = true;
					updateVersion = version;
					break;
				case LATEST:
					Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&b&lKIT-PVP&7] &7No new update found. You are on the latest version."));
					break;
				case UNAVAILABLE:
					Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&b&lKIT-PVP&7] &cUnable to perform an update check."));
					break;
			}
		}).check();
		
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		
		Player p = e.getPlayer();
		
		if (Toolkit.getMainHandItem(p).getType() == XMaterial.matchXMaterial(Config.getS("Items.Leave.Item")).get().parseMaterial().get()) {
			
			if (Config.getB("Items.Leave.Enabled")) {
					
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
					if (Config.getB("Items.Leave.BungeeCord.Enabled")) {
						
				        ByteArrayDataOutput out = ByteStreams.newDataOutput();
				        out.writeUTF("Connect");
				        
				        String server = Config.getS("Items.Leave.BungeeCord.Server");
				        
				        out.writeUTF(server);
				        p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
				        
					}
					
				}
				
			}
			
		}
		
	}

	public boolean hasPlaceholderAPI() { return hasPlaceholderAPI; }

	public boolean needsUpdate() { return needsUpdate; }
	
	public String getUpdateVersion() { return updateVersion; }
	
	public static Game getInstance() { return instance; }
	
	public Arena getArena() { return arena; }
	
	public Database getDatabase() { return database; }
	
	public String getPrefix() { return resources.getMessages().getString("Messages.General.Prefix"); }
	
	public Resources getResources() { return resources; }
	
}
