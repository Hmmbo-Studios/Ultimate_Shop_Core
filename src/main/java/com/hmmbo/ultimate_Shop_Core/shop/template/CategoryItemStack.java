package com.hmmbo.ultimate_Shop_Core.shop.template;

import org.bukkit.inventory.ItemStack;

public class CategoryItemStack extends ShopTemplateItemStack {
    public enum CategoryType {
        DECORATION,
        NEXT,
        PREV,
        CLOSE,
        BACK,
        SHOP_ITEM
    }

    public CategoryItemStack(ItemStack itemStack, CategoryType type, int index, double buyPrice, double sellPrice) {
        super(itemStack, Type.valueOf(type.name()), index, null, buyPrice, sellPrice);
    }
}
