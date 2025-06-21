package com.hmmbo.ultimate_Shop_Core.datatypes;

import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplate;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class Custom_Inventory implements InventoryHolder {

    private final ShopTemplate template;
    private Inventory inventory;

    public Custom_Inventory(ShopTemplate template) {
        this.template = template;
    }

    @Override
    public Inventory getInventory() {
        if (inventory == null) {
            inventory = template.createInventory(this);
        }
        return inventory;
    }

    public ShopTemplate getTemplate() {
        return template;
    }
}