package com.planetgallium.kitpvp.util;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;

public class WorldGuardAPI {

    /**
     * Adapted from: https://www.spigotmc.org/threads/worldguard-6-and-7-support.382130/
     */

    private static WorldGuardAPI instance;
    protected byte version;

    public static WorldGuardAPI getInstance() {
        if (instance == null) {
            instance = new WorldGuardAPI();
            Plugin worldGuardPlugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
            String version = worldGuardPlugin != null && worldGuardPlugin.isEnabled() ? worldGuardPlugin.getDescription().getVersion() : null;
            instance.version = (byte) (version != null ? version.startsWith("6") ? 6 : 7 : -1);
        }
        return instance;
    }

    public boolean allows(Player player, StateFlag...flags) {
        Location location = player.getLocation();
        return version == -1 || version == 6 ? allowsWg6(player, location, flags) : allowsWg7(player, location, flags);
    }

    private boolean allowsWg6(Player player, Location location, StateFlag...flags) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        com.sk89q.worldguard.protection.managers.RegionManager regionManager = com.sk89q.worldguard.bukkit.WGBukkit.getRegionManager(location.getWorld());
        com.sk89q.worldguard.protection.ApplicableRegionSet regionSet = null;

        try {
            regionSet = (ApplicableRegionSet) regionManager.getClass().getMethod("getApplicableRegions", Location.class).invoke(regionManager, location);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }

        for (StateFlag flag : flags) {
            if (regionSet.queryState(localPlayer, flag) == StateFlag.State.DENY) {
                return false;
            }
        }

        return true;
    }

    private boolean allowsWg7(Player player, Location location, StateFlag...flags) {
        com.sk89q.worldguard.protection.regions.RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
        com.sk89q.worldguard.protection.regions.RegionQuery query = container.createQuery();
        ApplicableRegionSet regionSet = query.getApplicableRegions(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(location));

        return regionSet.testState(WorldGuardPlugin.inst().wrapPlayer(player), flags);
    }

}

