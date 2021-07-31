package com.planetgallium.kitpvp.game;

import com.planetgallium.kitpvp.menu.KitMenu;
import com.planetgallium.kitpvp.menu.PreviewMenu;
import com.planetgallium.kitpvp.menu.RefillMenu;
import com.planetgallium.kitpvp.util.Resources;

public class Menus {

    private final KitMenu kitMenu;
    private final PreviewMenu previewMenu;
    private final RefillMenu refillMenu;

    public Menus(Resources resources) {
        this.kitMenu = new KitMenu(resources);
        this.previewMenu = new PreviewMenu();
        this.refillMenu = new RefillMenu(resources);
    }

    public KitMenu getKitMenu() { return kitMenu; }

    public PreviewMenu getPreviewMenu() { return previewMenu; }

    public RefillMenu getRefillMenu() { return refillMenu; }

}
