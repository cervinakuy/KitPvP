package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.WorldGuardLoader;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Toolkit;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class WorldGuardListener implements Listener {

    private final Resource config;
    private final Arena arena;
    private final WorldGuardLoader worldGuardLoader;

    public WorldGuardListener(Game game)
    {
        this.config = game.getResources().getConfig();
        this.arena = game.getArena();
        this.worldGuardLoader = game.getLoader();
    }

    @EventHandler
    public void onArenaEnter(PlayerMoveEvent e) {
        //Getting worldguard arenas
        if (!Toolkit.inArena(e.getPlayer()) || !Game.getInstance().hasWorldGuard() || !config.getBoolean("Arena.SpawnOnArenaJoinWithNoKit", false) || arena.getKits().playerHasKit(e.getPlayer().getName()))
            return ;
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        LocalPlayer player = WorldGuardPlugin.inst().wrapPlayer(e.getPlayer());
        ApplicableRegionSet set = query.getApplicableRegions(player.getLocation());
        StateFlag.State flag;

        for (ProtectedRegion region : set) {
            flag = region.getFlag(worldGuardLoader.getFlag());
            if (flag == StateFlag.State.ALLOW) {
                //Running this not in task may print moved too quickly spam. So now I teleport the player synchronized with ticks
                Bukkit.getScheduler().runTask(Game.getInstance(), () -> {
                    e.getPlayer().setFallDistance(0);
                    arena.toSpawn(e.getPlayer(), e.getPlayer().getWorld().getName());
                });
            }
        }
    }

}
