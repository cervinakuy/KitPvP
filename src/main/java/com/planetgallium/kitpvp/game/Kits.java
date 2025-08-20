package com.planetgallium.kitpvp.game;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.XSound;
import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.Ability;
import com.planetgallium.kitpvp.api.Kit;
import com.planetgallium.kitpvp.api.PlayerSelectKitEvent;
import com.planetgallium.kitpvp.item.AttributeParser;
import com.planetgallium.kitpvp.util.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Kits {

    private final Game plugin;
    private final Arena arena;
    private final Resources resources;
    private final Resource messages;

    private final Map<UUID, String> playerKits;

    public Kits(Game plugin, Arena arena) {
        this.arena = arena;
        this.plugin = plugin;
        this.resources = plugin.getResources();
        this.messages = resources.getMessages();

        this.playerKits = new HashMap<>();
    }

    // Could also load and cache kits onEnable like Abilities.yml so no lag spike for hefty kit when first used by
    // player

    public void createKit(Player fromPlayer, String kitName) {
        Kit kitToCreate = createKitFromPlayer(fromPlayer, kitName);

        Resource kitResource = new Resource(plugin, "kits/" + kitToCreate.getName() + ".yml");
        kitToCreate.toResource(kitResource);

        resources.addResource(kitToCreate.getName() + ".yml", kitResource);
        plugin.getDatabase().addKitCooldownTable(kitName);

        automaticallyAddKitToMenu(fromPlayer, kitToCreate);
    }

    private void automaticallyAddKitToMenu(Player fromPlayer, Kit kitToCreate) {
        if (plugin.getConfig().getBoolean("Other.AutomaticallyAddKitToMenu")) {

            int nextAvailableMenuSlot = Toolkit.getNextAvailable(resources.getMenu(), "Menu.Items",
                    resources.getMenu().getInt("Menu.General.Size") - 1, true, -1);

            if (nextAvailableMenuSlot != -1) {
                Resource menuConfig = resources.getMenu();
                String pathPrefix = "Menu.Items." + nextAvailableMenuSlot;

                menuConfig.set(pathPrefix + ".Name", "&a&l" + kitToCreate.getName() + " Kit");
                menuConfig.set(pathPrefix + ".Material", "BEDROCK");
                menuConfig.set(pathPrefix + ".Lore", new String[]{
                        "&7This information can be modified in the",
                        "&7menu.yml file. To disable automatic adding to the",
                        "&7menu, disable AutomaticallyAddKitToMenu in the",
                        "&7config.yml.",
                        " ",
                        "&e&eLeft-click to select.",
                        "&eRight-click to preview."});
                menuConfig.set(pathPrefix + ".Commands.Left-Click",new String[]{
                        "player: kp kit " + kitToCreate.getName()});
                menuConfig.set(pathPrefix + ".Commands.Right-Click", new String[]{
                        "player: kp preview " + kitToCreate.getName()});

                menuConfig.save();
                menuConfig.load();
//                arena.getMenus().getKitMenu().rebuildCache();
            } else {
                fromPlayer.sendMessage(messages.fetchString("Messages.Error.Menu"));
            }
        }
    }

    public Kit createKitFromPlayer(Player player, String name) {
        Player p = player;
        
        //          KIT             //

        Kit kit = new Kit(name);

        kit.setHelmet(p.getInventory().getHelmet());
        kit.setChestplate(p.getInventory().getChestplate());
        kit.setLeggings(p.getInventory().getLeggings());
        kit.setBoots(p.getInventory().getBoots());

        kit.setMaxHealth(Toolkit.getMaxHealth(p));

        for (PotionEffect effect : p.getActivePotionEffects()) {
            PotionEffectType type = effect.getType();
            int amplifier = effect.getAmplifier();
            int duration = effect.getDuration();
            int amplifierNonZeroBased = amplifier + 1;
            int durationSeconds = duration / 20;

            kit.addEffect(type, amplifierNonZeroBased, durationSeconds);
        }

        for (int i = 0; i < 36; i++) {
            ItemStack item = p.getInventory().getItem(i);
            if (item == null) {
                continue; // skip this item
            }

            if (Toolkit.hasMatchingMaterial(item, "MUSHROOM_STEW")) {
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(resources.getConfig().fetchString("Soups.Name"));
                itemMeta.setLore(Toolkit.colorizeList(resources.getConfig().getStringList("Soups.Lore")));
                item.setItemMeta(itemMeta);
            }

            kit.setInventoryItem(i, item);
        }
        
        if (Toolkit.versionToNumber() >= 19) {
            ItemStack offhandItem = p.getInventory().getItemInOffHand();
            if (offhandItem != null) {
                kit.setOffhand(offhandItem);
            }
        }

        //          ABILITY         //

        Ability sampleAbility = new Ability(kit.getName() + "-Blank");

        ItemStack activator = XMaterial.EMERALD.parseItem();
        ItemMeta activatorMeta = activator.getItemMeta();

        activatorMeta.setDisplayName(Toolkit.translate("&aBlank Kit Ability &7(Right Click)"));
        activator.setItemMeta(activatorMeta);

        sampleAbility.setActivator(activator);

        sampleAbility.setMessage("%prefix% &7You have used your ability.");
        sampleAbility.setSound(XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1, 1);
        sampleAbility.addEffect(XPotion.SPEED.getPotionEffectType(), 1, 10);
        sampleAbility.addCommand("console: This command is run from the console, you can use %player%");
        sampleAbility.addCommand("player: This command is run from the player, you can use %player%");

        resources.addAbilityResource(sampleAbility);

        return kit;
    }

    private Kit createKitFromResource(Resource resource) throws IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Kit kit = new Kit(trimName(resource.getName()));

        kit.setPermission(resource.fetchString("Kit.Permission"));
        kit.setLevel(resource.getInt("Kit.Level"));
        kit.setCooldown(new Cooldown(resource.fetchString("Kit.Cooldown")));

        kit.setMaxHealth(resource.contains("Kit.Health") ? resource.getInt("Kit.Health") : 20);

        kit.setHelmet(AttributeParser.getItemStackFromPath(resource, "Inventory.Armor.Helmet"));
        kit.setChestplate(AttributeParser.getItemStackFromPath(resource, "Inventory.Armor.Chestplate"));
        kit.setLeggings(AttributeParser.getItemStackFromPath(resource, "Inventory.Armor.Leggings"));
        kit.setBoots(AttributeParser.getItemStackFromPath(resource, "Inventory.Armor.Boots"));

        for (PotionEffect effect : AttributeParser.getEffectsFromPath(resource, "Effects")) {
            kit.addEffect(effect.getType(), effect.getAmplifier(), effect.getDuration());
        }

        for (int i = 0; i < 36; i++) {
            if (resource.contains("Inventory.Items." + i)) {
                kit.setInventoryItem(i, AttributeParser.getItemStackFromPath(resource, "Inventory.Items." + i));
            }
        }

        kit.setFill(AttributeParser.getItemStackFromPath(resource, "Inventory.Items.Fill"));
        kit.setOffhand(AttributeParser.getItemStackFromPath(resource, "Inventory.Items.Offhand"));

        kit.setOption("AddOverflowItemsOnKit",
                resources.getConfig().getBoolean("Arena.AddOverflowItemsOnKit"));
        kit.setOption("DropRemainingOverflowItemsOnKit",
                resources.getConfig().getBoolean("Arena.DropRemainingOverflowItemsOnKit"));
        kit.setOption("Message-OverflowItemsLost",
                resources.getMessages().fetchString("Messages.Error.OverflowItemsLost"));
        kit.setOption("Message-OverflowItemsDropped",
                resources.getMessages().fetchString("Messages.Other.OverflowItemsDropped"));

        return kit;
    }

    public void attemptToGiveKitToPlayer(Player player, Kit kit) {
        Player p = player;

        if (kit == null) {
            p.sendMessage(messages.fetchString("Messages.Error.Lost"));
            return;
        }

        if (!p.hasPermission(kit.getPermission())) {
            p.sendMessage(messages.fetchString("Messages.General.Permission")
                    .replace("%permission%", kit.getPermission()));
            return;
        }

        if (!(Toolkit.getPermissionAmount(p, "kp.levelbypass.", 0) >= kit.getLevel() ||
                arena.getStats().getStat("level", p.getUniqueId()) >= kit.getLevel())) {
            p.sendMessage(messages.fetchString("Messages.Other.Needed")
                    .replace("%level%", String.valueOf(kit.getLevel())));
            return;
        }

        Cooldown cooldownRemaining = arena.getCooldowns().getRemainingCooldown(p, kit);
        if (!p.hasPermission("kp.cooldownbypass") && cooldownRemaining.toSeconds() > 0) {
            p.sendMessage(messages.fetchString("Messages.Error.CooldownKit")
                    .replace("%cooldown%", cooldownRemaining.formatted(false)));
            return;
        }

        if (playerHasKit(player.getUniqueId())) {
            p.sendMessage(messages.fetchString("Messages.Error.Selected"));
            Toolkit.playSoundToPlayer(p, "ENTITY_ENDER_DRAGON_HURT", 1);
            return;
        }

        if (resources.getConfig().getBoolean("Arena.ClearInventoryOnKit")) {
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
        }

        kit.apply(p);
        p.sendMessage(messages.fetchString("Messages.Commands.Kit").replace("%kit%", kit.getName()));
        Toolkit.playSoundToPlayer(p, "ENTITY_HORSE_ARMOR", 1);

        Bukkit.getPluginManager().callEvent(new PlayerSelectKitEvent(player, kit));
        setPlayerKit(p.getUniqueId(), kit.getName());

        Cooldown kitCooldown = kit.getCooldown();
        if (kitCooldown != null && kitCooldown.toSeconds() > 0 && !p.hasPermission("kp.cooldownbypass")) {
            arena.getCooldowns().setKitCooldown(p.getUniqueId(), kit.getName());
        }

        Resource kitResource = resources.getKit(kit.getName());
        if (kitResource != null && kitResource.contains("Commands")) {
            Toolkit.runCommands(p, kitResource.getStringList("Commands"),
                    "none", "none");
        }
    }

    private Kit loadKitFromCacheOrCreate(String kitName) {
        if (!CacheManager.getKitCache().containsKey(kitName)) {
            if (isKit(kitName)) {
                Resource kit = resources.getKit(kitName);
                try {
                    CacheManager.getKitCache().put(kitName, createKitFromResource(kit));
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                Toolkit.printToConsole("&7[&b&lKIT-PVP&7] &cNo kit with name " + kitName + " found in the kits folder." +
                        " Try reloading the server.");
            }
        }
        return CacheManager.getKitCache().get(kitName);
    }

    public Kit getKitByName(String kitName) {
        return loadKitFromCacheOrCreate(kitName);
    }

    public Kit getKitOfPlayer(UUID uniqueId) {
        String kitName = playerKits.get(uniqueId);
        if (kitName == null) {
            return null;
        }
        return loadKitFromCacheOrCreate(kitName);
    }

    public boolean isKit(String kitName) {
        return resources.getPluginDirectoryFiles("kits", false).contains(kitName);
    }

    public void deleteKit(String kitName) {
        resources.removeResource(kitName + ".yml");
        plugin.getDatabase().deleteKitCooldownTable(kitName);
        CacheManager.getKitCache().remove(kitName);
    }

    public void setPlayerKit(UUID uniqueId, String kitName) {
        playerKits.put(uniqueId, kitName);
    }

    public boolean playerHasKit(UUID uniqueId) {
        return playerKits.containsKey(uniqueId);
    }

    public void resetPlayerKit(UUID uniqueId) {
        playerKits.remove(uniqueId);
    }

    private String trimName(String kitNameWithFileEnding) {
        String[] splitName = kitNameWithFileEnding.split(".yml");
        return splitName[0];
    }

}
