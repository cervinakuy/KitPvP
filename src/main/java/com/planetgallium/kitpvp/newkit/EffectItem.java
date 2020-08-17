package com.planetgallium.kitpvp.newkit;

import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.XMaterial;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.*;

import java.util.ArrayList;
import java.util.List;

public class EffectItem {

    private Resource resource;
    private boolean isCustom;
    private boolean hasColor;

    private List<PotionEffect> effects;

    private PotionData potionData;
    private boolean isUpgraded;
    private boolean isExtended;
    private boolean isSplash;
    private PotionType potionType;

    private Color color;

    public EffectItem(Resource resource, String path) {

        this.resource = resource;
        this.isCustom = !resource.contains(path + "." + resource.getConfigurationSection(path).getKeys(false).toArray()[0] + ".Upgraded");
        this.hasColor = resource.contains(path.replace("Effects", "") + "Color");
        this.effects = new ArrayList<PotionEffect>();

        if (isCustom) {

            for (String identifier : resource.getConfigurationSection(path).getKeys(false)) {

                PotionEffectType effect = PotionEffectType.getByName(identifier);
                int duration = resource.getInt(path + "." + identifier + ".Duration") * 20;
                int amplifier = resource.getInt(path + "." + identifier + ".Amplifier");
                effects.add(new PotionEffect(effect, duration, amplifier - 1));

            }

        } else {

            for (String identifier : resource.getConfigurationSection(path).getKeys(false)) {

                this.potionType = PotionType.valueOf(resource.getString(path.replace("Effects", "") + "Type"));
                this.isUpgraded = resource.getBoolean(path + "." + identifier + ".Upgraded");
                this.isExtended = resource.getBoolean(path + "." + identifier + ".Extended");
                this.potionData = new PotionData(potionType == null ? PotionType.UNCRAFTABLE : potionType, isExtended, isUpgraded);

            }

        }

        if (hasColor) {

            this.color = Toolkit.getColorFromConfig(resource, path.replace("Effects", "") + "Color");

        }

        if (Toolkit.versionToNumber() == 18) {

            this.isSplash = resource.getString(path.replace("Effects", "") + "Type").equals("SPLASH_POTION");

        }

    }

    public ItemStack convertToEffectItem(ItemStack original) {

        if (Toolkit.versionToNumber() == 18) {

            if (original.getType() == XMaterial.POTION.parseMaterial().get() || original.getType() == XMaterial.SPLASH_POTION.parseMaterial().get()) {
                Potion potion = Potion.fromItemStack(original);
                potion.setSplash(isSplash);

                original = potion.toItemStack(original.getAmount());
            }

        }

        PotionMeta potionMeta = (PotionMeta) original.getItemMeta();

        if (isCustom) {

            for (PotionEffect effect : effects) {
                potionMeta.addCustomEffect(effect, true);
            }

        } else {

            potionMeta.setBasePotionData(potionData);

        }

        if (hasColor) {

            potionMeta.setColor(color);

        }

        original.setItemMeta(potionMeta);

        return original;

    }

}
