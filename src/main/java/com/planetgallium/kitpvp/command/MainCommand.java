package com.planetgallium.kitpvp.command;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.menu.KitMenu;
import com.planetgallium.kitpvp.menu.PreviewMenu;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.XSound;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MainCommand implements CommandExecutor {

    private List<String> spawnUsers = new ArrayList<String>();

    private boolean isFirst = false;

    private Game game;
    private Arena arena;
    private FileConfiguration config;
    private Resources resources;

    public MainCommand(Game game) {
        this.game = game;
        this.arena = game.getArena();
        this.config = game.getConfig();
        this.resources = game.getResources();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 0) {

            sender.sendMessage(Config.tr("&7[&b&lKIT-PVP&7]"));
            sender.sendMessage(Config.tr("&7Version: &b" + Game.getInstance().getDescription().getVersion()));
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

                game.reloadConfig();
                resources.reload();

                sender.sendMessage(resources.getMessages().getString("Messages.Commands.Reload"));
                return true;

            } else if (args[0].equalsIgnoreCase("debug") && hasPermission(sender, "kp.command.debug")) {

                String names = "";

                for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                    names += plugin.getName() + " ";
                }

                sender.sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aServer Version: &7" + Bukkit.getBukkitVersion()) + " " + (Bukkit.getVersion().contains("Spigot") ? "(Spigot)" : "(Other)"));
                sender.sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aPlugin Version: " + game.getDescription().getVersion()) + " " + (game.needsUpdate() ? "&c(Requires Update)" : "&a(Latest Version)"));
                sender.sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aSpawn Set: " + (config.contains("Arenas") ? "&aConfigured" : "&cUnconfigured")));
                sender.sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aSupport Discord: &7https://discord.gg/GtXQKZ6"));
                sender.sendMessage(Config.tr("&7[&b&lKIT-PVP&7] &aPlugin List: &7" + names));

            } else if (args[0].equalsIgnoreCase("import") && hasPermission(sender, "kp.command.import")) {

                if (game.getDatabase().isEnabled()) {

                    sender.sendMessage(Config.tr("%prefix% &7Importing data, please wait..."));

                    game.getDatabase().importData(resources);

                    sender.sendMessage(Config.tr("%prefix% &aDatabase data has successfully been exported to the stats.yml."));

                } else {

                    sender.sendMessage(Config.tr("%prefix% &cImporting is unnecessary, you can switch your storage type and import."));

                }

                return true;

            } else if (args[0].equalsIgnoreCase("export") && hasPermission(sender, "kp.command.export")) {

                if (game.getDatabase().isEnabled()) {

                    sender.sendMessage(Config.tr("%prefix% &7Exporting data, please wait..."));

                    game.getDatabase().exportData(resources);

                    sender.sendMessage(Config.tr("%prefix% &aStats successfully exported to database."));

                } else {

                    sender.sendMessage(Config.tr("%prefix% &cYou are using YAML storage, so exporting is not possible. Change your settings in the config.yml."));

                }

                return true;

            } else if (args[0].equalsIgnoreCase("kits") && hasPermission(sender, "kp.command.kits")) {

                String message = "";

                for (String kitName : arena.getKits().getList()) {

                    String[] fullName = kitName.split(".ym", 2);
                    fullName[0] = fullName[0].trim();

                    if (arena.getKits().getList().size() == 1) {

                        message += fullName[0];

                    } else {

                        if (!isFirst) {
                            isFirst = true;
                            message += fullName[0];
                        } else {
                            message += ", " + fullName[0];
                        }

                    }

                }

                sender.sendMessage(resources.getMessages().getString("Messages.Commands.Kits").replace("%kits%", message));
                isFirst = false;

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

            }

        } else if (args.length == 3) {

            if (args[0].equalsIgnoreCase("kit") && hasPermission(sender, "kp.command.kit.other")) {

                String kitName = args[1];
                String playerName = args[2];

                Player target = Bukkit.getPlayer(playerName);

                if (target != null && Toolkit.inArena(target)) {

                    if (!arena.getKits().hasKit(target.getName())) {

                        arena.getKits().giveKit(kitName, target);
                        sender.sendMessage(resources.getMessages().getString("Messages.Commands.KitOther").replace("%player%", playerName).replace("%kit%", kitName));

                    } else {

                        sender.sendMessage(resources.getMessages().getString("Messages.Error.SelectedOther").replace("%kit%", kitName));

                    }

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

                    for (String msg : resources.getMessages().getStringList("Messages.Stats.Message")) {

                        p.sendMessage(addPlaceholdersIfPossible(p, Config.tr(msg)));

                    }

                    XSound.playSoundFromString(p, "ENTITY_ITEM_PICKUP, 1, 1");

                } else if (args[0].equalsIgnoreCase("menu") && hasPermission(sender, "kp.command.menu")) {

                    KitMenu menu = new KitMenu(resources);
                    menu.create(p);

                } else if (args[0].equalsIgnoreCase("spawn") && hasPermission(sender, "kp.command.spawn")) {

                    if (config.contains("Arenas." + p.getWorld().getName())) {

                        if (!spawnUsers.contains(p.getName())) {

                            spawnUsers.add(p.getName());

                            p.sendMessage(resources.getMessages().getString("Messages.Commands.Teleporting"));
                            XSound.playSoundFromString(p, "ENTITY_ITEM_PICKUP, 1, -1");

                            Location beforeLocation = p.getLocation();

                            new BukkitRunnable() {

                                public int t = Config.getI("Spawn.Time") + 1;

                                @Override
                                public void run() {

                                    t--;

                                    if (t != 0) {

                                        if (p.getGameMode() != GameMode.SPECTATOR) {

                                            p.sendMessage(resources.getMessages().getString("Messages.Commands.Time").replace("%time%", String.valueOf(t)));
                                            XSound.playSoundFromString(p, "BLOCK_NOTE_BLOCK_SNARE, 1, 1");

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

                                        arena.toSpawn(p);

                                        if (Config.getB("Arena.ClearKitOnCommandSpawn")) {

                                            clearKit(p);

                                        }

                                        spawnUsers.remove(p.getName());

                                        XSound.playSoundFromString(p, "ENTITY_ENDERMAN_TELEPORT, 1, 1");

                                        cancel();

                                    }

                                }

                            }.runTaskTimer(game, 0L, 20L);

                        }

                    } else {

                        p.sendMessage(resources.getMessages().getString("Messages.Error.Arena"));

                    }

                } else if (args[0].equalsIgnoreCase("clear") && hasPermission(sender, "kp.command.clear")) {

                    clearKit(p);

                    p.sendMessage(resources.getMessages().getString("Messages.Commands.Cleared"));

                } else if (args[0].equalsIgnoreCase("addspawn") && hasPermission(p, "kp.command.addspawn")) {

                    String arenaName = p.getWorld().getName();
                    int spawnNumber = getNextArenaSpawnNumber(arenaName);

                    Toolkit.saveLocationToConfig(game, Config.getC(), "Arenas." + arenaName + "." + spawnNumber, p.getLocation());

                    p.sendMessage(resources.getMessages().getString("Messages.Commands.Added").replace("%number%", String.valueOf(spawnNumber)).replace("%arena%", arenaName));
                    XSound.playSoundFromString(p, "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1");

                    return true;

                } else if (args[0].equalsIgnoreCase("delarena") && hasPermission(sender, "kp.command.delarena")) {

                    String arenaName = p.getWorld().getName();

                    if (config.contains("Arenas." + arenaName)) {

                        config.set("Arenas." + arenaName, null);
                        game.saveConfig();

                        p.sendMessage(resources.getMessages().getString("Messages.Commands.Removed").replace("%arena%", arenaName));
                        XSound.playSoundFromString(p, "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1");

                    } else {

                        p.sendMessage(resources.getMessages().getString("Messages.Error.Arena"));

                    }

                    return true;

                }

            } else if (args.length == 2) {

                if (args[0].equalsIgnoreCase("arena") && hasPermission(sender, "kp.command.spawn")) {

                    String arenaName = args[1];

                    if (config.contains("Arenas.Spawn." + arenaName)) {

                        if (!game.getArena().getKits().hasKit(p.getName())) {

                            Location spawn = Toolkit.getLocationFromConfig(config, "Arenas." + arenaName + "." + arena.generateRandomArenaSpawn(arenaName));

                            p.teleport(spawn);
                            p.sendMessage(resources.getMessages().getString("Messages.Commands.Teleport"));

                        } else {

                            p.sendMessage(resources.getMessages().getString("Messages.Error.KitInvalid"));

                        }

                    } else {

                        p.sendMessage(resources.getMessages().getString("Messages.Error.ExistsArena"));

                    }

                } else if (args[0].equalsIgnoreCase("preview") && hasPermission(sender, "kp.command.preview")) {

                    if (Toolkit.inArena(p)) {

                        if (arena.getKits().isKit(args[1])) {

                            PreviewMenu preview = new PreviewMenu(args[1], resources.getKits(args[1]));
                            preview.open(p);

                        } else {

                            p.sendMessage(resources.getMessages().getString("Messages.Error.Lost"));

                        }

                    } else {

                        p.sendMessage(resources.getMessages().getString("Messages.Error.Location"));

                    }

                } else if (args[0].equalsIgnoreCase("create") && hasPermission(sender, "kp.command.create")) {

                    if (Toolkit.inArena(p)) {

                        arena.getKits().createKit(args[1], p);

                    } else {

                        p.sendMessage(resources.getMessages().getString("Messages.Error.Location"));

                    }

                } else if (args[0].equalsIgnoreCase("delete") && hasPermission(sender, "kp.command.delete")) {

                    if (Toolkit.inArena(p)) {

                        if (arena.getKits().getList().contains(args[1] + ".yml")) {

                            resources.removeKit(args[1]);
                            p.sendMessage(resources.getMessages().getString("Messages.Commands.Delete").replace("%kit%", args[1]));

                        } else {

                            p.sendMessage(resources.getMessages().getString("Messages.Error.Lost"));

                        }

                    } else {

                        p.sendMessage(resources.getMessages().getString("Messages.Error.Location"));

                    }

                } else if (args[0].equalsIgnoreCase("kit") && hasPermission(sender, "kp.command.kit")) {

                    if (Toolkit.inArena(p)) {

                        if (!arena.getKits().hasKit(p.getName())) {

                            arena.getKits().giveKit(args[1], p);

                        } else {

                            p.sendMessage(resources.getMessages().getString("Messages.Error.Selected"));
                            p.playSound(p.getLocation(), XSound.ENTITY_ENDER_DRAGON_HURT.parseSound(), 1, 1);

                        }

                    } else {

                        p.sendMessage(resources.getMessages().getString("Messages.Error.Location"));

                    }

                    return true;

                }

            }

        } else {

            sender.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Player")));

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

        arena.getKits().clearKit(p.getName());

    }

    private String addPlaceholdersIfPossible(Player p, String text) {

        if (game.hasPlaceholderAPI()) {
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
