package com.hmmbo.ultimate_Shop_Core.shop.listeners;

import com.hmmbo.ultimate_Shop_Core.datatypes.Custom_Inventory;
import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplate;
import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplateItemStack;
import com.hmmbo.ultimate_Shop_Core.shop.managers.ShopTemplateManager;
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
            ShopTemplateItemStack.Type type = ShopTemplateItemStack.extractType(event.getCurrentItem());
            event.setCancelled(true);

            if (type == null) return;

            switch (type) {
                case CATEGORY -> {
                    String category = ShopTemplateItemStack.extractCategory(event.getCurrentItem());
                    if (category == null) return;
                    String folder = template.getName().split("/")[0];
                    ShopTemplate cat = ShopTemplateManager.get().getTemplate(folder, category);
                    if (cat != null) {
                        event.getWhoClicked().openInventory(cat.createInventory());
                    }
                }
                case BACK -> {
                    String folder = template.getName().split("/")[0];
                    ShopTemplate rootTemplate = ShopTemplateManager.get().getTemplate(folder, "shop");
                    if (rootTemplate != null) {
                        event.getWhoClicked().openInventory(rootTemplate.createInventory());
                    }
                }
                case CLOSE -> event.getWhoClicked().closeInventory();
                default -> {
                }
            }
        }
    }
}
