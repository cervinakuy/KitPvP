package com.planetgallium.kitpvp.game;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.PlayerSelectKitEvent;
import com.planetgallium.kitpvp.item.EffectItem;
import com.planetgallium.kitpvp.item.KitItem;
import com.planetgallium.kitpvp.kit.Kit;
import com.planetgallium.kitpvp.util.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.util.*;

@SuppressWarnings("deprecation")
public class Kits {

    private Game plugin;
    private Resources resources;

    public Kits(Game plugin, Resources resources) {
        this.plugin = plugin;
        this.resources = resources;
    }

    private Map<String, String> kits = new HashMap<String, String>();

    public void createKit(String name, Player p) {

        if (!isKit(name)) {

            resources.addKit(name);

            Resource kitResource = resources.getKits(name);
            kitResource.set("Kit.Permission", "kp.kit." + name.toLowerCase());
            kitResource.set("Kit.Level", 0);
            kitResource.set("Kit.Cooldown", 0);
            kitResource.save();

            if (p.getInventory().getHelmet() != null) {

                saveItem(kitResource, name, "Inventory.Armor.Helmet", p.getInventory().getHelmet(), "&fHelmet");

            }

            if (p.getInventory().getChestplate() != null) {

                saveItem(kitResource, name, "Inventory.Armor.Chestplate", p.getInventory().getChestplate(), "&fChestplate");

            }

            if (p.getInventory().getLeggings() != null) {

                saveItem(kitResource, name, "Inventory.Armor.Leggings", p.getInventory().getLeggings(), "&fLeggings");

            }

            if (p.getInventory().getBoots() != null) {

                saveItem(kitResource, name, "Inventory.Armor.Boots", p.getInventory().getBoots(), "&fBoots");

            }

            for (int i = 0; i < 36; i++) {

                ItemStack item = p.getInventory().getItem(i);

                if (item != null) {

                    String backupName = (item.getType() == XMaterial.MUSHROOM_STEW.parseMaterial()) ? Config.getS("Soups.Name") : null;
                    saveItem(kitResource, name, "Inventory.Items." + i, p.getInventory().getItem(i), backupName);

                }

            }

            for (PotionEffect effect : p.getActivePotionEffects()) {

                resources.getKits(name).set("Potions." + effect.getType().getName() + ".Level", effect.getAmplifier() + 1);
                resources.getKits(name).set("Potions." + effect.getType().getName() + ".Duration", effect.getDuration() / 20);
                resources.getKits(name).save();

            }

            resources.getKits(name).set("Ability.Activator.Name", "&aDefault Ability &7(Must be modified in kit file)");
            resources.getKits(name).set("Ability.Activator.Item", "BEDROCK");
            resources.getKits(name).set("Ability.Message.Message", "%prefix% &7You have used your ability.");
            resources.getKits(name).set("Ability.Message.Enabled", true);
            resources.getKits(name).set("Ability.Sound.Sound", "FIZZ");
            resources.getKits(name).set("Ability.Sound.Pitch", 1);
            resources.getKits(name).set("Ability.Sound.Enabled", true);
            resources.getKits(name).set("Ability.Potions.SPEED.Level", 1);
            resources.getKits(name).set("Ability.Potions.SPEED.Duration", 10);
            resources.getKits(name).set("Ability.Commands.Commands", new String[]{"console: This command is run from the console, you can use %player%", "player: This command is run from the player, you can use %player%"});
            resources.getKits(name).set("Ability.Commands.Enabled", false);
            resources.getKits(name).save();

            p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Commands.Create").replace("%kit%", name)));

        } else {

            p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Exists").replace("%prefix%", resources.getMessages().getString("Messages.General.Prefix"))));

        }

    }

    public void giveKit(String name, Player p) {

        if (isKit(name)) {

            if (p.hasPermission(resources.getKits(name).getString("Kit.Permission"))) {

                if (Game.getInstance().getArena().getLevels().getLevel(p.getUniqueId()) >= resources.getKits(name).getInt("Kit.Level")) {

                    if (!plugin.getArena().getCooldowns().isOnCooldown(p.getUniqueId(), name)) {

                        Kit kit = new Kit();
                        kit.setName(name);

                        Resource kitResource = resources.getKits(kit.getName());

                        if (kitResource.contains("Inventory.Armor")) {

                            if (kitResource.contains("Inventory.Armor.Helmet")) {

                                kit.setHelmet(new KitItem(kitResource, kit.getName(), "Inventory.Armor.Helmet"));

                            }

                            if (kitResource.contains("Inventory.Armor.Chestplate")) {

                                kit.setChestplate(new KitItem(kitResource, kit.getName(), "Inventory.Armor.Chestplate"));

                            }

                            if (kitResource.contains("Inventory.Armor.Leggings")) {

                                kit.setLeggings(new KitItem(kitResource, kit.getName(), "Inventory.Armor.Leggings"));

                            }

                            if (kitResource.contains("Inventory.Armor.Boots")) {

                                kit.setBoots(new KitItem(kitResource, kit.getName(), "Inventory.Armor.Boots"));

                            }

                        }

                        ConfigurationSection items = kitResource.getConfigurationSection("Inventory.Items");

                        for (String identifier : items.getKeys(false)) {

                            if (!identifier.equals("Fill")) {

                                kit.addItem(new KitItem(kitResource, kit.getName(), "Inventory.Items." + identifier), Integer.valueOf(identifier));

                            } else {

                                kit.setFill(new KitItem(kitResource, kit.getName(), "Inventory.Items.Fill"));

                            }

                        }

                        if (kitResource.contains("Potions")) {

                            ConfigurationSection potions = kitResource.getConfigurationSection("Potions");

                            for (String identifier : potions.getKeys(false)) {

                                kit.addEffect(new EffectItem(kitResource, identifier));

                            }

                        }

                        kit.applyKit(p);
                        setKit(p.getName(), name);

                        if (resources.getKits(name).getString("Kit.Cooldown") != "0") {

                            plugin.getArena().getCooldowns().setCooldown(p.getUniqueId(), name);

                        }

                        Bukkit.getPluginManager().callEvent(new PlayerSelectKitEvent(p, kit.getName()));

                    } else {

                        p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Cooldown").replace("%cooldown%", plugin.getArena().getCooldowns().getFormattedCooldown(p.getUniqueId(), name))));

                    }

                } else {

                    p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Other.Needed").replace("%level%", String.valueOf(resources.getKits(name).getInt("Kit.Level")))));

                }

            } else {

                p.sendMessage(Config.tr(resources.getMessages().getString("Messages.General.Permission")));

            }

        } else {

            p.sendMessage(Config.tr(resources.getMessages().getString("Messages.Error.Lost")));

        }

    }

    public void saveItem(Resource resource, String kit, String path, ItemStack item, String backupName) {

        ItemMeta meta = item.getItemMeta();

        resource.set(path + ".Name", meta.hasDisplayName() ? meta.getDisplayName().replace("§", "&") : backupName);
        resource.set(path + ".Lore", meta.getLore());
        resource.set(path + ".Item", item.getType().toString());
        resource.set(path + ".Amount", item.getAmount() == 1 ? null : item.getAmount());
        resource.save();

        if (item.getType() == XMaterial.LEATHER_HELMET.parseMaterial() ||
                item.getType() == XMaterial.LEATHER_CHESTPLATE.parseMaterial() ||
                item.getType() == XMaterial.LEATHER_LEGGINGS.parseMaterial() ||
                item.getType() == XMaterial.LEATHER_BOOTS.parseMaterial()) {

            LeatherArmorMeta dyedMeta = (LeatherArmorMeta) meta;

            resource.set(path + ".Dye.Red", dyedMeta.getColor().getRed());
            resource.set(path + ".Dye.Green", dyedMeta.getColor().getGreen());
            resource.set(path + ".Dye.Blue", dyedMeta.getColor().getBlue());
            resource.save();

        } else if (item.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) {

            SkullMeta skullMeta = (SkullMeta) meta;

            resource.set(path + ".Skull", skullMeta.getOwner());
            resource.save();

        } else if (item.getType() == XMaterial.POTION.parseMaterial() ||
                item.getType() == XMaterial.SPLASH_POTION.parseMaterial() ||
                item.getType() == XMaterial.LINGERING_POTION.parseMaterial()) {

            if (Toolkit.versionToNumber() == 18) {

                Potion potionStack = Potion.fromItemStack(item);
                PotionMeta potionMeta = (PotionMeta) meta;

                if (potionMeta.getCustomEffects().size() > 0) {

                    for (PotionEffect potion : potionMeta.getCustomEffects()) {

                        resource.set(path + ".Potion.Type", potionStack.isSplash() ? "SPLASH_POTION" : "POTION");
                        resource.set(path + ".Potion." + potion.getType().getName() + ".Level", potion.getAmplifier());
                        resource.set(path + ".Potion." + potion.getType().getName() + ".Duration", potion.getDuration() / 20);
                        resource.save();

                    }

                } else {

                    for (PotionEffect potion : potionStack.getEffects()) {

                        resource.set(path + ".Potion.Type", potionStack.isSplash() ? "SPLASH_POTION" : "POTION");
                        resource.set(path + ".Potion." + potion.getType().getName() + ".Level", potion.getAmplifier());
                        resource.set(path + ".Potion." + potion.getType().getName() + ".Duration", potion.getDuration() / 20);
                        resource.save();

                    }

                }

            } else if (Toolkit.versionToNumber() >= 19) {

                PotionMeta potionMeta = (PotionMeta) meta;

                if (potionMeta.getCustomEffects().size() > 0) {

                    for (PotionEffect potion : potionMeta.getCustomEffects()) {

                        resource.set(path + ".Potion." + potion.getType().getName() + ".Level", potion.getAmplifier());
                        resource.set(path + ".Potion." + potion.getType().getName() + ".Duration", potion.getDuration() / 20);
                        resource.save();

                    }

                } else {

                    String type = potionMeta.getBasePotionData().getType().toString();
                    resource.set(path + ".Potion." + type + ".Upgraded", potionMeta.getBasePotionData().isUpgraded());
                    resource.set(path + ".Potion." + type + ".Extended", potionMeta.getBasePotionData().isExtended());
                    resource.save();

                }

            }

        }

        if (item.getEnchantments().size() > 0) {

            if (Toolkit.versionToNumber() < 113) {

                for (Enchantment enchantment : item.getEnchantments().keySet()) {

                    resource.set(path + ".Enchantments." + enchantment.getName() + ".Level", item.getEnchantments().get(enchantment));
                    resource.save();

                }

            } else if (Toolkit.versionToNumber() >= 113) {

                for (Enchantment enchantment : item.getEnchantments().keySet()) {

                    resource.set(path + ".Enchantments." + enchantment.getKey().getKey() + ".Level", item.getEnchantments().get(enchantment));
                    resource.save();

                }

            }

        }

        if (Toolkit.versionToNumber() < 113) {

            if (item.getDurability() > 0 &&
                    item.getType() != XMaterial.PLAYER_HEAD.parseMaterial() &&
                    item.getType() != XMaterial.POTION.parseMaterial() &&
                    item.getType() != XMaterial.SPLASH_POTION.parseMaterial()) {

                resource.set(path + ".Durability", item.getDurability());
                resource.save();

            }

        } else if (Toolkit.versionToNumber() >= 113) {

            if (meta instanceof Damageable &&
                    item.getType() != XMaterial.PLAYER_HEAD.parseMaterial() &&
                    item.getType() != XMaterial.POTION.parseMaterial() &&
                    item.getType() != XMaterial.SPLASH_POTION.parseMaterial()) {

                Damageable damagedMeta = (Damageable) meta;

                if (damagedMeta.hasDamage()) {

                    resource.set(path + ".Durability", damagedMeta.getDamage());
                    resource.save();

                }

            }

        }

    }

    private void setKit(String username, String kit) {

        if (!kits.containsKey(username)) {

            kits.put(username, kit);

        }

    }

    public void clearKit(String username) {

        if (hasKit(username)) {

            kits.remove(username);

        }

    }

    public String getKit(String username) {

        if (hasKit(username)) {

            return kits.get(username);

        }

        return resources.getMessages().getString("Messages.Other.NoKit");

    }

    public boolean hasKit(String username) {

        return kits.containsKey(username);

    }

    public boolean isKit(String kit) {

        return getList().contains(kit + ".yml");

    }

    public String getPath() {

        return plugin.getDataFolder().getAbsolutePath() + "/kits";

    }

    public List<String> getList() {

        File folder = new File(plugin.getDataFolder().getAbsolutePath() + "/kits");

        return new ArrayList<String>(Arrays.asList(folder.list()));

    }

}
