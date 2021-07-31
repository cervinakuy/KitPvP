package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.menu.KitHolder;
import com.planetgallium.kitpvp.menu.KitMenu;
import com.planetgallium.kitpvp.menu.PreviewHolder;
import com.planetgallium.kitpvp.menu.PreviewMenu;
import com.planetgallium.kitpvp.util.CacheManager;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Toolkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class MenuListener implements Listener {

    private final Arena arena;
    private final Resource config;
    private final Resource menuConfig;

    public MenuListener(Game plugin) {
        this.arena = plugin.getArena();
        this.config = plugin.getResources().getConfig();
        this.menuConfig = plugin.getResources().getMenu();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

//        System.out.println("sldfjsdkfjk j");

        if (e.getClickedInventory() != null) {

//            System.out.println("here");

            Player p = (Player) e.getWhoClicked();

            Inventory originInventory = e.getClickedInventory();
            Inventory clickedInventory = e.getInventory();

            // to prevent adding items into the inventory
            if (originInventory.getType() == InventoryType.PLAYER && clickedInventory.getType() == InventoryType.CHEST) {
                if (clickedInventory.getHolder() instanceof KitHolder ||
                        clickedInventory.getHolder() instanceof PreviewHolder) {
                    e.setCancelled(true);
                    return;
                }
            }

            if (e.getClickedInventory().getHolder() instanceof KitHolder) {

                Inventory openMenu = e.getClickedInventory();
                String itemPath = "Menu.Items." + e.getSlot();

                if (menuConfig.contains(itemPath)) {

                    if (openMenu.getItem(e.getSlot()) != null) {

                        e.setCancelled(true);
                        p.closeInventory();

                        String clickType = e.getClick() == ClickType.LEFT ? "Left-Click" : "Right-Click";

                        if (menuConfig.contains(itemPath + ".Commands")) {

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
                            Toolkit.runCommands(p, config.getStringList("PreviewMenuBackArrowCommands"), "none", "none");
                        }

                    }.runTaskLater(Game.getInstance(), 1L);

                } else {

                    e.setCancelled(true);

                }

            }

        }

    }

    @EventHandler
    public void onThing(PlayerDropItemEvent e) {

        //System.out.println("Tesdfjksdfk");

    }

}
