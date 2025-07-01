// File: SignPacketListener.java
package com.hmmbo.ultimate_Shop_Core.utils.sign;

import com.github.retrooper.packetevents.event.*;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUpdateSign;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class SignPacketListener extends PacketListenerCommon implements PacketListener {

    @Override
    public PacketListenerAbstract asAbstract(PacketListenerPriority priority) {
        return PacketListener.super.asAbstract(priority);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // Ensure the sender is a Player
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getPlayer();

        // Fetch and remove the pending callback
        Consumer<String[]> callback = SignInput.removeCallback(player.getUniqueId());
        if (callback == null) {
            return;
        }

        // Extract the submitted text lines and invoke the callback
        WrapperPlayClientUpdateSign wrapper = new WrapperPlayClientUpdateSign(event);
        callback.accept(wrapper.getTextLines());

        // Cancel the packet so no sign block is ever updated
        event.setCancelled(true);
    }
}
