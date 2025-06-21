package com.hmmbo.ultimate_Shop_Core.datatypes;

import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplate;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class Custom_Inventory implements InventoryHolder {

    private final ShopTemplate template;

    public Custom_Inventory(ShopTemplate template) {
        this.template = template;
    }

    @Override
    public Inventory getInventory() {
        return template.createInventory(); // optional
    }

    public ShopTemplate getTemplate() {
        return template;
    }
}