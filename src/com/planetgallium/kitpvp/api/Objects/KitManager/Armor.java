package com.planetgallium.kitpvp.api.Objects.KitManager;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Kits;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Armor {

    private Game game;

    private ItemStack helmet;

    private ItemStack chestplate;

    private ItemStack leggings;

    private ItemStack boots;

    private Kits kits;

    private String kit;

    public Armor(String kit) {
        game = Game.getInstance();
        kits = game.getArena().getKits();
        helmet = new ItemStack(Material.valueOf(game.getResources().getKits(kit).getString("Inventory.Armor.Helmet.Item")));
        helmet.setAmount(game.getResources().getKits(kit).getInt("Inventory.Armor.Helmet.Amount"));
        ItemMeta helmetMeta = helmet.getItemMeta();
        helmetMeta.setDisplayName(game.getResources().getKits(kit).getString("Inventory.Armor.Helmet.Name"));
        helmetMeta.setLore(game.getResources().getKits(kit).getStringList("Inventory.Armor.Helmet.Lore"));
        helmet.setItemMeta(helmetMeta);
        chestplate = new ItemStack(Material.valueOf(game.getResources().getKits(kit).getString("Inventory.Armor.Chestplate.Item")));
        chestplate.setAmount(game.getResources().getKits(kit).getInt("Inventory.Armor.Chestplate.Amount"));
        ItemMeta chestplateMeta = chestplate.getItemMeta();
        chestplateMeta.setDisplayName(game.getResources().getKits(kit).getString("Inventory.Armor.Chestplate.Name"));
        chestplateMeta.setLore(game.getResources().getKits(kit).getStringList("Inventory.Armor.Chestplate.Lore"));
        chestplate.setItemMeta(chestplateMeta);
        leggings = new ItemStack(Material.valueOf(game.getResources().getKits(kit).getString("Inventory.Armor.Leggings.Item")));
        leggings.setAmount(game.getResources().getKits(kit).getInt("Inventory.Armor.Leggings.Amount"));
        ItemMeta leggingsMeta = leggings.getItemMeta();
        leggingsMeta.setDisplayName(game.getResources().getKits(kit).getString("Inventory.Armor.Leggings.Name"));
        leggingsMeta.setLore(game.getResources().getKits(kit).getStringList("Inventory.Armor.Leggings.Lore"));
        leggings.setItemMeta(leggingsMeta);
        boots = new ItemStack(Material.valueOf(game.getResources().getKits(kit).getString("Inventory.Armor.Boots.Item")));
        boots.setAmount(game.getResources().getKits(kit).getInt("Inventory.Armor.Boots.Amount"));
        ItemMeta bootsMeta = boots.getItemMeta();
        bootsMeta.setDisplayName(game.getResources().getKits(kit).getString("Inventory.Armor.Boots.Name"));
        bootsMeta.setLore(game.getResources().getKits(kit).getStringList("Inventory.Armor.Boots.Lore"));
        boots.setItemMeta(bootsMeta);
        this.kit = kit;
    }

    public ItemStack getHelmet() { return helmet; }

    public ItemStack getChestplate() { return chestplate; }

    public ItemStack getLeggings() { return leggings; }

    public ItemStack getBoots() { return boots; }

    public void setHelmet(ItemStack helmet) {
        this.helmet = helmet;
        kits.saveItem(game.getResources().getKits(kit), kit, "Inventory.Armor.Helmet", helmet, "&fHelmet");
    }

    public void setChestplate(ItemStack chestplate) {
        this.chestplate = chestplate;
        kits.saveItem(game.getResources().getKits(kit), kit, "Inventory.Armor.Chestplate", chestplate, "&fChestplate");
    }

    public void setLeggings(ItemStack leggings) {
        this.leggings = leggings;
        kits.saveItem(game.getResources().getKits(kit), kit, "Inventory.Armor.Leggings", leggings, "&fLeggings");
    }

    public void setBoots(ItemStack boots) {
        this.boots = boots;
        kits.saveItem(game.getResources().getKits(kit), kit, "Inventory.Armor.Boots", boots, "&fBoots");
    }

    public boolean hasHelmet() { return game.getResources().getKits(kit).isSet("Inventory.Armor.Helmet"); }

    public boolean hasChestplate() {
        return game.getResources().getKits(kit).isSet("Inventory.Armor.Chestplate");
    }

    public boolean hasLeggings() { return game.getResources().getKits(kit).isSet("Inventory.Armor.Leggings"); }

    public boolean hasBoots() { return game.getResources().getKits(kit).isSet("Inventory.Armor.Boots"); }

}
