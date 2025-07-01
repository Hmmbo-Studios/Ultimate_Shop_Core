package com.hmmbo.ultimate_Shop_Core.utils.commands;


import com.hmmbo.ultimate_Shop_Core.UltimateShopCore;
import com.hmmbo.ultimate_Shop_Core.shop.managers.ShopTemplateManager;
import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplate;
import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@CommandPermission("")
@Command("shop")
public class ShopCommand {

    @Command("shop")
    public void onShopCommand(BukkitCommandActor sender) {
        String template = UltimateShopCore.instance.getConfig().getString("shop_template", "default");
        ShopTemplate shop = ShopTemplateManager.get().getTemplate(template, "shop");
        if (shop != null) {
            sender.asPlayer().openInventory(shop.createInventory());
        } else {
            sender.asPlayer().sendMessage("Shop template '" + template + "' not found.");
        }
    }

    @Command("shop admin change_template")
    @CommandPermission("usc.admin")
    public void changeTemplate(BukkitCommandActor sender, String name) {
        UltimateShopCore.instance.getConfig().set("shop_template", name);
        UltimateShopCore.instance.saveConfig();
        sender.asPlayer().sendMessage("Shop template changed to " + name + ".");
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
