package com.hmmbo.ultimate_Shop_Core.utils.commands;


import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@CommandPermission("")
@Command("shop")
public class ShopCommand {



        @Subcommand("buy")
        public void onBuyCommand(CommandSender sender) {
            sender.sendMessage("You chose to buy something!");
        }

        @Subcommand("sell")
        public void onSellCommand(CommandSender sender) {
            sender.sendMessage("You chose to sell something!");
        }



}
