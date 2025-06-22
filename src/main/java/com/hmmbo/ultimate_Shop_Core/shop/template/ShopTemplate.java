package com.hmmbo.ultimate_Shop_Core.shop.template;

import com.hmmbo.ultimate_Shop_Core.datatypes.Custom_Inventory;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShopTemplate {

    private final int rows;
    private final String name;
    private final String inventoryName;
    private final List<ShopTemplateItemStack> items;
    private boolean hasChangeMode = false;
    public enum Type {
        GUI_SHOP,
        AUCTION,
        CHEST_SHOP;

        public static ShopTemplate.Type fromString(String s) {
            try {
                return ShopTemplate.Type.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

    }

    public ShopTemplate(int rows, String name, String inventoryName, Type type) {
        this.rows = rows;
        this.name = name;
        this.inventoryName = inventoryName;
        items = new ArrayList<>();
    }

    public void addItem(ShopTemplateItemStack item) {
        items.add(item);
        if (item.getType() == ShopTemplateItemStack.Type.CHANGE_MODE) {
            hasChangeMode = true;
        }
    }

    public List<ShopTemplateItemStack> getItems() {
        return items;
    }

    public boolean hasChangeMode() {
        return hasChangeMode;
    }

    public Inventory createInventory() {
        int size = rows * 9;
        String title = inventoryName != null ? inventoryName : name;
        Inventory inventory = Bukkit.createInventory(new Custom_Inventory(this), size, title);
        for (ShopTemplateItemStack templateItem : items) {
            int index = templateItem.getIndex();
            ItemStack stack = templateItem.getItemStack();

            if (index >= 0 && index < size) {
                inventory.setItem(index, stack);
            }
        }

        return inventory;
    }

    public Inventory createInventory(ItemStack dynamicItem, double buyPrice, double sellPrice) {
        int size = rows * 9;
        String title = inventoryName != null ? inventoryName : name;
        Inventory inventory = Bukkit.createInventory(new Custom_Inventory(this, dynamicItem, buyPrice, sellPrice), size, title);
        for (ShopTemplateItemStack templateItem : items) {
            int index = templateItem.getIndex();
            ItemStack stack = templateItem.getItemStack();
            if (dynamicItem != null && (templateItem.getType() == ShopTemplateItemStack.Type.SHOP_ITEM || templateItem.isDynamicItem())) {
                stack = dynamicItem.clone();
            }
            if (index >= 0 && index < size) {
                inventory.setItem(index, stack);
            }
        }
        return inventory;
    }

    public int getRows() {
        return rows;
    }

    public String getName() {
        return name;
    }

    public String getInventoryName() {
        return inventoryName;
    }
}
