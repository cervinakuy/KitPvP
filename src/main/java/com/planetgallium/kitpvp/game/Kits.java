package com.planetgallium.kitpvp.game;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.Ability;
import com.planetgallium.kitpvp.api.Kit;
import com.planetgallium.kitpvp.api.PlayerSelectKitEvent;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.newkit.AttributeParser;
import com.planetgallium.kitpvp.util.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;

public class Kits {

    private Game plugin;
    private Arena arena;
    private Resources resources;

    private Map<String, String> kits;

    public Kits(Game plugin, Arena arena) {
        this.arena = arena;
        this.plugin = plugin;
        this.resources = plugin.getResources();

        this.kits = new HashMap<>();
    }

    public Kit createKitFromPlayer(Player player, String name) {

        Player p = player;
        
        //          KIT             //

        Kit kit = new Kit(name);

        kit.setHelmet(p.getInventory().getHelmet());
        kit.setChestplate(p.getInventory().getChestplate());
        kit.setLeggings(p.getInventory().getLeggings());
        kit.setBoots(p.getInventory().getBoots());

        for (PotionEffect effect : p.getActivePotionEffects()) {
            PotionEffectType type = effect.getType();
            int amplifier = effect.getAmplifier();
            int duration = effect.getDuration();

            kit.addEffect(type, amplifier, duration / 20);
        }

        for (int i = 0; i < 36; i++) {
            ItemStack item = p.getInventory().getItem(i);
            if (item != null) {
                kit.setInventoryItem(i, item);
            }
            // do something about soups?
        }
        
        if (Toolkit.versionToNumber() >= 19) {
            ItemStack offhandItem = p.getInventory().getItemInOffHand();
            if (offhandItem != null) {
                kit.setOffhand(offhandItem);
            }
        }

        //          ABILITY         //

        Ability sampleAbility = new Ability("Example");

        ItemStack activator = XMaterial.EMERALD.parseItem();
        ItemMeta activatorMeta = activator.getItemMeta();

        activatorMeta.setDisplayName(Toolkit.translate("&aDefault Ability &7(Must be modified in kit file)"));
        activator.setItemMeta(activatorMeta);

        sampleAbility.setActivator(activator);

        sampleAbility.setMessage("%prefix% &7You have used your ability.");
        sampleAbility.setSound(XSound.BLOCK_NOTE_BLOCK_PLING.parseSound(), 1, 1);
        sampleAbility.addEffect(XPotion.SPEED.parsePotionEffectType(), 1, 10);
        sampleAbility.addCommand("console: This command is run from the console, you can use %player%");
        sampleAbility.addCommand("player: This command is run from the p, you can use %player%");

        kit.addAbility(sampleAbility);

        return kit;

    }

    private Kit createKitFromResource(Resource resource) {

        Kit kit = new Kit(resource.getName());

        kit.setPermission(resource.getString("Kit.Permission"));
        kit.setLevel(resource.getInt("Kit.Level"));
        // cooldown


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

        return kit;

    }

    public void attemptToGiveKitToPlayer(Player player, Kit kit) {

        Player p = player;

        if (!p.hasPermission(kit.getPermission())) {
            p.sendMessage(resources.getMessages().getString("Messages.General.Permission"));
            return;
        }

        if (!(Toolkit.getPermissionAmount(p, "kp.levelbypass.", 0) >= kit.getLevel() ||
                arena.getLevels().getLevel(p.getUniqueId()) >= kit.getLevel())) {
            p.sendMessage(resources.getMessages().getString("Messages.Other.Needed").replace("%level%", String.valueOf(kit.getLevel())));
            return;
        }

        if (!(p.hasPermission("kp.cooldownbypass") || !arena.getCooldowns().isOnCooldown(p.getUniqueId(), kit.getName()))) {
            p.sendMessage(resources.getMessages().getString("Messages.Error.Cooldown").replace("%cooldown%", arena.getCooldowns().getFormattedCooldown(p.getUniqueId(), kit.getName())));
            return;
        }

        if (hasKit(player.getName())) {
            p.sendMessage(resources.getMessages().getString("Messages.Error.Selected"));
            p.playSound(p.getLocation(), XSound.ENTITY_ENDER_DRAGON_HURT.parseSound(), 1, 1);
            return;
        }

        if (Config.getB("Arena.ClearInventoryOnKit")) {
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
        }

        kit.apply(p);
        p.sendMessage(resources.getMessages().getString("Messages.Commands.Kit").replace("%kit%", kit.getName()));
        p.playSound(p.getLocation(), XSound.ENTITY_HORSE_ARMOR.parseSound(), 1, 1);

        Bukkit.getPluginManager().callEvent(new PlayerSelectKitEvent(player, kit));

        if (kit.getCooldown() != null && !p.hasPermission("kp.cooldownbypass")) {
            arena.getCooldowns().setCooldown(p.getUniqueId(), kit.getName());
        }

    }

    public Kit getKitByName(String kitName) {

        if (!CacheManager.getKitCache().containsKey(kitName)) {

        }

        return CacheManager.getKitCache().get(kitName);

    }

    public void setKit(String playerName, String kitName) {

        kits.put(playerName, kitName);

    }

    public boolean hasKit(String playerName) {

        return kits.containsKey(playerName);

    }

    public Kit getKitOfPlayer(String playerName) {

        // USE CACHING SYSTEM PLEASE

//        return kits.get(playerName);

        return null;
    }

    public void resetKit(String playerName) {

        kits.remove(playerName);

    }

    public boolean isKit(String kitName) {

        return getList().contains(kitName);

    }

    public List<String> getList() {

        File folder = new File(plugin.getDataFolder().getAbsolutePath() + "/kits");
        List<String> list = new ArrayList<>();

        for (String fileName : folder.list()) {
            list.add(fileName.split(".yml")[0]);
        }

        return list;

    }

}
