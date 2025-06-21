package com.hmmbo.ultimate_Shop_Core.shop.template;

import com.hmmbo.ultimate_Shop_Core.datatypes.Custom_Inventory;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShopTemplate {

    private final int rows;
    private final String name;
    private final String inventoryName;
    private final List<ShopTemplateItemStack> items;
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
    }

    public List<ShopTemplateItemStack> getItems() {
        return items;
    }

    public Inventory createInventory() {
        Custom_Inventory holder = new Custom_Inventory(this);
        return holder.getInventory();
    }

    public Inventory createInventory(InventoryHolder holder) {
        return createInventory(holder, null);
    }

    public Inventory createInventory(InventoryHolder holder, ItemStack displayItem) {
        int size = rows * 9;
        String title = inventoryName != null ? inventoryName : name;
        Inventory inventory = Bukkit.createInventory(holder, size, title);
        for (ShopTemplateItemStack templateItem : items) {
            int index = templateItem.getIndex();
            ItemStack stack = templateItem.getItemStack();

            if (templateItem.getType() == ShopTemplateItemStack.Type.SELECTED_ITEM && displayItem != null) {
                stack = displayItem;
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
