package com.planetgallium.kitpvp.listener;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Resource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class PacketListener implements com.github.retrooper.packetevents.event.PacketListener {

    private final Game plugin;
    private final Resource config;

    public PacketListener(Game plugin) {
        this.plugin = plugin;
        this.config = plugin.getResources().getConfig();
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_EQUIPMENT) {
            final WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment(event);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getEntityId() == packet.getEntityId()) {
                    if (player.hasPotionEffect(PotionEffectType.INVISIBILITY) && config.getBoolean("Arena.ArmorInvisibility", true)) {
                        for (Equipment equipment : packet.getEquipment()) {
                            if (equipment.getSlot() != EquipmentSlot.MAIN_HAND && equipment.getSlot() != EquipmentSlot.OFF_HAND) {
                                equipment.setItem(ItemStack.EMPTY);
                            }
                        }
                        event.markForReEncode(true);
                    }
                }
            }
        }
    }
}
