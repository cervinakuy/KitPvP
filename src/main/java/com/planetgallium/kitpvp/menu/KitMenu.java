package com.planetgallium.kitpvp.menu;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Menu;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.XMaterial;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class KitMenu implements Listener {

    private Menu menu;
    private Resources resources;

    public KitMenu(Resources resources) {
        this.resources = resources;
    }

    public void create(Player p) {

        menu = new Menu(Config.tr(resources.getMenu().getString("Menu.General.Title")), new KitHolder(), resources.getMenu().getInt("Menu.General.Size"));

        ConfigurationSection section = resources.getMenu().getConfigurationSection("Menu.Items");

        for (String identifier : section.getKeys(false)) {

            if (!resources.getMenu().getBoolean("Menu.General.Hide-Items-With-No-Permission")) {

                menu.addItem(resources.getMenu().getString("Menu.Items." + identifier + ".Name"),
                        XMaterial.matchXMaterial(resources.getMenu().getString("Menu.Items." + identifier + ".Item").toUpperCase()).get().parseMaterial(),
                        resources.getMenu().getStringList("Menu.Items." + identifier + ".Lore"),
                        resources.getMenu().getInt("Menu.Items." + identifier + ".Slot"));

            } else {

                if (resources.getMenu().contains("Menu.Items." + identifier + ".View-Permission")) {

                    if (p.hasPermission(resources.getMenu().getString("Menu.Items." + identifier + ".View-Permission"))) {

                        menu.addItem(resources.getMenu().getString("Menu.Items." + identifier + ".Name"),
                                XMaterial.matchXMaterial(resources.getMenu().getString("Menu.Items." + identifier + ".Item").toUpperCase()).get().parseMaterial(),
                                resources.getMenu().getStringList("Menu.Items." + identifier + ".Lore"),
                                resources.getMenu().getInt("Menu.Items." + identifier + ".Slot"));

                    }

                }

            }

        }

        menu.openMenu(p);

    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (e.getClickedInventory() != null) {

            if (e.getClickedInventory().getHolder() instanceof KitHolder) {

                Player p = (Player) e.getWhoClicked();

                ConfigurationSection section = resources.getMenu().getConfigurationSection("Menu.Items");

                for (String identifier : section.getKeys(false)) {

                    if (resources.getMenu().getInt("Menu.Items." + identifier + ".Slot") == e.getSlot()) {

                        if (!resources.getMenu().getBoolean("Menu.General.Hide-Items-With-No-Permission")) {

                            if (e.getClick() == ClickType.LEFT) {

                                e.setCancelled(true);
                                p.closeInventory();

                                runCommands(p, "Menu.Items." + identifier + ".Commands.Enabled", "Menu.Items." + identifier + ".Commands.Left-Click");

                            } else {

                                e.setCancelled(true);
                                p.closeInventory();

                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        runCommands(p, "Menu.Items." + identifier + ".Commands.Enabled", "Menu.Items." + identifier + ".Commands.Right-Click");
                                    }
                                }.runTaskLater(Game.getInstance(), 1L);

                            }

                        } else {

                            if (resources.getMenu().getString("Menu.Items." + identifier + ".View-Permission") != null && p.hasPermission(resources.getMenu().getString("Menu.Items." + identifier + ".View-Permission"))) {

                                if (e.getClick() == ClickType.LEFT) {

                                    e.setCancelled(true);
                                    p.closeInventory();

                                    runCommands(p, "Menu.Items." + identifier + ".Commands.Enabled", "Menu.Items." + identifier + ".Commands.Left-Click");

                                } else {

                                    e.setCancelled(true);
                                    p.closeInventory();

                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            runCommands(p, "Menu.Items." + identifier + ".Commands.Enabled", "Menu.Items." + identifier + ".Commands.Right-Click");
                                        }
                                    }.runTaskLater(Game.getInstance(), 1L);

                                }

                            }

                        }

                        break;

                    }

                }

            } else if (e.getClickedInventory().getHolder() instanceof PreviewHolder) {

                Player p = (Player) e.getWhoClicked();

                if (e.getSlot() == 8) {

                    p.closeInventory();

                    new BukkitRunnable() {

                        @Override
                        public void run() {

                            create(p);

                        }

                    }.runTaskLater(Game.getInstance(), 1L);

                } else {

                    e.setCancelled(true);

                }

            }

        }

    }

    private void runCommands(Player p, String enablePath, String commandPath) {

        if (resources.getMenu().getBoolean(enablePath)) {

            for (String list : resources.getMenu().getStringList(commandPath)) {

                String[] command = list.split(":", 2);
                command[1] = command[1].trim();

                if (command[0].equals("console")) {

                    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {

                        String withPlaceholders = PlaceholderAPI.setPlaceholders(p, command[1].replace("%player%", p.getName()));
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), withPlaceholders);

                    } else {

                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command[1].trim().replace("%player%", p.getName()));

                    }

                } else if (command[0].equals("player")) {

                    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {

                        String withPlaceholders = PlaceholderAPI.setPlaceholders(p, command[1].trim().replace("%player%", p.getName()));
                        p.performCommand(withPlaceholders);

                    } else {

                        p.performCommand(command[1].replace("%player%", p.getName()));

                    }

                }

            }

        }

    }

}
