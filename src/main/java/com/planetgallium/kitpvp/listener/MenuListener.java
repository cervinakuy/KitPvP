package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.menu.KitHolder;
import com.planetgallium.kitpvp.menu.PreviewHolder;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class MenuListener implements Listener {

    private Arena arena;
    private FileConfiguration menuConfig;

    public MenuListener(Arena arena, Resources resources) {
        this.arena = arena;
        this.menuConfig = resources.getMenu();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (e.getClickedInventory() != null) {

            Player p = (Player) e.getWhoClicked();

            if (e.getClickedInventory().getHolder() instanceof KitHolder) {

                Inventory openMenu = e.getClickedInventory();
                String itemPath = "Menu.Items." + e.getSlot();

                if (menuConfig.contains(itemPath)) {

                    if (openMenu.getItem(e.getSlot()) != null) {

                        e.setCancelled(true);
                        p.closeInventory();

                        String clickType = e.getClick() == ClickType.LEFT ? "Left-Click" : "Right-Click";

                        if (menuConfig.getBoolean(itemPath + ".Commands.Enabled")) {

                            new BukkitRunnable() {

                                @Override
                                public void run() {
                                    Toolkit.runCommands(p, menuConfig.getStringList(itemPath + ".Commands." + clickType), "none", "none");
                                }

                            }.runTaskLater(Game.getInstance(), 1L);

                        }

                    }

                }

            } else if (e.getClickedInventory().getHolder() instanceof PreviewHolder) {

                if (e.getSlot() == 8) {

                    p.closeInventory();

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            arena.getMenus().getKitMenu().open(p);
                        }

                    }.runTaskLater(Game.getInstance(), 1L);

                } else {

                    e.setCancelled(true);

                }

            }

        }

    }

}
