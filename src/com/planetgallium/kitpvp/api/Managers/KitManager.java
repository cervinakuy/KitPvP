package com.planetgallium.kitpvp.api.Managers;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.Objects.KitManager.Ability;
import com.planetgallium.kitpvp.api.Objects.KitManager.Inventory;
import com.planetgallium.kitpvp.api.Objects.KitManager.Potion;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.game.Kits;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;

public class KitManager {

    private Game game;

    private Arena arena;

    private Resources resources;

    private Kits kits;

    private Ability ability;

    public HashMap<String, Potion> potionCache;

    public KitManager() {
        this.game = Game.getInstance();
        this.arena = game.getArena();
        this.resources = game.getResources();
        this.kits = new Kits(game, game.getResources());
        this.potionCache = new HashMap<>();
    }

    public void createKit(String kit) {
        Resource kitResource = resources.getKits(kit);
        if (!isKit(kit)) {
            resources.addKit(kit);
            kitResource.set("Kit.Permission", "kp.kit." + kit.toLowerCase());
            kitResource.set("Kit.Level", 0);
            kitResource.set("Kit.Cooldown", 0);
            kitResource.save();
        } else {
            Bukkit.getConsoleSender().sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Exists").
                    replace("%prefix%", resources.getMessages().getString("Messages.General.Prefix"))));
        }

        if (getInventory(kit).getArmor().hasHelmet()) {
            kits.saveItem(kitResource, kit, "Inventory.Armor.Helmet", getInventory(kit).getArmor().getHelmet(), "&fHelmet");
        }

        if (getInventory(kit).getArmor().hasChestplate()) {
            kits.saveItem(kitResource, kit, "Inventory.Armor.Chestplate", getInventory(kit).getArmor().getChestplate(), "&fChestplate");
        }

        if (getInventory(kit).getArmor().hasLeggings()) {
            kits.saveItem(kitResource, kit, "Inventory.Armor.Leggings", getInventory(kit).getArmor().getLeggings(), "&fLeggings");
        }

        if (getInventory(kit).getArmor().hasBoots()) {
            kits.saveItem(kitResource, kit, "Inventory.Armor.Boots", getInventory(kit).getArmor().getBoots(), "&fBoots");
        }

        for (int i = 0; i < 36; i++) {

            ItemStack item = getInventory(kit).getSlot(i);

            if (item != null) {

                String backupName = (item.getType() == XMaterial.MUSHROOM_STEW.parseMaterial()) ? Config.getS("Soups.Name") : null;
                kits.saveItem(kitResource, kit, "Inventory.Items." + i, getInventory(kit).getSlot(i), backupName);

            }

        }
    }

    public void createKitInGame(String kit, Player player) {
        kits.createKit(kit, player);
    }

    public void giveKit(String kit, Player player) {
        kits.giveKit(kit, player);
    }

    public void clearKit(Player p) {
        p.getInventory().setArmorContents(null);
        p.getInventory().clear();

        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }

        if (Config.getB("Arena.GiveItemsOnClear")) {
            arena.giveItems(p);
        }

        arena.getKits().clearKit(p.getName());

        p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Commands.Cleared")));

    }

    public boolean isKit(String kit) { return kits.isKit(kit); }

    public Inventory getInventory(String kit) { return new Inventory(kit); }

    public boolean hasPotions(String kit) { return resources.getKits(kit).isSet("Potions"); }

    public HashMap<String, Potion> getPotions(String kit) {
        if (resources.getKits(kit).isSet("Potions")) {
            ConfigurationSection section = resources.getKits(kit).getConfigurationSection("Potions");
            for (String s: section.getKeys(false)) {
                potionCache.put(s, new Potion(resources.getKits(kit).getInt("Potions" + s + ".Level"),
                        resources.getKits(kit).getInt("Potions" + s + ".Duration")));
            }
            HashMap<String, Potion> potions = potionCache;
            potionCache.clear();
            return potions;
        } else {
            return null;
        }
    }

    public void setPotions(String kit, HashMap<String, Potion> potions) {
        potionCache = potions;
        for (String s: potionCache.keySet()) {
            resources.getKits(kit).set("Potions." + s + ".Level", potionCache.get(s).getLevel());
            resources.getKits(kit).set("Potions." + s + ".Duration", potionCache.get(s).getDuration());
        }
        resources.getKits(kit).save();
    }

    public Ability getAbility(String kit) { return new Ability(kit); }

    public String getPermission(String kit) { return resources.getKits(kit).getString("Kit.Permission"); }

    public int getCooldown(String kit) { return resources.getKits(kit).getInt("Kit.Cooldown"); }

    public int getLevel(String kit) { return resources.getKits(kit).getInt("Kit.Level"); }

    public void setPermission(String kit, String permission) { resources.getKits(kit).set("Kit.Permission", permission); }

    public void setCooldown(String kit, int cooldown) { resources.getKits(kit).set("Kit.Cooldown", cooldown); }

    public void setLevel(String kit, int level) { resources.getKits(kit).set("Kit.Level", level); }
}
