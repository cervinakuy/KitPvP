package com.planetgallium.kitpvp.command;

import com.cryptomorin.xseries.XSound;
import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.Kit;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MainCommand implements CommandExecutor {

    private final List<String> spawnUsers = new ArrayList<>();

    private final Game plugin;
    private final Arena arena;
    private final Resources resources;
    private final Resource config;
    private final Resource messages;

    public MainCommand(Game game) {
        this.plugin = game;
        this.arena = game.getArena();
        this.resources = game.getResources();
        this.config = resources.getConfig();
        this.messages = resources.getMessages();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 0) {
            executeInfoCommand(sender);
            return true;

        } else if (args.length == 1) {

            if (isCommand(args, sender,"help", null)) {
                executeHelpCommand(sender);
                return true;

            } else if (isCommand(args, sender, "reload", "kp.command.reload")) {
                executeReloadCommand(sender);
                return true;

            } else if (isCommand(args, sender, "debug", "kp.command.debug")) {
                executeDebugCommand(sender);
                return true;

            } else if (isCommand(args, sender, "kits", "kp.command.kits")) {
                executeKitsCommand(sender);
                return true;

            }

        } else if (args.length == 2) {

            if (isCommand(args, sender, "clear", "kp.command.clear.other")) {
                executeClearCommandOther(sender, args);
                return true;

            } else if (isCommand(args, sender, "delete", "kp.command.delete")) {
                executeDeleteKitCommand(sender, args);
                return true;

            } else if (isCommand(args, sender, "stats", "kp.command.stats.other")) {
                executeStatsCommandOther(sender, args);
                return true;
            }

        } else if (args.length == 3) {

            if (isCommand(args, sender, "kit", "kp.command.kit.other")) {
                executeKitCommand(sender, args);
                return true;
            }

        } else if (args.length == 4) {

            if (isCommand(args, sender, "setstats", "kp.command.setstats")) {
                executeSetStatsCommand(sender, args);
                return true;
            }

        }

        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (args.length == 1) {

                if (isCommand(args, sender, "stats", "kp.command.stats")) {
                    executeStatsCommandSelf(p);
                    return true;

                } else if (isCommand(args, sender, "menu", "kp.command.menu")) {
                    executeMenuCommand(p);
                    return true;

                } else if (isCommand(args, sender, "spawn", "kp.command.spawn")) {
                    executeSpawnCommand(p);
                    return true;

                } else if (isCommand(args, sender, "clear","kp.command.clear")) {
                    executeClearCommandSelf(p);
                    return true;

                } else if (isCommand(args, sender, "addspawn", "kp.command.addspawn")) {
                    executeAddSpawnCommand(p);
                    return true;

                } else if (isCommand(args, sender, "delarena", "kp.command.delarena")) {
                    executeDeleteArenaCommand(p);
                    return true;

                } else {
                    sendUnknownCommand(sender, alias, args);
                    return true;

                }

            } else if (args.length == 2) {

                if (isCommand(args, sender, "arena", "kp.command.spawn")) {
                    executeArenaCommand(p, args);
                    return true;

                } else if (isCommand(args, sender, "preview", "kp.command.preview")) {
                    executePreviewCommand(p, args);
                    return true;

                } else if (isCommand(args, sender, "create", "kp.command.create")) {
                    executeCreateKitCommand(p, args);
                    return true;

                } else if (isCommand(args, sender, "kit", null)) {
                    executeKitCommandSelf(p, args);
                    return true;

                } else {
                    sendUnknownCommand(sender, alias, args);
                    return true;

                }

            } else {
                sendUnknownCommand(sender, alias, args);
                return true;

            }

        } else {
            sender.sendMessage(messages.fetchString("Messages.General.Player"));
            return true;
        }

    }

    private void executeInfoCommand(CommandSender sender) {
        sender.sendMessage(Toolkit.translate("&7[&b&lKIT-PVP&7]"));
        sender.sendMessage(Toolkit.translate("&7Version: &b" + plugin.getDescription().getVersion()));
        sender.sendMessage(Toolkit.translate("&7Developer: &bCervinakuy"));
        sender.sendMessage(Toolkit.translate("&7Commands: &b/kp help"));
        sender.sendMessage(Toolkit.translate("&7Download: &bbit.ly/KP-Download"));
    }

    private void executeHelpCommand(CommandSender sender) {
        sender.sendMessage(Toolkit.translate("&3&m           &r &b&lKIT-PVP &3Created by Cervinakuy &3&m             "));
        sender.sendMessage(Toolkit.translate(" "));
        sender.sendMessage(Toolkit.translate("&7- &b/kp &7Displays information about KitPvP."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp help &7Displays the help message."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp reload &7Reloads the configuration files."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp debug &7Prints debug information."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp addspawn &7Adds a spawn to an arena."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp arena <arena> &7Teleports you to a different arena."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp delarena &7Removes an arena."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp spawn &7Teleports you to the local arena spawn."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp create <kitName> &7Creates a kit from your inventory."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp delete <kitName> &7Deletes an existing kit."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp preview <kitName> &7Preview the contents of a kit."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp kits &7Lists all available kits."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp kit <kitName> &7Select a kit."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp kit <kitName> <player> &7Attempts to select a kit for a player."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp clear &7Clears your current kit."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp clear <player> &7Clears a kit for a player."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp stats &7View your stats."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp stats <player> &7View the stats of another player."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp menu &7Displays the kits menu."));
        sender.sendMessage(Toolkit.translate("&7- &b/kp setstats <player> <type> <amount> &7Change stats of a player."));
        sender.sendMessage(Toolkit.translate(" "));
        sender.sendMessage(Toolkit.translate("&3&m                                                                               "));
    }

    private void executeReloadCommand(CommandSender sender) {
        resources.reload();
        CacheManager.clearCaches();
        arena.getMenus().getKitMenu().rebuildCache();
        arena.getAbilities().rebuildCache();

        sender.sendMessage(messages.fetchString("Messages.Commands.Reload"));
    }

    private void executeDebugCommand(CommandSender sender) {
        String names = "";

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            names += plugin.getName() + " ";
        }

        String serverVersion = Bukkit.getBukkitVersion() + " " +
                (Bukkit.getVersion().contains("Spigot") ? "(Spigot)" : "(Other)");
        String pluginVersion = plugin.getDescription().getVersion() + " " +
                (plugin.needsUpdate() ? "&c(Requires Update)" : "&a(Latest Version)");
        String isSpawnSet = (config.contains("Arenas") ? "&aConfigured" : "&cUnconfigured");
        String supportDiscordLink = "https://discord.gg/Hfej6UR8Bk";

        sender.sendMessage(Toolkit.translate("&7[&b&lKIT-PVP&7] &aServer Version: &7" + serverVersion));
        sender.sendMessage(Toolkit.translate("&7[&b&lKIT-PVP&7] &aPlugin Version: &7" + pluginVersion));
        sender.sendMessage(Toolkit.translate("&7[&b&lKIT-PVP&7] &aSpawn Set: " + isSpawnSet));
        sender.sendMessage(Toolkit.translate("&7[&b&lKIT-PVP&7] &aSupport Discord: &7" + supportDiscordLink));
        sender.sendMessage(Toolkit.translate("&7[&b&lKIT-PVP&7] &aPlugin List: &7" + names));
    }

    private void executeKitsCommand(CommandSender sender) {
        String message = "";

        for (String kitName : resources.getPluginDirectoryFiles("kits", false)) {
            String[] fileName = kitName.split(".yml", 2);
            message += fileName[0] + ", ";
        }

        message = message.substring(0, message.length() - 2);

        sender.sendMessage(messages.fetchString("Messages.Commands.Kits").replace("%kits%", message));
    }

    private void executeClearCommandOther(CommandSender sender, String[] args) {
        String playerName = args[1];

        Player target = Bukkit.getPlayer(playerName);

        if (target != null && Toolkit.inArena(target)) {
            clearKit(target);

            target.sendMessage(messages.fetchString("Messages.Commands.Cleared"));
            sender.sendMessage(messages.fetchString("Messages.Commands.ClearedOther")
                    .replace("%player%", target.getName()));

        } else {
            sender.sendMessage(messages.fetchString("Messages.Error.Offline"));
        }
    }

    private void executeDeleteKitCommand(CommandSender sender, String[] args) {
        String kitName = args[1];

        if (arena.getKits().isKit(kitName)) {
            arena.getKits().deleteKit(kitName);
            sender.sendMessage(messages.fetchString("Messages.Commands.Delete")
                    .replace("%kit%", kitName));
        } else {
            sender.sendMessage(messages.fetchString("Messages.Error.Lost"));
        }
    }

    private void executeStatsCommandOther(CommandSender sender, String[] args) {
        String targetName = args[1];

        if (plugin.getDatabase().isPlayerRegistered(targetName)) {
            sendStatsMessage(sender, targetName);
        } else {
            sender.sendMessage(messages.fetchString("Messages.Error.Offline"));
        }
    }

    private void executeKitCommand(CommandSender sender, String[] args) {
        String kitName = args[1];
        String playerName = args[2];

        Player target = Bukkit.getPlayer(playerName);

        if (target != null && Toolkit.inArena(target)) {
            Kit kitToGive = arena.getKits().getKitByName(kitName);
            sender.sendMessage(messages.fetchString("Messages.Commands.KitOther")
                    .replace("%player%", playerName)
                    .replace("%kit%", kitName));

            arena.getKits().attemptToGiveKitToPlayer(target, kitToGive);
        } else {
            sender.sendMessage(messages.fetchString("Messages.Error.Offline"));
        }
    }

    private void executeSetStatsCommand(CommandSender sender, String[] args) {
        String playerName = args[1];
        String statsIdentifier = args[2];
        String possibleAmount = args[3];

        if (statsIdentifier.equalsIgnoreCase("kills") ||
                statsIdentifier.equalsIgnoreCase("deaths") ||
                statsIdentifier.equalsIgnoreCase("level") ||
                statsIdentifier.equalsIgnoreCase("experience")) {

            if (!StringUtils.isNumeric(possibleAmount)) {
                sender.sendMessage(resources.getMessages().fetchString("Messages.Error.InvalidNumber")
                        .replace("%number%", possibleAmount));
            }

            String playerUUID = plugin.getDatabase().usernameToUUID(playerName);

            if (playerUUID == null) {
                sender.sendMessage(resources.getMessages().fetchString("Messages.Error.Offline"));
            }

            int amount = Integer.parseInt(possibleAmount);
            arena.getStats().setStat(statsIdentifier, playerName, amount);
            arena.getStats().pushCachedStatsToDatabase(playerName, false);

            sender.sendMessage(resources.getMessages().fetchString("Messages.Commands.SetStats")
                    .replace("%player%", playerName)
                    .replace("%amount%", String.valueOf(amount))
                    .replace("%type%", statsIdentifier));

        } else {
            sender.sendMessage(resources.getMessages().fetchString("Messages.Error.InvalidType")
                    .replace("%type%", statsIdentifier)
                    .replace("%types%", "kills, deaths, level, experience"));
        }
    }

    private void executeStatsCommandSelf(Player p) {
        sendStatsMessage(p, p.getName());
    }

    private void executeMenuCommand(Player p) {
        arena.getMenus().getKitMenu().open(p);
    }

    private void executeSpawnCommand(Player p) {
        if (!config.contains("Arenas." + p.getWorld().getName())) {
            p.sendMessage(messages.fetchString("Messages.Error.Arena")
                    .replace("%arena%", p.getWorld().getName()));
            return;
        }

        if (spawnUsers.contains(p.getName())) {
            return;
        }

        p.sendMessage(messages.fetchString("Messages.Commands.Teleporting"));
        spawnUsers.add(p.getName());
        XSound.play(p, "ENTITY_ITEM_PICKUP, 1, -1");

        Location beforeLocation = p.getLocation();

        new BukkitRunnable() {
            public int time = config.getInt("Spawn.Time") + 1;

            @Override
            public void run() {
                time--;

                if (time != 0) {
                    if (p.getGameMode() != GameMode.SPECTATOR) {
                        p.sendMessage(messages.fetchString("Messages.Commands.Time")
                                .replace("%time%", String.valueOf(time)));
                        XSound.play(p, "BLOCK_NOTE_BLOCK_SNARE, 1, 1");

                        if (beforeLocation.getBlockX() != p.getLocation().getBlockX() ||
                                beforeLocation.getBlockY() != p.getLocation().getBlockY() ||
                                beforeLocation.getBlockZ() != p.getLocation().getBlockZ()) {
                            p.sendMessage(messages.fetchString("Messages.Error.Moved"));
                            spawnUsers.remove(p.getName());
                            cancel();
                        }
                    } else {
                        spawnUsers.remove(p.getName());
                        cancel();
                    }
                } else {
                    p.sendMessage(messages.fetchString("Messages.Commands.Teleport"));

                    arena.toSpawn(p, p.getWorld().getName());

                    if (config.getBoolean("Arena.ClearKitOnCommandSpawn")) {
                        clearKit(p);
                    }

                    spawnUsers.remove(p.getName());
                    XSound.play(p, "ENTITY_ENDERMAN_TELEPORT, 1, 1");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void executeClearCommandSelf(Player p) {
        clearKit(p);
        p.sendMessage(messages.fetchString("Messages.Commands.Cleared"));
    }

    private void executeAddSpawnCommand(Player p) {
        String arenaName = p.getWorld().getName();
        int spawnNumber = Toolkit.getNextAvailable(config, "Arenas." + arenaName,
                1000, false, 1);

        Toolkit.saveLocationToResource(config, "Arenas." + arenaName + "." + spawnNumber, p.getLocation());

        p.sendMessage(messages.fetchString("Messages.Commands.Added")
                .replace("%number%", String.valueOf(spawnNumber))
                .replace("%arena%", arenaName));
        XSound.play(p, "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1");
    }

    private void executeDeleteArenaCommand(Player p) {
        String arenaName = p.getWorld().getName();

        if (config.contains("Arenas." + arenaName)) {
            config.set("Arenas." + arenaName, null);
            plugin.saveConfig();

            p.sendMessage(messages.fetchString("Messages.Commands.Removed").replace("%arena%", arenaName));
            XSound.play(p, "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1");
        } else {
            p.sendMessage(messages.fetchString("Messages.Error.Arena"));
        }
    }

    private void executeArenaCommand(Player p, String[] args) {
        String arenaName = args[1];

        if (resources.getConfig().getBoolean("Arena.PreventArenaSignUseWithKit")) {
            if (arena.getKits().playerHasKit(p.getName())) {
                p.sendMessage(messages.fetchString("Messages.Error.KitInvalid"));
                return;
            }
        }

        arena.toSpawn(p, arenaName);
    }

    private void executePreviewCommand(Player p, String[] args) {
        String kitName = args[1];

        if (arena.getKits().isKit(kitName)) {
            Kit kitToPreview = arena.getKits().getKitByName(kitName);
            arena.getMenus().getPreviewMenu().open(p, kitToPreview, resources);
        } else {
            p.sendMessage(messages.fetchString("Messages.Error.Lost"));
        }
    }

    private void executeCreateKitCommand(Player p, String[] args) {
        String kitName = args[1];

        if (!validateKitName(kitName)) {
            p.sendMessage(messages.fetchString("Messages.Error.InvalidKitName"));
            return;
        }

        if (!arena.getKits().isKit(kitName)) {
            arena.getKits().createKit(p, kitName);

            p.sendMessage(messages.fetchString("Messages.Commands.Create")
                    .replace("%kit%", kitName));
        } else {
            p.sendMessage(messages.fetchString("Messages.Error.Exists"));
        }
    }

    private void executeKitCommandSelf(Player p, String[] args) {
        if (Toolkit.inArena(p)) {
            String kitName = args[1];
            Kit kitToGive = arena.getKits().getKitByName(kitName);

            arena.getKits().attemptToGiveKitToPlayer(p, kitToGive);
        } else {
            p.sendMessage(messages.fetchString("Messages.Error.Location"));
        }
    }

    private boolean hasPermission(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) {
            return true;
        }

        sender.sendMessage(messages.fetchString("Messages.General.Permission")
                .replace("%permission%", permission));
        return false;
    }

    private void clearKit(Player p) {
        p.getInventory().setArmorContents(null);
        p.getInventory().clear();

        Toolkit.setMaxHealth(p, 20);
        p.setHealth(20.0);

        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }

        if (config.getBoolean("Arena.GiveItemsOnClear")) {
            arena.giveArenaItems(p);
        }

        arena.getKits().resetPlayerKit(p.getName());
    }

    private void sendStatsMessage(CommandSender receiver, String username) {
        for (String line : messages.getStringList("Messages.Stats.Message")) {
            receiver.sendMessage(arena.getUtilities().replaceBuiltInPlaceholdersIfPresent(line, username));
        }
    }

    private boolean validateKitName(String possibleKitName) {
        return !possibleKitName.contains("-") && !possibleKitName.contains("+");
    }

    private boolean isCommand(String[] args, CommandSender sender, String arg0, String permission) {
        if (args[0].equalsIgnoreCase(arg0)) {
            return permission == null || hasPermission(sender, permission);
        }
        return false;
    }

    private void sendUnknownCommand(CommandSender sender, String alias, String[] args) {
        StringBuilder unknownCommand = new StringBuilder();
        for (String arg : args) {
            unknownCommand.append(arg).append(" ");
        }

        sender.sendMessage(Toolkit.translate("%prefix% &cUnknown command: /" + alias + " " + unknownCommand));
    }

}
