package com.hmmbo.ultimate_Shop_Core.utils.commands;


import com.hmmbo.ultimate_Shop_Core.Ultimate_Shop_Core;
import com.hmmbo.ultimate_Shop_Core.shop.managers.ShopTemplateManager;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Collections;
import java.util.List;

@CommandPermission("")
@Command("shop")
public class ShopCommand {

        @Command("shop")
        public void onBaseCommand(BukkitCommandActor sender) {
//
         //   new AnvilGUI.Builder()
//                    .onClick((slot, state) -> {
//                        if (slot != AnvilGUI.Slot.OUTPUT) return Collections.emptyList();
//
//                        String input = state.getText();
//
//                        try {
//                           state.getPlayer().sendMessage(input);
//                        } catch (Exception e) {
//                            state.getPlayer().sendMessage("Invalid format or error saving.");
//                            return Collections.emptyList();
//                        }
//
//                        return List.of(
//                                AnvilGUI.ResponseAction.close()
//                        );
//                    })
//                    .onClose(state -> sender.asPlayer().sendMessage("Closed Inventory"))
//                    .text("5-10 or 8")
//                    .title("Enter Xp Drop Range")
//                    .plugin(Ultimate_Shop_Core.instance)
//                    .open(sender.asPlayer());


            sender.asPlayer().sendMessage("Welcome to the shop!");
        }

    @Command("shop")
    public void onBasedCommand(BukkitCommandActor sender, String name) {
            Ultimate_Shop_Core.instance.getLogger().info(ShopTemplateManager.cache.toString());
        sender.asPlayer().openInventory(ShopTemplateManager.get().getTemplate(name, "shop").createInventory());
    }

        @Subcommand("buy")
        public void onBuyCommand(CommandSender sender) {
            sender.sendMessage("You chose to buy something!");
        }

        @Subcommand("sell")
        public void onSellCommand(CommandSender sender) {
            sender.sendMessage("You chose to sell something!");
        }



}
