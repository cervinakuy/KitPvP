package com.planetgallium.kitpvp.api;

import com.planetgallium.kitpvp.item.AttributeWriter;
import com.planetgallium.kitpvp.util.Cooldown;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Toolkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kit {

    private final String name;
    private String permission;
    private Cooldown cooldown;
    private int level;
    private int maxHealth;

    private final Map<String, Object> options;
    private final Map<Integer, ItemStack> inventory;
    private final List<PotionEffect> effects;

    private ItemStack kitHelmet;
    private ItemStack kitChestplate;
    private ItemStack kitLeggings;
    private ItemStack kitBoots;
    private ItemStack offhand;
    private ItemStack fill;

    public Kit(String name) {
        this.name = name;
        this.options = new HashMap<>();
        this.inventory = new HashMap<>();
        this.effects = new ArrayList<>();
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setCooldown(Cooldown cooldown) {
        this.cooldown = cooldown;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setMaxHealth(int health) {
        this.maxHealth = health;
    }

    public void setOption(String key, Object value) {
        // NOTE: using this strategy to pass data rather than using Resources class so
        // kit class can be used externally without needing access to Resources
        this.options.put(key, value);
    }

    public void setInventoryItem(int slot, ItemStack item) {
        inventory.put(slot, item);
    }

    public void addEffect(PotionEffectType type, int amplifierNonZeroBased, int durationSeconds) {
        PotionEffect effect = new PotionEffect(type, durationSeconds * 20, amplifierNonZeroBased - 1);
        effects.add(effect);
    }

    public void setHelmet(ItemStack helmet) {
        this.kitHelmet = helmet;
    }

    public void setChestplate(ItemStack chestplate) {
        this.kitChestplate = chestplate;
    }

    public void setLeggings(ItemStack leggings) {
        this.kitLeggings = leggings;
    }

    public void setBoots(ItemStack boots) {
        this.kitBoots = boots;
    }

    public void setOffhand(ItemStack offhand) {
        this.offhand = offhand;
    }

    public void setFill(ItemStack fill) {
        this.fill = fill;
    }

    public void apply(Player player) {
        List<ItemStack> overflowItems = new ArrayList<>();
        boolean addOverflowItems = (Boolean) options.get("AddOverflowItemsOnKit");

        if (kitHelmet != null) {
            if (player.getInventory().getHelmet() == null) {
                player.getInventory().setHelmet(kitHelmet);
            } else {
                overflowItems.add(kitHelmet);
            }
        }

        if (kitChestplate != null) {
            if (player.getInventory().getChestplate() == null) {
                player.getInventory().setChestplate(kitChestplate);
            } else {
                overflowItems.add(kitChestplate);
            }
        }

        if (kitLeggings != null) {
            if (player.getInventory().getLeggings() == null) {
                player.getInventory().setLeggings(kitLeggings);
            } else {
                overflowItems.add(kitLeggings);
            }
        }

        if (kitBoots != null) {
            if (player.getInventory().getBoots() == null) {
                player.getInventory().setBoots(kitBoots);
            } else {
                overflowItems.add(kitBoots);
            }
        }

        Toolkit.setMaxHealth(player, maxHealth);

        for (int i = 0; i < 36; i++) {
            if (addOverflowItems) {
                // if kit wants to put an item in slot i, but slot i in player inventory is already taken
                if (inventory.containsKey(i) && player.getInventory().getItem(i) != null) {
                    overflowItems.add(inventory.get(i)); // add kit item to overflow items
                    continue; // ignore this kit item, will be accounted for with giveOverflowItems
                }
            }

            if (inventory.get(i) != null) {
                player.getInventory().setItem(i, inventory.get(i));
            }
        }

        if (offhand != null && Toolkit.versionToNumber() >= 19) {
            player.getInventory().setItemInOffHand(offhand);
        }

        if (addOverflowItems) {
            giveOverflowItems(player, overflowItems);
        }

        if (fill != null) {
            for (int i = 0; i < 36; i++) {
                if (player.getInventory().getItem(i) == null) {
                    player.getInventory().setItem(i, fill);
                }
            }
        }

        effects.stream().forEach(effect -> player.addPotionEffect(effect));
    }

    private void giveOverflowItems(Player p, List<ItemStack> overflowItems) {
        for (int i = 0; i < 36; i++) {
            if (p.getInventory().getItem(i) == null) { // if empty slot found
                if (overflowItems.size() >= 1) {
                    p.getInventory().setItem(i, overflowItems.get(0));
                    overflowItems.remove(0);
                } else {
                    return; // if no overflow items left, return out of function
                }
            }
        }

        boolean dropRemainingOverflowItems = (Boolean) options.get("DropRemainingOverflowItemsOnKit");

        if (dropRemainingOverflowItems) {
            for (ItemStack remainingOverflowItem : overflowItems) {
                p.getWorld().dropItem(p.getLocation(), remainingOverflowItem);
            }
            String overflowItemsDropMessage = (String) options.get("Message-OverflowItemsDropped");
            p.sendMessage(overflowItemsDropMessage.replace("%number%", String.valueOf(overflowItems.size())));
        } else {
            String overflowItemsLostMessage = (String) options.get("Message-OverflowItemsLost");
            p.sendMessage(overflowItemsLostMessage.replace("%number%", String.valueOf(overflowItems.size())));
        }
    }

    public void toResource(Resource resource) {
        resource.set("Kit.Permission", permission != null ? permission : "kp.kit." + name);
        resource.set("Kit.Cooldown", cooldown != null ? cooldown.formatted(true) : 0);
        resource.set("Kit.Level", level);
        resource.set("Kit.Health", maxHealth);
        resource.save();

        AttributeWriter.itemStackToResource(resource, "Inventory.Armor.Helmet", kitHelmet);
        AttributeWriter.itemStackToResource(resource, "Inventory.Armor.Chestplate", kitChestplate);
        AttributeWriter.itemStackToResource(resource, "Inventory.Armor.Leggings", kitLeggings);
        AttributeWriter.itemStackToResource(resource, "Inventory.Armor.Boots", kitBoots);

        for (Integer slot : inventory.keySet()) {
            AttributeWriter.itemStackToResource(resource, "Inventory.Items." + slot, inventory.get(slot));
        }

        AttributeWriter.itemStackToResource(resource, "Inventory.Items.Offhand", offhand);
        AttributeWriter.itemStackToResource(resource, "Inventory.Items.Fill", fill);

        for (PotionEffect effect : effects) {
            AttributeWriter.potionEffectToResource(resource, "Effects", effect);
        }

        resource.save();
    }

    public String getName() { return name; }

    public String getPermission() { return permission; }

    public Cooldown getCooldown() { return cooldown; }

    public int getLevel() { return level; }

    public int getMaxHealth() { return maxHealth; }

    public Map<Integer, ItemStack> getInventory() { return inventory; }

    public List<PotionEffect> getEffects() { return effects; }

    public ItemStack getHelmet() { return kitHelmet; }

    public ItemStack getChestplate() { return kitChestplate; }

    public ItemStack getLeggings() { return kitLeggings; }

    public ItemStack getBoots() { return kitBoots; }

    public ItemStack getFill() { return fill; }

}
