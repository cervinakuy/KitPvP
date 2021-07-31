package com.planetgallium.kitpvp.menu;

import com.cryptomorin.xseries.XMaterial;
import com.planetgallium.kitpvp.util.Menu;
import com.planetgallium.kitpvp.util.Resources;
import org.bukkit.entity.Player;

public class RefillMenu {

    private Resources resources;
    private Menu menu;

    public RefillMenu(Resources resources) {
        this.resources = resources;
    }

    private void create() {

        this.menu = new Menu("Refill", null, 54);

        for (int i = 0; i < menu.getSize(); i++) {
            menu.addItem(resources.getConfig().getString("Soups.Name"), XMaterial.MUSHROOM_STEW.parseMaterial(), resources.getConfig().getStringList("Soups.Lore"), i);
        }

    }

    public void open(Player p) {

        create();
        menu.openMenu(p);

    }

}
