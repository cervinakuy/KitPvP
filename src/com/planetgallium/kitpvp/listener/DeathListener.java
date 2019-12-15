package com.planetgallium.kitpvp.listener;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
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
	private Resources resources;
	
	public DeathListener(Arena arena, Resources resources) {
		this.arena = arena;
		this.resources = resources;
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		
		if (Toolkit.inArena(e.getEntity())) {
			
			Player victim = e.getEntity();
			e.setDeathMessage("");
			
			// Death Drops
			if (Config.getB("Arena.PreventDeathDrops")) {
				e.getDrops().clear();
			}
			
			respawnPlayer(victim);
			setDeathMessage(victim, e);
			
			// Update Stats
			arena.getStats().addDeath(victim.getUniqueId());
			arena.getLevels().removeExperience(victim, resources.getLevels().getInt("Levels.General.Experience.Death"));
			
			// Death Particle
			victim.getWorld().playEffect(victim.getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, 152);
			
			// Death Commands
			Toolkit.runCommands("Death", victim, "%victim%", victim.getName());
			
			// Death Sound
			if (Config.getB("Death.Sound.Enabled")) {
				broadcast(victim.getWorld(), XSound.matchXSound(Config.getS("Death.Sound.Sound")).parseSound(), 1, (int) Config.getI("Death.Sound.Pitch"));
			}
			
		}
	
	}

	private void respawnPlayer(Player p) {
		
		if (Config.getB("Arena.FancyDeath")) {
			
			Location deathLocation = p.getLocation();
			Bukkit.getScheduler().scheduleSyncDelayedTask(Game.getInstance(), () -> p.spigot().respawn(), 1);
			
			p.setGameMode(GameMode.SPECTATOR);
			p.teleport(deathLocation);
			
			arena.removePlayer(p);
			
			new BukkitRunnable() {
				
				int time = Config.getI("Death.Title.Time");
				
				@Override
				public void run() {
					
					if (time != 0) {

						title.sendTitle(p, Config.getS("Death.Title.Title"), Config.getS("Death.Title.Subtitle").replace("%seconds%", String.valueOf(time)), 0, 20, 20);
						p.playSound(p.getLocation(), XSound.UI_BUTTON_CLICK.parseSound(), 1, 1);
						time--;
						
					} else {
						
						if (Config.getB("Arena.ClearInventoryOnRespawn")) {
							p.getInventory().clear();
							p.getInventory().setArmorContents(null);
						}
						
						arena.addPlayer(p);
						
						p.sendMessage(Config.getS("Death.Title.Message"));
						p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0));
						p.playSound(p.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1, 1);
						
						Toolkit.runCommands("Respawn", p);
						
						cancel();
						
					}
					
				}
				
			}.runTaskTimer(Game.getInstance(), 0L, 20L);
			
		} else {
			
			arena.removePlayer(p);
			
			if (Config.getB("Arena.ClearInventoryOnRespawn")) {
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
			}
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(Game.getInstance(), () -> arena.addPlayer(p), 1);
			
			Toolkit.runCommands("Respawn", p);
			
		}
		
	}
	
	private void setDeathMessage(Player p, PlayerDeathEvent e) {

		DamageCause cause = p.getLastDamageCause().getCause();
		
		if (cause == DamageCause.FALL) {
			
			broadcast(p.getWorld(), Config.getS("Death.Messages.Fall").replace("%victim%", p.getName()));
			
		} else if (cause == DamageCause.FIRE || cause == DamageCause.FIRE_TICK || cause == DamageCause.LAVA) {
			
			broadcast(p.getWorld(), Config.getS("Death.Messages.Fire").replace("%victim%", p.getName()));
			
		} else if (cause == DamageCause.BLOCK_EXPLOSION || cause == DamageCause.ENTITY_EXPLOSION) {
			
			broadcast(p.getWorld(), Config.getS("Death.Messages.Explosion").replace("%victim%", p.getName()));
			
		} else if (cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.VOID) {
			
			if (p.getKiller() != null) {
				
				broadcast(p.getWorld(), Config.getS("Death.Messages.Player").replace("%victim%", p.getName()).replace("%killer%", p.getKiller().getName()));
				
				arena.getStats().addKill(p.getKiller().getUniqueId());
				arena.getLevels().addExperience(p.getKiller(), resources.getLevels().getInt("Levels.General.Experience.Kill"));
				
				Toolkit.runKillCommands(p, p.getKiller());
				
				if (resources.getScoreboard().getBoolean("Scoreboard.General.Enabled")) {
					
					new BukkitRunnable() {
						
						@Override
		            	public void run() {
							
							arena.updateScoreboards(p.getKiller().getPlayer(), false);
							
						}
						
					}.runTaskLater(Game.getInstance(), 20L);
					
				}
				
			} else {
				
				broadcast(p.getWorld(), Config.getS("Death.Messages.Void").replace("%victim%", p.getName()));
				
			}
					
		} else if (cause == DamageCause.PROJECTILE) {
			
			if (p.getLastDamageCause().getEntityType() == EntityType.ARROW ||
					p.getLastDamageCause().getEntityType() == EntityType.SNOWBALL ||
					Toolkit.versionToNumber() >= 113 && p.getLastDamageCause().getEntityType() == EntityType.TRIDENT ||
					p.getLastDamageCause().getEntityType() == EntityType.PLAYER) {
				
				EntityDamageByEntityEvent shotEvent = (EntityDamageByEntityEvent) p.getLastDamageCause();
					
				if (shotEvent.getDamager().getType() == EntityType.ARROW) {
					
					Arrow arrow = (Arrow) shotEvent.getDamager();
					
					if (arrow.getShooter() != null) {
						
						if (arrow.getShooter() instanceof Player) {
							
							Player shooter = (Player) arrow.getShooter();
							broadcast(p.getWorld(), Config.getS("Death.Messages.Shot").replace("%victim%", p.getName()).replace("%shooter%", shooter.getName()));
							arena.getStats().addKill(shooter.getUniqueId());
							arena.getLevels().addExperience(shooter, resources.getLevels().getInt("Levels.General.Experience.Kill"));
							
						}
						
					}
					
				} else if (shotEvent.getDamager().getType() == EntityType.SNOWBALL) {
					
					Snowball snowball = (Snowball) shotEvent.getDamager();
					
					if (snowball.getShooter() != null) {
						
						if (snowball.getShooter() instanceof Player) {
							
							Player shooter = (Player) snowball.getShooter();
							broadcast(p.getWorld(), Config.getS("Death.Messages.Shot").replace("%victim%", p.getName()).replace("%shooter%", shooter.getName()));
							arena.getStats().addKill(shooter.getUniqueId());
							arena.getLevels().addExperience(shooter, resources.getLevels().getInt("Levels.General.Experience.Kill"));
							
						}
						
					}
					
				} else if (Toolkit.versionToNumber() >= 113 && shotEvent.getDamager().getType() == EntityType.TRIDENT) {
					
					Trident trident = (Trident) shotEvent.getDamager();
					
					if (trident.getShooter() != null) {
						
						if (trident.getShooter() instanceof Player) {
							
							Player shooter = (Player) trident.getShooter();
							broadcast(p.getWorld(), Config.getS("Death.Messages.Shot").replace("%victim%", p.getName()).replace("%shooter%", shooter.getName()));
							arena.getStats().addKill(shooter.getUniqueId());
							arena.getLevels().addExperience(shooter, resources.getLevels().getInt("Levels.General.Experience.Kill"));
							
						}
						
					}
					
				}
				
			}
			
		} else {
			
			broadcast(p.getWorld(), Config.getS("Death.Messages.Unknown").replace("%victim%", p.getName()));
			
		}
		
	}
	
	private void broadcast(World world, String message) {
		
		if (Config.getB("Death.Messages.Enabled")) {
			
			for (Player all : world.getPlayers()) {
				
				all.sendMessage(message);
				
			}
			
		}
		
	}
	
	private void broadcast(World world, Sound sound, int volume, int pitch) {
		
		if (Config.getB("Death.Sound.Enabled")) {
			
			for (Player all : world.getPlayers()) {
				
				all.playSound(all.getLocation(), XSound.matchXSound(sound.toString()).parseSound(), volume, pitch);
				
			}
			
		}
		
	}

}
