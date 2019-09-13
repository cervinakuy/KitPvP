package com.planetgallium.kitpvp.api.Objects.KitManager;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Kits;
import com.planetgallium.kitpvp.util.Resource;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Inventory {

    private String name;

    private Resource kitResource;

    private Kits kits;

    private Armor armor;

    public Inventory(String name) {
        this.name = name;
        this.kitResource = Game.getInstance().getResources().getKits(name);
        this.kits = new Kits(Game.getInstance(), Game.getInstance().getResources());
        this.armor = new Armor(name);
    }

    public void setSlot(int slot, ItemStack item) {
        kits.saveItem(kitResource, name, "Inventory.Items." + slot, item, "&fItem");
    }

    public ItemStack getSlot(int slot) {
        String path = "Inventory.Items." + slot;
        ItemStack item = new ItemStack(Material.valueOf(kitResource.getString(path + ".Item")));
        ItemMeta itemMeta = item.getItemMeta();
        if (kitResource.isSet(path + ".Name")) {
            itemMeta.setDisplayName(kitResource.getString(path + ".Name"));
        }
        if (kitResource.isSet(path + ".Amount")) {
            item.setAmount(kitResource.getInt(path + ".Amount"));
        }
        item.setItemMeta(itemMeta);
        return item;
    }

    public Armor getArmor() { return armor; }

    public void setArmor(Armor armor) { this.armor = armor; }
}
