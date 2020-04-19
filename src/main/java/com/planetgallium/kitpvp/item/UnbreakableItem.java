package com.planetgallium.kitpvp.item;

import com.planetgallium.kitpvp.util.Toolkit;
import org.bukkit.inventory.ItemStack;

public class UnbreakableItem {

    public ItemStack convertToUnbreakable(ItemStack original) {

        // versionToNumber converts server version to number (ex. 1.14 -> 114)
        if (Toolkit.versionToNumber() <= 114) {

//            original.getItemMeta().spigot().setUnbreakable(true);
            return original;

        } else if (Toolkit.versionToNumber() > 114) {

            original.getItemMeta().setUnbreakable(true);
            return original;

        }

        return original;

    }

}
