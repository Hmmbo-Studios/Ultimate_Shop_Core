// File: SignInput.java
package com.hmmbo.ultimate_Shop_Core.utils.sign;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenSignEditor;
import com.github.retrooper.packetevents.util.Vector3i;
import com.hmmbo.ultimate_Shop_Core.Ultimate_Shop_Core;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Utility for prompting players with a virtual sign editor
 * (no block placed) and retrieving the input via callback.
 */
public class SignInput {
    private static final Map<UUID, Consumer<String[]>> callbacks = new ConcurrentHashMap<>();
    private static boolean initialized = false;

    // Called by SignPacketListener to fetch & remove the pending callback
    static Consumer<String[]> removeCallback(UUID uuid) {
        return callbacks.remove(uuid);
    }

    /**
     * Registers the SignPacketListener. Call once in your plugin's onEnable().
     */
    public static void init() {
        if (initialized) return;
        PacketEvents.getAPI()
                .getEventManager()
                .registerListener(new SignPacketListener());
        initialized = true;
    }

    /**
     * Opens a "virtual" sign editor for the player (no block is actually placed).
     * Once the player submits, the provided callback will be invoked with the four lines of text.
     */
    public static void open(Player player, Consumer<String[]> callback) {
        if (!initialized) {
            init();
        }

        // Store the callback before sending the packet to avoid races
        callbacks.put(player.getUniqueId(), callback);

        // Use an off-screen location so no sign is visible in-world
        Location loc = player.getLocation().add(0, 256, 0);
        Vector3i position = new Vector3i(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        // 'true' means editing the front text of the sign
        WrapperPlayServerOpenSignEditor packet =
                new WrapperPlayServerOpenSignEditor(position, true);

        // Send via PlayerManager instead of the removed ServerManager.sendPacket
        PacketEvents.getAPI()
                .getPlayerManager()
                .sendPacket(player, packet);
    }
}
