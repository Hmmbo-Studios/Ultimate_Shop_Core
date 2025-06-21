package com.hmmbo.ultimate_Shop_Core.shop.listeners;

import com.hmmbo.ultimate_Shop_Core.datatypes.Custom_Inventory;
import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplate;
import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplateItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class ShopMenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();

        if (inv.getHolder() instanceof Custom_Inventory shopHolder) {
            ShopTemplate template = shopHolder.getTemplate();

            event.getWhoClicked().sendMessage("This is a Shop GUI: " + template.getName());
            event.getWhoClicked().sendMessage(ShopTemplateItemStack.extractType(
                    event.getCurrentItem()).toString());

            event.setCancelled(true);
        }
    }
}
