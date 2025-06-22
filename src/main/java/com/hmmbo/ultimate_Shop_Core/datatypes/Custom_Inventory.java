package com.hmmbo.ultimate_Shop_Core.datatypes;

import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplate;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class Custom_Inventory implements InventoryHolder {

    private final ShopTemplate template;
    private final org.bukkit.inventory.ItemStack dynamicItem;
    private final double buyPrice;
    private final double sellPrice;
    private int amount = 1;

    public Custom_Inventory(ShopTemplate template) {
        this(template, null, 0, 0);
    }

    public Custom_Inventory(ShopTemplate template, org.bukkit.inventory.ItemStack item, double buyPrice, double sellPrice) {
        this.template = template;
        this.dynamicItem = item;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    @Override
    public Inventory getInventory() {
        if (dynamicItem != null) {
            return template.createInventory(dynamicItem, buyPrice, sellPrice);
        }
        return template.createInventory();
    }

    public ShopTemplate getTemplate() {
        return template;
    }

    public org.bukkit.inventory.ItemStack getDynamicItem() {
        return dynamicItem;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public int getAmount() {
        return amount;
    }

    public void addAmount(int add) {
        this.amount += add;
        if (this.amount < 1) this.amount = 1;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(1, amount);
    }
}