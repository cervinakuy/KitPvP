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
	
	private String version = "Error";
	public String storageType;
	private boolean needsUpdate = false;
	
	@Override
	public void onEnable() {
		
		instance = this;
		
		resources.load();
		database = new Database(this, "Storage.MySQL");
		arena = new Arena(this, resources);
		
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(this, this);
		pm.registerEvents(new EventListener(arena, resources), this);
		pm.registerEvents(new ArenaListener(arena, resources), this);
		pm.registerEvents(new JoinListener(this, arena), this);
		pm.registerEvents(new LeaveListener(this, arena), this);
		pm.registerEvents(new ArrowListener(), this);
		pm.registerEvents(new DeathListener(arena, resources), this);
		pm.registerEvents(new HitListener(), this);
		pm.registerEvents(new AttackListener(resources), this);
		pm.registerEvents(new ItemListener(arena, resources), this);
		pm.registerEvents(new SoupListener(this), this);
		pm.registerEvents(new TrailListener(resources), this);
		pm.registerEvents(new InteractListener(resources), this);
		pm.registerEvents(new ChatListener(resources), this);
		pm.registerEvents(new KitMenu(resources), this);
		pm.registerEvents(new SignListener(resources), this);
		pm.registerEvents(new AliasCommand(), this);
		pm.registerEvents(new AbilityListener(arena, resources), this);
		pm.registerEvents(new TrackerListener(this), this);
		pm.registerEvents(getArena().getKillStreaks(), this);
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	    getCommand("kitpvp").setExecutor(new MainCommand(this, arena, resources));
	    getCommand("ckit").setExecutor(new KitCommand(resources));
	    getCommand("cspawn").setExecutor(new SpawnCommand());
	    getCommand("ckits").setExecutor(new KitsCommand());
	    getCommand("cstats").setExecutor(new StatsCommand());
	    
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&b&lKIT-PVP&7] &7Enabling &bKitPvP &7version &b" + this.getDescription().getVersion() + "&7..."));
		
		getLogger().info("Some features added by Tekcno");
		
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
			new Placeholders(arena).register();
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
		
		Updater updater = new Updater(this, 27107, false);
		Updater.UpdateResult result = updater.getResult();
		
		switch (result) {
		
		case UPDATE_AVAILABLE:
			
			needsUpdate = true;
			version = updater.getVersion();
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&b&lKIT-PVP&7] &aNew version found! Please update to v" + updater.getVersion() + " on the Spigot page."));
			break;
		
		case NO_UPDATE:
			
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&b&lKIT-PVP&7] &7No new update found. You are on the latest version."));
			break;
			
		case DISABLED:
			
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&b&lKIT-PVP&7] &cChecking for updates is currently disabled."));
			break;
			
		case FAIL_SPIGOT:
			
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&b&lKIT-PVP&7] &cThere was a problem reaching Spigot to check for updates."));
			break;
			
		case FAIL_NOVERSION:
			
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&b&lKIT-PVP&7] &cThe version could not be fetched from Spigot."));
			break;
			
		case BAD_RESOURCEID:
			
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&b&lKIT-PVP&7] &cThe updater resource ID is invalid."));
			break;
		
		}
		
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		
		Player p = e.getPlayer();
		
		if (Toolkit.getMainHandItem(p).getType() == XMaterial.matchXMaterial(Config.getS("Items.Leave.Item")).get().parseMaterial()) {
			
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
	
	public boolean needsUpdate() { return needsUpdate; }
	
	public String getUpdateVersion() { return version; }
	
	public static Game getInstance() { return instance; }
	
	public Arena getArena() { return arena; }
	
	public Database getDatabase() { return database; }
	
	public String getPrefix() { return resources.getMessages().getString("Messages.General.Prefix"); }
	
	public Resources getResources() { return resources; }
	
}
