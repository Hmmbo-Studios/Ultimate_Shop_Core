package com.hmmbo.ultimate_Shop_Core.shop.template;

import org.bukkit.inventory.ItemStack;

public class ShopMenuItemStack extends ShopTemplateItemStack {
    public enum MenuType {
        DECORATION,
        NEXT,
        PREV,
        CLOSE,
        BACK,
        CATEGORY
    }

    public ShopMenuItemStack(ItemStack itemStack, MenuType type, int index, String category) {
        super(itemStack, Type.valueOf(type.name()), index, category, 0, 0);
    }
}
