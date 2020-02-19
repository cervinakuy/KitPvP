package com.planetgallium.kitpvp.kit;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.item.EffectItem;
import com.planetgallium.kitpvp.item.KitItem;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.XSound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kit {

    private String name;

    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack fill;

    private List<PotionEffect> potions = new ArrayList<PotionEffect>();
    private Map<Integer, ItemStack> inventory = new HashMap<Integer, ItemStack>();

    public void setName(String name) {

        this.name = name;

    }

    public void setHelmet(KitItem helmet) {

        this.helmet = helmet.toItemStack();

    }

    public void setChestplate(KitItem chestplate) {

        this.chestplate = chestplate.toItemStack();

    }

    public void setLeggings(KitItem leggings) {

        this.leggings = leggings.toItemStack();

    }

    public void setBoots(KitItem boots) {

        this.boots = boots.toItemStack();

    }

    public void setFill(KitItem fill) {

        this.fill = fill.toItemStack();

    }

    public void addEffect(EffectItem effect) {

        potions.add(effect.toPotionEffect());

    }

    public void addItem(KitItem item, int slot) {

        inventory.put(slot, item.toItemStack());

    }

    public void applyKit(Player p) {

        if (Config.getB("Arena.ClearInventoryOnKit")) {
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
        }

        if (helmet != null) p.getInventory().setHelmet(helmet);
        if (chestplate != null) p.getInventory().setChestplate(chestplate);
        if (leggings != null) p.getInventory().setLeggings(leggings);
        if (boots != null) p.getInventory().setBoots(boots);

        for (PotionEffect effect : potions) {

            p.addPotionEffect(effect);

        }

        for (int i = 0; i < 36; i++) {

            if (inventory.get(i) != null) {

                p.getInventory().setItem(i, inventory.get(i));

            }

        }

        for (int i = 0; i < 36; i++) {

            if (p.getInventory().getItem(i) == null) {

                p.getInventory().setItem(i, fill);

            }

        }

        p.sendMessage(Config.tr(Game.getInstance().getResources().getMessages().getString("Messages.Commands.Kit").replace("%kit%", getName())));
        p.playSound(p.getLocation(), XSound.ENTITY_HORSE_ARMOR.parseSound(), 1, 1);

    }

    public String getName() {
        return name;
    }

}
