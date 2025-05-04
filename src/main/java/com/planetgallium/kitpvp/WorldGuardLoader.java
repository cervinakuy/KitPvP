package com.planetgallium.kitpvp;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

public class WorldGuardLoader {

    private StateFlag flag;

    public WorldGuardLoader()
    {
        try {
            // Attempt to get the WorldGuard instance and register flags
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            // Register your custom flag
            flag = new StateFlag("kp-arena", false);
            registry.register(flag);

        } catch (NoClassDefFoundError e) {
            // Log a message indicating WorldGuard is not found
        } catch (FlagConflictException e) {
            // Handle the case where the flag is already registered by another plugin
            Game.getInstance().getServer().getPluginManager().disablePlugin(Game.getInstance());
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            e.printStackTrace();
        }
    }

    public StateFlag getFlag()
    {
        return flag;
    }

}
