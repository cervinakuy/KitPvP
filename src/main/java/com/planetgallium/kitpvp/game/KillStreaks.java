package com.planetgallium.kitpvp.game;

import java.util.HashMap;
import java.util.UUID;

import com.cryptomorin.xseries.messages.Titles;
import com.planetgallium.kitpvp.util.*;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class KillStreaks implements Listener {

	private final Resources resources;
	private final Resource killConfig;
	private final HashMap<UUID, Integer> kills;
	
	public KillStreaks(Resources resources) {
		this.resources = resources;
		this.killConfig = resources.getKillStreaks();
		this.kills = new HashMap<>();
	}
	
	@EventHandler
	public void onKill(PlayerDeathEvent e) {
		if (Toolkit.inArena(e.getEntity())) {
			Player damager = e.getEntity().getKiller();
			Player damagedPlayer = e.getEntity();

			if (damager != null && !damager.getUniqueId().equals(damagedPlayer.getUniqueId())) {
				kills.put(damager.getUniqueId(), getStreak(damager.getUniqueId()) + 1);
				runStreakCase("KillStreaks", damager);
				runStreakCase("EndStreaks", damagedPlayer);
				kills.put(damagedPlayer.getUniqueId(), 0);
				
			} else {
				kills.put(damagedPlayer.getUniqueId(), 0);
				runStreakCase("EndStreaks", damagedPlayer);
			}
			
		}
	}
	
	@EventHandler
	public void createStreak(PlayerJoinEvent e) {
		Player p = e.getPlayer();

		if (!kills.containsKey(p.getUniqueId())) {
			kills.put(e.getPlayer().getUniqueId(), 0);
		}
	}
	
	@EventHandler
	public void removeStreak(PlayerQuitEvent e) {
		if (resources.getConfig().getBoolean("Arena.ResetKillStreakOnLeave")) {
			kills.put(e.getPlayer().getUniqueId(), 0);
		}
	}
	
	public void runStreakCase(String streakType, Player p) {
		String username = p.getName();
		int streakNumber = getStreak(p.getUniqueId());
		World world = p.getWorld();

		String pathPrefix = streakType + "." + streakNumber;

		if (killConfig.contains(pathPrefix)) {

			// TITLE
			if (killConfig.contains(pathPrefix + ".Title")) {
				String title = killConfig.fetchString(pathPrefix + ".Title.Title")
						.replace("%player%", username)
						.replace("%streak%", String.valueOf(streakNumber));
				String subtitle = killConfig.fetchString(pathPrefix + ".Title.Subtitle")
						.replace("%player%", username)
						.replace("%streak%", String.valueOf(streakNumber));

				for (Player local : world.getPlayers()) {
					Titles.sendTitle(local, 20, 60, 20, title, subtitle);
				}
			}

			// SOUND
			if (killConfig.contains(pathPrefix + ".Sound")) {
				Sound sound = Toolkit.safeSound(killConfig.fetchString(pathPrefix + ".Sound.Sound"));
				int pitch = killConfig.getInt(pathPrefix + ".Sound.Pitch");

				for (Player local : world.getPlayers()) {
					local.playSound(local.getLocation(), sound, 1, pitch);
				}
			}

			// MESSAGE
			if (killConfig.contains(pathPrefix + ".Message")) {
				String message = killConfig.fetchString(pathPrefix + ".Message.Message")
						.replace("%streak%", String.valueOf(streakNumber))
						.replace("%player%", username);

				for (Player local : world.getPlayers()) {
					local.sendMessage(message);
				}
			}

			// COMMANDS
			if (killConfig.contains(pathPrefix + ".Commands")) {
				Toolkit.runCommands(p, killConfig.getStringList(pathPrefix + ".Commands"),
						"none", "none");
			}

		}
		
	}
	
	public int getStreak(UUID uniqueId) {
		if (!kills.containsKey(uniqueId)) {
			kills.put(uniqueId, 0);
		}

		return kills.get(uniqueId);
	}
	
	public void resetStreak(Player p) {
		if (kills.containsKey(p.getUniqueId())) {
			runStreakCase("EndStreaks", p);
			kills.put(p.getUniqueId(), 0);
		}
	}

	public void setStreak(Player p, int streak) {
		kills.put(p.getUniqueId(), streak);
	}

}
