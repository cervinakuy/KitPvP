package com.planetgallium.kitpvp.menu;

import com.planetgallium.kitpvp.util.Menu;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class RefillMenu {

    private final Resources resources;
    private Menu menu;

    public RefillMenu(Resources resources) {
        this.resources = resources;
    }

    private void create() {
        this.menu = new Menu("Refill", null, 54);

        for (int i = 0; i < menu.getSize(); i++) {
            menu.addItem(resources.getConfig().fetchString("Soups.Name"),
                    Toolkit.safeMaterial("MUSHROOM_STEW"),
                    resources.getConfig().getStringList("Soups.Lore"), i);
        }
    }

    public void open(Player p) {
        create();
        menu.openMenu(p);
    }

}
