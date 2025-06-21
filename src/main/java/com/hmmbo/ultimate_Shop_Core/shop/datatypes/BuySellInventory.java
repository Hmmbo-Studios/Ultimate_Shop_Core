package com.hmmbo.ultimate_Shop_Core.shop.datatypes;

import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplate;
import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplateItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class BuySellInventory implements InventoryHolder {

    private final ShopTemplate template;
    private final ShopTemplateItemStack shopItem;
    private final ShopTemplate parentTemplate;
    private int amount = 1;
    private int displaySlot = -1;

    public BuySellInventory(ShopTemplate template, ShopTemplateItemStack item, ShopTemplate parentTemplate) {
        this.template = template;
        this.shopItem = item;
        this.parentTemplate = parentTemplate;
    }

    @Override
    public Inventory getInventory() {
        return template.createInventory(this);
    }

    public ShopTemplateItemStack getShopItem() {
        return shopItem;
    }

    public ShopTemplate getParentTemplate() {
        return parentTemplate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(1, amount);
    }

    public int getDisplaySlot() {
        return displaySlot;
    }

    public void setDisplaySlot(int displaySlot) {
        this.displaySlot = displaySlot;
    }
}
