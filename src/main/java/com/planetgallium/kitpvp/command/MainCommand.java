package com.planetgallium.kitpvp.command;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.Kit;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.*;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MainCommand implements CommandExecutor {

    private List<String> spawnUsers = new ArrayList<String>();

    private Game plugin;
    private Arena arena;
    private FileConfiguration config;
    private Resources resources;

    public MainCommand(Game game) {
        this.plugin = game;
        this.arena = game.getArena();
        this.config = game.getConfig();
        this.resources = game.getResources();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 0) {

            sender.sendMessage(Config.tr("&7[&b&lKIT-PVP&7]"));
            sender.sendMessage(Config.tr("&7Version: &b" + plugin.getDescription().getVersion()));
            sender.sendMessage(Config.tr("&7Developer: &bCervinakuy"));
            sender.sendMessage(Config.tr("&7Commands: &b/kp help"));
            sender.sendMessage(Config.tr("&7Download: &bbit.ly/KP-Download"));
            return true;

        } else if (args.length == 1) {

            if (args[0].equalsIgnoreCase("help")) {

                sender.sendMessage(Config.tr("&3&m           &r &b&lKIT-PVP &3Created by Cervinakuy &3&m             "));
                sender.sendMessage(Config.tr(" "));
                sender.sendMessage(Config.tr("&7- &b/kp &7Displays information about KitPvP."));
                sender.sendMessage(Config.tr("&7- &b/kp help &7Displays the help message."));
                sender.sendMessage(Config.tr("&7- &b/kp reload &7Reloads the configuration."));
                sender.sendMessage(Config.tr("&7- &b/kp debug &7Prints debug information."));
                sender.sendMessage(Config.tr("&7- &b/kp addspawn &7Adds a spawn to an arena."));
                sender.sendMessage(Config.tr("&7- &b/kp delarena &7Removes an arena."));
                sender.sendMessage(Config.tr("&7- &b/kp spawn &7Teleports you to the Spawn in your current arena."));
                sender.sendMessage(Config.tr("&7- &b/kp arena [arena] &7Teleport to a different KitPvP arena."));
                sender.sendMessage(Config.tr("&7- &b/kp create [kitname] &7Creates a kit from your inventory."));
                sender.sendMessage(Config.tr("&7- &b/kp delete [kitname] &7Deletes an existing kit."));
                sender.sendMessage(Config.tr("&7- &b/kp preview [kitname] &7Preview the contents of a kit."));
                sender.sendMessage(Config.tr("&7- &b/kp kit [kitname] &7Select a kit."));
                sender.sendMessage(Config.tr("&7- &b/kp kit [kitname] [player] &7Attempts to select a kit for a player."));
                sender.sendMessage(Config.tr("&7- &b/kp kits &7Lists all available kits."));
                sender.sendMessage(Config.tr("&7- &b/kp clear &7Clears your current kit."));
                sender.sendMessage(Config.tr("&7- &b/kp clear [player] &7Clears a kit for a player."));
                sender.sendMessage(Config.tr("&7- &b/kp stats &7View your stats."));
                sender.sendMessage(Config.tr("&7- &b/kp menu &7Displays the kits menu."));
                sender.sendMessage(Config.tr("&7- &b/kp import &7Imports all stats from the MySQL database."));
                sender.sendMessage(Config.tr("&7- &b/kp export &7Exports all stats to the MySQL database."));
                sender.sendMessage(Config.tr(" "));
                sender.sendMessage(Config.tr("&3&m                                                                               "));
                return true;

            } else if (args[0].equalsIgnoreCase("reload") && hasPermission(sender, "kp.command.reload")) {

                plugin.reloadConfig();
                resources.reload();

                sender.sendMessage(resources.getMessages().getString("Messages.Commands.Reload"));
                return true;

            } else if (args[0].equalsIgnoreCase("debug") && hasPermission(sender, "kp.command.debug")) {

                String names = "";

                for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                    names += plugin.getName() + " ";
                }

                sender.sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aServer Version: &7" + Bukkit.getBukkitVersion()) + " " + (Bukkit.getVersion().contains("Spigot") ? "(Spigot)" : "(Other)"));
                sender.sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aPlugin Version: " + plugin.getDescription().getVersion()) + " " + (plugin.needsUpdate() ? "&c(Requires Update)" : "&a(Latest Version)"));
                sender.sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aSpawn Set: " + (config.contains("Arenas") ? "&aConfigured" : "&cUnconfigured")));
                sender.sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aSupport Discord: &7https://discord.gg/GtXQKZ6"));
                sender.sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aPlugin List: &7" + names));

            } else if (args[0].equalsIgnoreCase("import") && hasPermission(sender, "kp.command.import")) {

                if (plugin.getDatabase().isEnabled()) {

                    sender.sendMessage(Config.tr("%prefix% &7Importing data, please wait..."));
                    plugin.getDatabase().importData(resources);
                    sender.sendMessage(Config.tr("%prefix% &aDatabase data has successfully been exported to the stats.yml."));

                } else {

                    sender.sendMessage(Config.tr("%prefix% &cImporting is unnecessary, you can switch your storage type and import."));

                }

                return true;

            } else if (args[0].equalsIgnoreCase("export") && hasPermission(sender, "kp.command.export")) {

                if (plugin.getDatabase().isEnabled()) {

                    sender.sendMessage(Config.tr("%prefix% &7Exporting data, please wait..."));
                    plugin.getDatabase().exportData(resources);
                    sender.sendMessage(Config.tr("%prefix% &aStats successfully exported to database."));

                } else {

                    sender.sendMessage(Config.tr("%prefix% &cYou are using YAML storage, so exporting is not possible. Change your settings in the config.yml."));

                }

                return true;

            } else if (args[0].equalsIgnoreCase("kits") && hasPermission(sender, "kp.command.kits")) {

                String message = "";

                for (String kitName : arena.getKits().getKitList()) {
                    String[] fileName = kitName.split(".yml", 2);
                    message += fileName[0] + ", ";
                }

                message = message.substring(0, message.length() - 2);

                sender.sendMessage(resources.getMessages().getString("Messages.Commands.Kits").replace("%kits%", message));

                return true;

            }

        } else if (args.length == 2) {

            if (args[0].equalsIgnoreCase("clear") && hasPermission(sender, "kp.command.clear.other")) {

                String playerName = args[1];

                Player target = Bukkit.getPlayer(playerName);

                if (target != null && Toolkit.inArena(target)) {

                    clearKit(target);

                    target.sendMessage(resources.getMessages().getString("Messages.Commands.Cleared"));
                    sender.sendMessage(resources.getMessages().getString("Messages.Commands.ClearedOther").replace("%player%", target.getName()));

                } else {

                    sender.sendMessage(resources.getMessages().getString("Messages.Error.Offline"));

                }

                return true;

            } else if (args[0].equalsIgnoreCase("delete") && hasPermission(sender, "kp.command.delete")) {

                String kitName = args[1];

                if (arena.getKits().isKit(kitName)) {

                    resources.removeResource(kitName + ".yml");
                    sender.sendMessage(resources.getMessages().getString("Messages.Commands.Delete")
                            .replace("%kit%", kitName));

                } else {

                    sender.sendMessage(resources.getMessages().getString("Messages.Error.Lost"));

                }

                return true;

            }

        } else if (args.length == 3) {

            if (args[0].equalsIgnoreCase("kit") && hasPermission(sender, "kp.command.kit.other")) {

                String kitName = args[1];
                String playerName = args[2];

                Player target = Bukkit.getPlayer(playerName);

                if (target != null && Toolkit.inArena(target)) {

                    Kit kitToGive = arena.getKits().getKitByName(kitName);
                    arena.getKits().attemptToGiveKitToPlayer(target, kitToGive);

                    sender.sendMessage(resources.getMessages().getString("Messages.Commands.KitOther").replace("%player%", playerName).replace("%kit%", kitName));

                } else {

                    sender.sendMessage(resources.getMessages().getString("Messages.Error.Offline"));

                }

                return true;

            }

        }

        if (sender instanceof Player) {

            Player p = (Player) sender;

            if (args.length == 1) {

                if (args[0].equalsIgnoreCase("stats") && hasPermission(sender, "kp.command.stats")) {

                    for (String line : resources.getMessages().getStringList("Messages.Stats.Message")) {

                        p.sendMessage(addPlaceholdersIfPossible(p, Config.tr(line)));

                    }

                    XSound.play(p, "ENTITY_ITEM_PICKUP, 1, 1");

                } else if (args[0].equalsIgnoreCase("menu") && hasPermission(sender, "kp.command.menu")) {

                    arena.getMenus().getKitMenu().open(p);

                } else if (args[0].equalsIgnoreCase("spawn") && hasPermission(sender, "kp.command.spawn")) {

                    if (config.contains("Arenas." + p.getWorld().getName())) {

                        if (!spawnUsers.contains(p.getName())) {

                            spawnUsers.add(p.getName());

                            p.sendMessage(resources.getMessages().getString("Messages.Commands.Teleporting"));
                            XSound.play(p, "ENTITY_ITEM_PICKUP, 1, -1");

                            Location beforeLocation = p.getLocation();

                            new BukkitRunnable() {

                                public int time = Config.getI("Spawn.Time") + 1;

                                @Override
                                public void run() {

                                    time--;

                                    if (time != 0) {

                                        if (p.getGameMode() != GameMode.SPECTATOR) {

                                            p.sendMessage(resources.getMessages().getString("Messages.Commands.Time").replace("%time%", String.valueOf(time)));
                                            XSound.play(p, "BLOCK_NOTE_BLOCK_SNARE, 1, 1");

                                            if (beforeLocation.getBlockX() != p.getLocation().getBlockX() || beforeLocation.getBlockY() != p.getLocation().getBlockY() || beforeLocation.getBlockZ() != p.getLocation().getBlockZ()) {

                                                p.sendMessage(resources.getMessages().getString("Messages.Error.Moved"));
                                                spawnUsers.remove(p.getName());
                                                cancel();

                                            }

                                        } else {

                                            spawnUsers.remove(p.getName());
                                            cancel();

                                        }

                                    } else {

                                        p.sendMessage(resources.getMessages().getString("Messages.Commands.Teleport"));

                                        arena.toSpawn(p, p.getWorld().getName());

                                        if (Config.getB("Arena.ClearKitOnCommandSpawn")) {
                                            clearKit(p);
                                        }

                                        spawnUsers.remove(p.getName());

                                        XSound.play(p, "ENTITY_ENDERMAN_TELEPORT, 1, 1");

                                        cancel();

                                    }

                                }

                            }.runTaskTimer(plugin, 0L, 20L);

                        }

                    } else {

                        p.sendMessage(resources.getMessages().getString("Messages.Error.Arena"));

                    }

                } else if (args[0].equalsIgnoreCase("clear") && hasPermission(sender, "kp.command.clear")) {

                    clearKit(p);
                    p.sendMessage(resources.getMessages().getString("Messages.Commands.Cleared"));
                    return true;

                } else if (args[0].equalsIgnoreCase("addspawn") && hasPermission(p, "kp.command.addspawn")) {

                    String arenaName = p.getWorld().getName();
                    int spawnNumber = getNextArenaSpawnNumber(arenaName);

                    Toolkit.saveLocationToConfig(plugin, config, "Arenas." + arenaName + "." + spawnNumber, p.getLocation());

                    p.sendMessage(resources.getMessages().getString("Messages.Commands.Added")
                            .replace("%number%", String.valueOf(spawnNumber))
                            .replace("%arena%", arenaName));
                    XSound.play(p, "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1");

                    return true;

                } else if (args[0].equalsIgnoreCase("delarena") && hasPermission(sender, "kp.command.delarena")) {

                    String arenaName = p.getWorld().getName();

                    if (config.contains("Arenas." + arenaName)) {

                        config.set("Arenas." + arenaName, null);
                        plugin.saveConfig();

                        p.sendMessage(resources.getMessages().getString("Messages.Commands.Removed").replace("%arena%", arenaName));
                        XSound.play(p, "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1");

                    } else {

                        p.sendMessage(resources.getMessages().getString("Messages.Error.Arena"));

                    }

                    return true;

                }

            } else if (args.length == 2) {

                if (args[0].equalsIgnoreCase("arena") && hasPermission(sender, "kp.command.spawn")) {

                    String arenaName = args[1];

                    if (!arena.getKits().hasKit(p.getName())) {

                        arena.toSpawn(p, arenaName);

                    } else {

                        p.sendMessage(resources.getMessages().getString("Messages.Error.KitInvalid"));

                    }

                } else if (args[0].equalsIgnoreCase("preview") && hasPermission(sender, "kp.command.preview")) {

                    String kitName = args[1];

                    if (arena.getKits().isKit(kitName)) {

                        Kit kitToPreview = arena.getKits().getKitByName(kitName);
                        arena.getMenus().getPreviewMenu().open(p, kitToPreview);

                    } else {

                        p.sendMessage(resources.getMessages().getString("Messages.Error.Lost"));

                    }

                } else if (args[0].equalsIgnoreCase("create") && hasPermission(sender, "kp.command.create")) {

                    String kitName = args[1];

                    if (!arena.getKits().isKit(kitName)) {

                        Kit kitToCreate = arena.getKits().createKitFromPlayer(p, kitName);
                        Resource kitResource = new Resource(plugin, "kits/" + kitToCreate.getName() + ".yml");
                        kitToCreate.toResource(kitResource);

                        resources.addResource(kitToCreate.getName() + ".yml", kitResource);

                        p.sendMessage(resources.getMessages().getString("Messages.Commands.Create")
                                .replace("%kit%", kitName));

                    } else {

                        p.sendMessage(resources.getMessages().getString("Messages.Error.Exists"));

                    }

                } else if (args[0].equalsIgnoreCase("kit")/* && hasPermission(sender, "kp.command.kit")*/) {

                    if (Toolkit.inArena(p)) {

                        String kitName = args[1];
                        Kit kitToGive = arena.getKits().getKitByName(kitName);

                        arena.getKits().attemptToGiveKitToPlayer(p, kitToGive);

                    } else {

                        p.sendMessage(resources.getMessages().getString("Messages.Error.Location"));

                    }

                    return true;

                }

            }

        } else {

            sender.sendMessage(resources.getMessages().getString("Messages.General.Player"));

        }
        return false;

    }

    private boolean hasPermission(CommandSender sender, String permission) {

        if (sender.hasPermission(permission)) {
            return true;
        }
        sender.sendMessage(resources.getMessages().getString("Messages.General.Permission"));
        return false;

    }

    private void clearKit(Player p) {

        p.getInventory().setArmorContents(null);
        p.getInventory().clear();

        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }

        if (Config.getB("Arena.GiveItemsOnClear")) {
            arena.giveItems(p);
        }

        arena.getKits().resetKit(p.getName());

    }

    private String addPlaceholdersIfPossible(Player p, String text) {

        if (plugin.hasPlaceholderAPI()) {
            text = PlaceholderAPI.setPlaceholders(p, text);
        }

        text = text.replace("%player%", p.getName())
                .replace("%kills%", String.valueOf(arena.getStats().getKills(p.getUniqueId())))
                .replace("%deaths%", String.valueOf(arena.getStats().getDeaths(p.getUniqueId())))
                .replace("%level%", String.valueOf(arena.getLevels().getLevel(p.getUniqueId())))
                .replace("%experience%", String.valueOf(arena.getLevels().getExperience(p.getUniqueId())));

        return text;

    }

    private int getNextArenaSpawnNumber(String arenaName) {

        for (int i = 1; i < 1000; i++) {

            if (!config.contains("Arenas." + arenaName + "." + i)) {

                return i;

            }

        }

        return 1;

    }

}
