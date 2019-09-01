package com.planetgallium.kitpvp;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

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
import com.planetgallium.kitpvp.command.AliasCommand;
import com.planetgallium.kitpvp.command.KitCommand;
import com.planetgallium.kitpvp.command.KitsCommand;
import com.planetgallium.kitpvp.command.MainCommand;
import com.planetgallium.kitpvp.command.SpawnCommand;
import com.planetgallium.kitpvp.command.StatsCommand;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.game.PlayerData;
import com.planetgallium.kitpvp.listener.AbilityListener;
import com.planetgallium.kitpvp.listener.ArenaListener;
import com.planetgallium.kitpvp.listener.ArrowListener;
import com.planetgallium.kitpvp.listener.AttackListener;
import com.planetgallium.kitpvp.listener.ChatListener;
import com.planetgallium.kitpvp.listener.DeathListener;
import com.planetgallium.kitpvp.listener.HitListener;
import com.planetgallium.kitpvp.listener.InteractListener;
import com.planetgallium.kitpvp.listener.ItemListener;
import com.planetgallium.kitpvp.listener.JoinListener;
import com.planetgallium.kitpvp.listener.LeaveListener;
import com.planetgallium.kitpvp.listener.SignListener;
import com.planetgallium.kitpvp.listener.SoupListener;
import com.planetgallium.kitpvp.listener.TrailListener;
import com.planetgallium.kitpvp.menu.KitMenu;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Metrics;
import com.planetgallium.kitpvp.util.Placeholders;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.Updater;
import com.planetgallium.kitpvp.util.XMaterial;

import net.md_5.bungee.api.ChatColor;

public class Game extends JavaPlugin implements Listener {
	
	private String version = "Error";
	private boolean needsUpdate = false;
	
	private static Game instance;
	
	private Arena arena;
	private final Resources resources = new Resources(this);
	
	private static Connection connection;
	
	public static String storageType;
	public static HashMap<UUID, PlayerData> playerCache = new HashMap<>();
	
	@Override
	public void onEnable() {
		
		instance = this;
		
		resources.load();
		arena = new Arena(this, resources);
		
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(this, this);
		pm.registerEvents(new EventListener(arena, resources), this);
		pm.registerEvents(new ArenaListener(arena, resources), this);
		pm.registerEvents(new JoinListener(arena), this);
		pm.registerEvents(new LeaveListener(resources), this);
		pm.registerEvents(new ArrowListener(), this);
		pm.registerEvents(new DeathListener(arena, resources), this);
		pm.registerEvents(new HitListener(), this);
		pm.registerEvents(new AttackListener(resources), this);
		pm.registerEvents(new ItemListener(arena, resources), this);
		pm.registerEvents(new SoupListener(), this);
		pm.registerEvents(new TrailListener(resources), this);
		pm.registerEvents(new InteractListener(resources), this);
		pm.registerEvents(new ChatListener(resources), this);
		pm.registerEvents(new KitMenu(resources), this);
		pm.registerEvents(new SignListener(resources), this);
		pm.registerEvents(new AliasCommand(), this);
		pm.registerEvents(new AbilityListener(arena, resources), this);
		pm.registerEvents(getArena().getKillStreaks(), this);
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		if (Config.getC().getString("Storage-Type").equalsIgnoreCase("mysql")) {
			storageType = "mysql";
			MySQLSetup();
			preventMySQLTimeout();
			createPlayerData();
		} else {
			storageType = "yaml";
		}
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	    getCommand("kitpvp").setExecutor(new MainCommand(this, arena, resources));
	    getCommand("ckit").setExecutor(new KitCommand(resources));
	    getCommand("cspawn").setExecutor(new SpawnCommand());
	    getCommand("ckits").setExecutor(new KitsCommand());
	    getCommand("cstats").setExecutor(new StatsCommand());
	    
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&b&lKIT-PVP&7] &7Enabling &bKitPvP &7version &b" + this.getDescription().getVersion() + "&7..."));
		
		new Metrics(this);
		
		// UPDATE CHECKER //
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				checkUpdate();
				
			}
			
		}.runTaskAsynchronously(this);
		
		// UPDATE CHECKER
		
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[&b&lKIT-PVP&7] &7Discovered &bPlaceholderAPI&7, now hooking into it."));
			new Placeholders(arena).register();
		}
		
		Bukkit.getConsoleSender().sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aDone!"));
		
	}
	
    public void MySQLSetup() {
        String host = getConfig().getString("MySQL.host");
        String database = getConfig().getString("MySQL.database");
        int port = getConfig().getInt("MySQL.port");
        String username = getConfig().getString("MySQL.username");
        String password = getConfig().getString("MySQL.password");

        try {
            synchronized (this) {
                if (getConnection() != null && !getConnection().isClosed()) {
                    return;
                }
                Class.forName("com.mysql.jdbc.Driver");
                setConnection(DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username, password));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void preventMySQLTimeout() {
        try {
            getConnection().createStatement().executeQuery("SELECT 1;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Bukkit.getScheduler().runTaskLater(this, ()->preventMySQLTimeout(), 24000L);
    }
    public void createPlayerData() {
        boolean isTableCreated = false;
        try {
        	String tableName = getConfig().getString("MySQL.table");
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet rs = databaseMetaData.getTables(null, null, tableName, null);
            if (rs.next()) {
                isTableCreated = true;
            }
            String playerTable = "CREATE TABLE " + tableName + " (" +
                    "UUID VARCHAR(255)," +
                    "USERNAME VARCHAR(255)," +
                    "LEVEL INT (4)," +
                    "EXPERIENCE INT(10)," +
                    "KILLS INT(10)," +
                    "DEATHS INT(10)" +
                    ");";
            if (!isTableCreated) {
                Statement statement = connection.createStatement();
                statement.executeUpdate(playerTable);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
		
		if (Toolkit.getMainHandItem(p).getType() == XMaterial.matchXMaterial(Config.getS("Items.Leave.Item")).parseMaterial()) {
			
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
	
	public String getPrefix() { return resources.getMessages().getString("Messages.General.Prefix"); }
	
	public Resources getResources() { return resources; }
	
    public static Connection getConnection() {
        return connection;
    }


    private static void setConnection(Connection connection) {
        Game.connection = connection;
    }

}
