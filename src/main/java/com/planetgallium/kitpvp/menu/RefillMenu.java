package com.planetgallium.kitpvp.menu;

import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Menu;
import com.planetgallium.kitpvp.util.XMaterial;
import org.bukkit.entity.Player;

public class RefillMenu {

    private Menu menu;

    public RefillMenu() {
        create();
    }

    private void create() {

        this.menu = new Menu("Refill", null, 54);

        for (int i = 0; i < menu.getSize(); i++) {

            menu.addItem(Config.getS("Soups.Name"), XMaterial.MUSHROOM_STEW.parseMaterial().get(), Config.getC().getStringList("Soups.Lore"), i);

        }

    }

    public void open(Player p) {

        menu.openMenu(p);

    }

}
