package com.planetgallium.kitpvp.listener;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Title;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.XSound;

public class DeathListener implements Listener {
	
	private Title title = new Title();
	private Arena arena;
	private FileConfiguration config;
	private Resources resources;
	
	public DeathListener(Game plugin, Arena arena) {
		this.arena = arena;
		this.config = plugin.getConfig();
		this.resources = plugin.getResources();
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {

		if (Toolkit.inArena(e.getEntity())) {

			Player victim = e.getEntity();
			e.setDeathMessage("");

			if (config.getBoolean("Arena.PreventDeathDrops")) {
				e.getDrops().clear();
			}

			setDeathMessage(victim);
			respawnPlayer(victim);

			arena.getStats().addDeath(victim.getUniqueId());
			arena.getLevels().removeExperience(victim, resources.getLevels().getInt("Levels.General.Experience.Death"));

			if (config.getBoolean("Arena.DeathParticle")) {
				victim.getWorld().playEffect(victim.getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, 152);
			}

			Toolkit.runCommands(config, "Death", victim, "%victim%", victim.getName());

			if (config.getBoolean("Death.Sound.Enabled")) {
				broadcast(victim.getWorld(), XSound.matchXSound(Config.getS("Death.Sound.Sound")).get().parseSound(), 1, (int) Config.getI("Death.Sound.Pitch"));
			}

		}
	
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {

		if (Toolkit.inArena(e.getPlayer())) {

			if (!config.getBoolean("Arena.FancyDeath")) {

				Player p = e.getPlayer();

				new BukkitRunnable() {

					@Override
					public void run() {
						arena.toSpawn(p, p.getWorld().getName());
					}

				}.runTaskLater(Game.getInstance(), 1L);

			}

		}

	}

	private void respawnPlayer(Player victim) {
		
		if (config.getBoolean("Arena.FancyDeath")) {

			Location deathLocation = victim.getLocation();

			new BukkitRunnable() {

				@Override
				public void run() {

					victim.spigot().respawn();
					victim.setGameMode(GameMode.SPECTATOR);
					victim.teleport(deathLocation);

				}

			}.runTaskLater(Game.getInstance(), 1L);

			arena.removePlayer(victim);
			
			new BukkitRunnable() {
				
				int time = Config.getI("Death.Title.Time");
				
				@Override
				public void run() {

					if (time != 0) {

						title.sendTitle(victim, Config.getS("Death.Title.Title"), Config.getS("Death.Title.Subtitle").replace("%seconds%", String.valueOf(time)), 0, 20, 20);
						victim.playSound(victim.getLocation(), XSound.UI_BUTTON_CLICK.parseSound(), 1, 1);
						time--;

					} else {

						if (config.getBoolean("Arena.ClearInventoryOnRespawn")) {
							victim.getInventory().clear();
							victim.getInventory().setArmorContents(null);
						}

						arena.addPlayer(victim, true, config.getBoolean("Arena.GiveItemsOnRespawn"));

						victim.sendMessage(Config.getS("Death.Title.Message"));
						victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0));
						victim.playSound(victim.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1, 1);

						Toolkit.runCommands(config, "Respawn", victim, "none", "none");

						cancel();

					}

				}
				
			}.runTaskTimer(Game.getInstance(), 0L, 20L);
			
		} else {

			arena.removePlayer(victim);
			
			if (config.getBoolean("Arena.ClearInventoryOnRespawn")) {
				victim.getInventory().clear();
				victim.getInventory().setArmorContents(null);
			}

			new BukkitRunnable() {

				@Override
				public void run() {

					arena.addPlayer(victim, true, config.getBoolean("Arena.GiveItemsOnRespawn"));
					Toolkit.runCommands(config, "Respawn", victim, "none", "none");

				}

			}.runTaskLater(Game.getInstance(), 1L);
			
		}
		
	}

	private void setDeathMessage(Player victim) {

		DamageCause cause = victim.getLastDamageCause().getCause();

		if (cause == DamageCause.PROJECTILE && getShooter(victim.getLastDamageCause()).getType() == EntityType.PLAYER) {

			Player killer = (Player) getShooter(victim.getLastDamageCause());

			broadcast(victim.getWorld(), getDeathMessage(victim, killer, "Shot"));
			creditWithKill(victim, killer);

//		} else if (cause == DamageCause.ENTITY_ATTACK) {
//
//			broadcast(victim.getWorld(), Config.getS("Death.Messages.Player").replace("%victim%", victim.getName()).replace("%killer%", victim.getKiller().getName()));
//			creditWithKill(victim, victim.getKiller());

		} else if (victim.getKiller() != null) {

			Player killer = victim.getKiller();

			broadcast(victim.getWorld(), getDeathMessage(victim, killer, "Player"));
			creditWithKill(victim, killer);

		} else if (arena.getHitCache().get(victim.getName()) != null) {

			String killerName = arena.getHitCache().get(victim.getName());
			Player killer = Toolkit.getPlayer(victim.getWorld(), killerName);

			broadcast(victim.getWorld(), getDeathMessage(victim, killer, "Player"));
			creditWithKill(victim, killer);

		} else if ((cause == DamageCause.BLOCK_EXPLOSION || cause == DamageCause.ENTITY_EXPLOSION) && getExplodedEntity(victim.getLastDamageCause()).getType() == EntityType.PRIMED_TNT) {

			String bomberName = getExplodedEntity(victim.getLastDamageCause()).getCustomName();
			Player killer = Toolkit.getPlayer(victim.getWorld(), bomberName);

			broadcast(victim.getWorld(), getDeathMessage(victim, killer, "Player"));
			creditWithKill(victim, killer);

		} else if (cause == DamageCause.VOID) {

			broadcast(victim.getWorld(), getDeathMessage(victim, null, "Void"));

		} else if (cause == DamageCause.FALL) {

			broadcast(victim.getWorld(), getDeathMessage(victim, null, "Fall"));
			
		} else if (cause == DamageCause.FIRE || cause == DamageCause.FIRE_TICK || cause == DamageCause.LAVA) {

			broadcast(victim.getWorld(), getDeathMessage(victim, null, "Fire"));
			
		} else if (cause == DamageCause.BLOCK_EXPLOSION || cause == DamageCause.ENTITY_EXPLOSION) {

			broadcast(victim.getWorld(), getDeathMessage(victim, null, "Explosion"));

		} else {

			broadcast(victim.getWorld(), getDeathMessage(victim, null, "Unknown"));

		}
		
	}

	private Entity getShooter(EntityDamageEvent e) {

		EntityDamageByEntityEvent shotEvent = (EntityDamageByEntityEvent) e;

		Projectile damager = (Projectile) shotEvent.getDamager();
		Entity shooter = (Entity) damager.getShooter();

		return shooter;

	}

	private Entity getExplodedEntity(EntityDamageEvent e) {

		EntityDamageByEntityEvent blownUpEvent = (EntityDamageByEntityEvent) e;

		return blownUpEvent.getDamager();

	}

	private void creditWithKill(Player victim, Player killer) {

		if (victim != null && killer != null) {

			if (!victim.getName().equals(killer.getName())) {

				arena.getStats().addKill(killer.getUniqueId());
				arena.getLevels().addExperience(killer, resources.getLevels().getInt("Levels.General.Experience.Kill"));

				Toolkit.runKillCommands(victim, killer);

				if (resources.getScoreboard().getBoolean("Scoreboard.General.Enabled")) {

					new BukkitRunnable() {

						@Override
						public void run() {

							if (killer instanceof Player) {

								arena.updateScoreboards(killer, false);

							}

						}

					}.runTaskLater(Game.getInstance(), 20L);

				}

			}

		}

	}

	private String getDeathMessage(Player victim, Player killer, String type) {

		String deathMessage = config.getString("Death.Messages." + type);

		if (victim != null) {
			deathMessage = deathMessage.replace("%victim%", victim.getName());
		}

		if (killer != null) {
			deathMessage = deathMessage.replace("%killer%", killer.getName())
					.replace("%killer_health%", String.valueOf(Toolkit.round(killer.getHealth(), 2)));
		}

		return deathMessage;

	}

	private void broadcast(World world, String message) {
		
		if (config.getBoolean("Death.Messages.Enabled")) {
			
			for (Player all : world.getPlayers()) {
				
				all.sendMessage(Config.tr(message));
				
			}
			
		}
		
	}

	private void broadcast(World world, Sound sound, int volume, int pitch) {
		
		if (config.getBoolean("Death.Sound.Enabled")) {
			
			for (Player all : world.getPlayers()) {
				
				all.playSound(all.getLocation(), XSound.matchXSound(sound.toString()).get().parseSound(), volume, pitch);
				
			}
			
		}
		
	}

}
