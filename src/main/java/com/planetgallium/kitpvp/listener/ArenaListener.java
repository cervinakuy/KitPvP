package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.XMaterial;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.weather.WeatherChangeEvent;

public class ArenaListener implements Listener {

    private Arena arena;
    private Resources resources;

    public ArenaListener(Arena arena, Resources resources) {
        this.arena = arena;
        this.resources = resources;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {

        Player p = e.getPlayer();

        if (Toolkit.inArena(p) && Config.getB("Arena.PreventBlockBreaking")) {

            e.setCancelled(!p.hasPermission("kp.arena.blockbreaking"));

        }

    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {

        Player p = e.getPlayer();

        if (Toolkit.inArena(p)) {

            if (e.getBlock().getType() == XMaterial.TNT.parseMaterial()) {

                e.setCancelled(true);

            } else if (arena.getKits().hasKit(p.getName())) {

                String abilityPath = resources.getKits(arena.getKits().getKit(p.getName())).getString("Ability.Activator.Item");

                if (abilityPath != null) {

                    if (e.getBlock().getType() == XMaterial.matchXMaterial(abilityPath).get().parseMaterial()) {

                        e.setCancelled(true);

                    }

                }

            } else if ((Toolkit.getMainHandItem(p).hasItemMeta() && Toolkit.getMainHandItem(p).getItemMeta().getDisplayName().equals(Config.getS("Items.Kits.Name"))) || Toolkit.getMainHandItem(p).getType() == XMaterial.matchXMaterial(Config.getS("Items.Leave.Item")).get().parseMaterial()) {

                e.setCancelled(true);

            } else {

                if (Config.getB("Arena.PreventBlockPlacing")) {

                    e.setCancelled(!p.hasPermission("kp.arena.blockplacing"));

                }

            }

        }

    }

    @EventHandler
    public void onDamage(PlayerItemDamageEvent e) {

        if (Toolkit.inArena(e.getPlayer()) && Config.getB("Arena.PreventItemDurabilityDamage")) {

            e.setCancelled(true);

        }

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {

        Player p = e.getPlayer();

        if (Toolkit.inArena(p) && Config.getB("Arena.PreventItemDropping")) {

            e.setCancelled(!p.hasPermission("kp.arena.itemdropping"));

        }

    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {

        Player p = (Player) e.getEntity();

        if (Toolkit.inArena(p) && Config.getB("Arena.PreventHunger")) {

            e.setCancelled(true);

        }

    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {

        if (Toolkit.inArena(e.getEntity())) {

            e.setCancelled(true);

        }

    }

    @EventHandler
    public void onArrowDamage(EntityDamageByEntityEvent e) {

        if (e.getEntity() instanceof Player && e.getDamager() instanceof Projectile) {

            Player damagedPlayer = (Player) e.getEntity();

            if (Toolkit.inArena(damagedPlayer)) {

                if (Config.getB("Arena.NoKitProtection")) {

                    e.setCancelled(!arena.getKits().hasKit(damagedPlayer.getName()));

                }

            }

        }

    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {

        if (Toolkit.inArena(e.getWorld()) && Config.getB("Arena.KeepWeatherAtSunny")) {

            if (e.toWeatherState()) {

                e.setCancelled(true);
                e.getWorld().setStorm(false);
                e.getWorld().setThundering(false);
                e.getWorld().setWeatherDuration(0);

            }

        }

    }

    @EventHandler
    public void onExplosion(EntityDamageEvent e) {

        if (e.getEntity() instanceof Player) {

            Player damagedPlayer = (Player) e.getEntity();

            if (Toolkit.inArena(damagedPlayer)) {

                if (e.getCause() == DamageCause.BLOCK_EXPLOSION || e.getCause() == DamageCause.ENTITY_EXPLOSION || e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.FIRE_TICK) {

                    if (Config.getB("Arena.NoKitProtection")) {

                        e.setCancelled(!arena.getKits().hasKit(damagedPlayer.getName()));

                    }

                } else if (e.getCause() == DamageCause.FALL) {

                    if (Config.getB("Arena.PreventFallDamage")) {

                        e.setCancelled(true); // only canceling if preventing fall damage is enabled, this allows for WorldGuard to step in

                    }

                } else if (damagedPlayer.getGameMode() == GameMode.SPECTATOR) {

                    e.setCancelled(true);

                }

            }

        }

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {

        Player p = e.getPlayer();

        if (Toolkit.inArena(p)) {

            if (Toolkit.getMainHandItem(p).getType() == XMaterial.ENDER_EYE.parseMaterial()) {

                e.setCancelled(true);

            }

            if (e.getClickedBlock() != null) {

                if (e.getClickedBlock().getType() == XMaterial.CHEST.parseMaterial()) {

                    if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {

                        if (Config.getB("Arena.PreventChestOpen")) {

                            e.setCancelled(true);

                        }

                    }

                }

            }

        }

    }

    @EventHandler
    public void onPearl(PlayerTeleportEvent e) {

        Player p = e.getPlayer();

        if (Toolkit.inArena(p)) {

            if (e.getCause() == TeleportCause.ENDER_PEARL) {

                e.setCancelled(true);
                p.teleport(e.getTo());

            }

        }

    }

}
