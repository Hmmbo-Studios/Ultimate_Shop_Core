package com.hmmbo.ultimate_Shop_Core.utils.sign;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenSignEditor;
import com.hmmbo.ultimate_Shop_Core.UltimateShopCore;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Utility for prompting players with a sign editor and retrieving the input.
 * Uses PacketEvents for minimal packet handling.
 */
public class SignInput {
    private static final Map<UUID, Consumer<String[]>> callbacks = new ConcurrentHashMap<>();
    private static boolean registered = false;

    /**
     * Initialise the packet listener. Call this once in plugin onEnable.
     */
    public static void init(Plugin plugin) {
        if (registered) return;

        PacketEvents.getAPI().getEventManager().registerListener(new PacketListener() {
            @Override
            public void onPacketReceive(PacketReceiveEvent event) {
                if (event.getPacketType() != PacketType.Play.Client.UPDATE_SIGN) return;
                Player player = (Player) event.getPlayer();
                Consumer<String[]> cb = callbacks.remove(player.getUniqueId());
                if (cb != null) {
                    WrapperPlayClientUpdateSign wrapper = new WrapperPlayClientUpdateSign(event);
                    cb.accept(wrapper.getLines());
                    event.setCancelled(true);
                }
            }
        });
        registered = true;
    }

    /**
     * Opens a sign editor for the player. The callback is invoked with the entered lines.
     */
    public static void open(Player player, Consumer<String[]> callback) {
        if (!registered) {
            init(UltimateShopCore.instance);
        }

        // Use an off-screen position so no sign block is shown to the player.
        Location loc = new Location(player.getWorld(), 0, 0, 0);
        WrapperPlayServerOpenSignEditor packet = new WrapperPlayServerOpenSignEditor(loc);
        PacketEvents.getAPI().getServerManager().sendPacket(player, packet);

        callbacks.put(player.getUniqueId(), callback);
    }
}
