package com.hmmbo.ultimate_Shop_Core.utils.sign;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.hmmbo.ultimate_Shop_Core.Ultimate_Shop_Core;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Utility for prompting players with a sign editor and retrieving the input.
 */
public class SignInput {
    private static final Map<UUID, Consumer<String[]>> callbacks = new ConcurrentHashMap<>();
    private static PacketListener listener;

    /**
     * Initialise the packet listener. Call this once in plugin onEnable.
     */
    public static void init(Plugin plugin) {
        if (listener != null) return;
        listener = new PacketAdapter(plugin, PacketType.Play.Client.UPDATE_SIGN) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                UUID id = event.getPlayer().getUniqueId();
                Consumer<String[]> cb = callbacks.remove(id);
                if (cb != null) {
                    String[] lines = event.getPacket().getStringArrays().read(0);
                    cb.accept(lines);
                    Location loc = event.getPlayer().getLocation().getBlock().getLocation();
                    event.getPlayer().sendBlockChange(loc, Material.AIR.createBlockData());
                    event.setCancelled(true);
                }
            }
        };
        ProtocolLibrary.getProtocolManager().addPacketListener(listener);
    }

    /**
     * Opens a sign editor for the player. The callback is invoked with the entered lines.
     */
    public static void open(Player player, Consumer<String[]> callback) {
        if (listener == null) {
            init(Ultimate_Shop_Core.instance);
        }
        Location loc = player.getLocation();
        loc = new Location(player.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        BlockData data = Material.OAK_SIGN.createBlockData();
        player.sendBlockChange(loc, data);

        var openSign = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);
        openSign.getBlockPositionModifier().write(0, pos);
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, openSign);

        callbacks.put(player.getUniqueId(), callback);
    }
}
