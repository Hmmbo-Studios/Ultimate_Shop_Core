package com.hmmbo.ultimate_Shop_Core.shop.template;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ShopTemplateItemStack {

    private static final NamespacedKey TYPE_KEY = new NamespacedKey("ultimate_shop_core", "template_type");
    private static final NamespacedKey CATEGORY_KEY = new NamespacedKey("ultimate_shop_core", "template_category");
    private static final NamespacedKey ACTION_KEY = new NamespacedKey("ultimate_shop_core", "template_action");
    private static final NamespacedKey BUY_KEY = new NamespacedKey("ultimate_shop_core", "buy_price");
    private static final NamespacedKey SELL_KEY = new NamespacedKey("ultimate_shop_core", "sell_price");

    private ItemStack itemStack;
    private Type type;
    private int index;
    private String category;
    private String action;
    private double buyPrice;
    private double sellPrice;

    public enum Type {
        DECORATION,
        NEXT,
        PREV,
        CLOSE,
        BACK,
        SHOP_ITEM,
        CATEGORY,
        ACTION,
        SELECTED_ITEM;

        public static Type fromString(String s) {
            try {
                return Type.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    public ShopTemplateItemStack(ItemStack itemStack, Type type, int index) {
        this(itemStack, type, index, null, null, 0, 0);
    }

    public ShopTemplateItemStack(ItemStack itemStack, Type type, int index, String category) {
        this(itemStack, type, index, category, null, 0, 0);
    }

    public ShopTemplateItemStack(ItemStack itemStack, Type type, int index, String category, String action, double buyPrice, double sellPrice) {
        this.itemStack = itemStack;
        this.type = type;
        this.index = index;
        this.category = category;
        this.action = action;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        storeData(itemStack, type, category, action, buyPrice, sellPrice);
    }

    private void storeData(ItemStack item, Type type, String category, String action, double buyPrice, double sellPrice) {
        if (item == null || type == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        meta.getPersistentDataContainer().set(TYPE_KEY, PersistentDataType.STRING, type.name());
        if (category != null) {
            meta.getPersistentDataContainer().set(CATEGORY_KEY, PersistentDataType.STRING, category);
        }
        if (action != null) {
            meta.getPersistentDataContainer().set(ACTION_KEY, PersistentDataType.STRING, action);
        }
        meta.getPersistentDataContainer().set(BUY_KEY, PersistentDataType.DOUBLE, buyPrice);
        meta.getPersistentDataContainer().set(SELL_KEY, PersistentDataType.DOUBLE, sellPrice);
        item.setItemMeta(meta);
    }

    public static Type extractType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container.has(TYPE_KEY, PersistentDataType.STRING)) {
            String typeStr = container.get(TYPE_KEY, PersistentDataType.STRING);
            return Type.fromString(typeStr);
        }
        return null;
    }

    public static String extractAction(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(ACTION_KEY, PersistentDataType.STRING)) {
            return container.get(ACTION_KEY, PersistentDataType.STRING);
        }
        return null;
    }

    public static double extractBuyPrice(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(BUY_KEY, PersistentDataType.DOUBLE)) {
            return container.get(BUY_KEY, PersistentDataType.DOUBLE);
        }
        return 0;
    }

    public static double extractSellPrice(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container.has(SELL_KEY, PersistentDataType.DOUBLE)) {
            return container.get(SELL_KEY, PersistentDataType.DOUBLE);
        }
        return 0;
    }

    public static String extractCategory(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container.has(CATEGORY_KEY, PersistentDataType.STRING)) {
            return container.get(CATEGORY_KEY, PersistentDataType.STRING);
        }
        return null;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        storeData(itemStack, this.type, this.category, this.action, this.buyPrice, this.sellPrice); // re-store data
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
        storeData(this.itemStack, type, this.category, this.action, this.buyPrice, this.sellPrice); // re-store data
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
        storeData(this.itemStack, this.type, category, this.action, this.buyPrice, this.sellPrice);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
        storeData(this.itemStack, this.type, this.category, action, this.buyPrice, this.sellPrice);
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
        storeData(this.itemStack, this.type, this.category, this.action, buyPrice, this.sellPrice);
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
        storeData(this.itemStack, this.type, this.category, this.action, this.buyPrice, sellPrice);
    }
}
