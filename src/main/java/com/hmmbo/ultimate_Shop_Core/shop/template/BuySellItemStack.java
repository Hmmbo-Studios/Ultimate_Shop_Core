package com.hmmbo.ultimate_Shop_Core.shop.template;

import org.bukkit.inventory.ItemStack;

public class BuySellItemStack extends ShopTemplateItemStack {
    public enum BuySellType {
        ADD1,
        ADD16,
        ADD32,
        ADD64,
        BUY,
        SELL,
        BUY_STACK,
        SELL_STACK,
        INPUT,
        CHANGE_MODE,
        SHOP_ITEM,
        CLOSE,
        BACK,
        DECORATION
    }

    public BuySellItemStack(ItemStack itemStack, BuySellType type, int index, double buyPrice, double sellPrice) {
        super(itemStack, Type.valueOf(type.name()), index, null, buyPrice, sellPrice);
    }
}
