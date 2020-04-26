package com.planetgallium.kitpvp.item;

import com.planetgallium.kitpvp.util.Toolkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UnbreakableItem {

    public ItemStack convertToUnbreakable(ItemStack original) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        if (Toolkit.versionToNumber() <= 114) {

            ItemMeta meta = original.getItemMeta();

            Method spigotMethod = meta.getClass().getMethod("spigot");
            spigotMethod.setAccessible(true);

            Object spigotInstance = spigotMethod.invoke(meta);
            Class spigotClass = spigotInstance.getClass();

            Method setUnbreakableMethod = spigotClass.getDeclaredMethod("setUnbreakable", boolean.class);
            setUnbreakableMethod.setAccessible(true);

            setUnbreakableMethod.invoke(spigotInstance, true);

            return original;

        } else if (Toolkit.versionToNumber() > 114) {

            original.getItemMeta().setUnbreakable(true);
            return original;

        }

        return original;

    }

}
