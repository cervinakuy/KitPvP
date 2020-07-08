package com.planetgallium.kitpvp.game;

import java.util.HashMap;

import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Title;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.XSound;

public class KillStreaks implements Listener {

	private Resources resources;
	
	private Title title = new Title();
	private HashMap<String, Integer> kills = new HashMap<String, Integer>();
	private FileConfiguration killConfig;
	
	public KillStreaks(Resources resources) {
		this.resources = resources;
	}
	
	@EventHandler
	public void onKill(PlayerDeathEvent e) {
		
		if (Toolkit.inArena(e.getEntity())) {
			
			Player damager = e.getEntity().getKiller();
			Player damagedPlayer = e.getEntity();
				
			if (damager != null && damager.getName() != damagedPlayer.getName()) {
				
				kills.put(damager.getName(), (kills.get(damager.getName()) + 1));
				runCase("KillStreaks", getStreak(damager.getName()), damager.getName(), damager.getWorld(), damager);
				runCase("EndStreaks", getStreak(damagedPlayer.getName()), damagedPlayer.getName(), damagedPlayer.getWorld(), damagedPlayer);
				kills.put(damagedPlayer.getName(), 0);
				
			} else {
		
				kills.put(damagedPlayer.getName(), 0);
				runCase("EndStreaks", kills.get(damagedPlayer.getName()), damagedPlayer.getName(), damagedPlayer.getWorld(), damagedPlayer);
				
			}
			
		}
		
	}
	
	@EventHandler
	public void createStreak(PlayerJoinEvent e) {

		Player p = e.getPlayer();

		if (!kills.containsKey(p.getName())) {
			kills.put(e.getPlayer().getName(), 0);
		}
		
	}
	
	@EventHandler
	public void removeStreak(PlayerQuitEvent e) {

		if (Config.getB("Arena.ResetKillStreakOnLeave")) {
			kills.put(e.getPlayer().getName(), 0);
		}
		
	}
	
	public void runCase(String streakType, int streakNumber, String username, World world, Player p) {
		
		killConfig = resources.getKillStreaks();
		
		if (killConfig.getBoolean(streakType + "." + streakNumber + ".Streak-Enabled")) {
			
			if (killConfig.getBoolean(streakType + "." + streakNumber + ".Title.Enabled")) {
				
				for (Player local : world.getPlayers()) {
					
					title.sendTitle(local, killConfig.getString(streakType + "." + streakNumber + ".Title.Title"), killConfig.getString(streakType + "." + streakNumber + ".Title.Subtitle").replace("%player%", username).replace("%streak%", String.valueOf(streakNumber)), 20, 60, 20);
					
				}
				
			}
			
			if (killConfig.getBoolean(streakType + "." + streakNumber + ".Sound.Enabled")) {
				
				for (Player local : world.getPlayers()) {
					
					local.playSound(local.getLocation(), XSound.matchXSound(killConfig.getString(streakType + "." + streakNumber + ".Sound.Sound")).get().parseSound(), 1, killConfig.getInt(streakType + "." + streakNumber + ".Sound.Pitch"));
					
				}
				
			}
			
			if (killConfig.getBoolean(streakType + "." + streakNumber + ".Message.Enabled")) {
				
				for (Player local : world.getPlayers()) {
					
					local.sendMessage(Config.tr(killConfig.getString(streakType + "." + streakNumber + ".Message.Message").replace("%streak%", String.valueOf(streakNumber)).replace("%player%", username)));
					
				}
				
			}
			
			if (killConfig.getBoolean(streakType + "." + streakNumber + ".Commands.Enabled")) {

				Toolkit.runCommands(resources.getKillStreaks(), streakType + "." + streakNumber, p, "none", "none");
				
			}
		
		}
		
	}
	
	public int getStreak(String username) {
		
		if (kills.containsKey(username)) {

			return kills.get(username);
			
		}
		
		return 0;
		
	}
	
	public void resetStreak(Player p) {

		if (kills.containsKey(p.getName())) {
			runCase("EndStreaks", kills.get(p.getName()), p.getName(), p.getWorld(), p);
			kills.put(p.getName(), 0);
		}
		
	}

	public void setStreak(Player p, int streak) {

		kills.put(p.getName(), streak);

	}

}
