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
    private boolean stackMode = false;
    private boolean toggleMode = false;
    private int buySlot = -1;
    private int sellSlot = -1;
    private int buyStackSlot = -1;
    private int sellStackSlot = -1;
    private int changeModeSlot = -1;
    private ShopTemplateItemStack buyItem;
    private ShopTemplateItemStack sellItem;
    private ShopTemplateItemStack buyStackItem;
    private ShopTemplateItemStack sellStackItem;

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

    public boolean isStackMode() {
        return stackMode;
    }

    public void setStackMode(boolean stackMode) {
        this.stackMode = stackMode;
    }

    public boolean hasToggleMode() {
        return toggleMode;
    }

    public void setToggleMode(boolean toggleMode) {
        this.toggleMode = toggleMode;
    }

    public int getBuySlot() {
        return buySlot;
    }

    public void setBuySlot(int buySlot) {
        this.buySlot = buySlot;
    }

    public int getSellSlot() {
        return sellSlot;
    }

    public void setSellSlot(int sellSlot) {
        this.sellSlot = sellSlot;
    }

    public int getBuyStackSlot() {
        return buyStackSlot;
    }

    public void setBuyStackSlot(int buyStackSlot) {
        this.buyStackSlot = buyStackSlot;
    }

    public int getSellStackSlot() {
        return sellStackSlot;
    }

    public void setSellStackSlot(int sellStackSlot) {
        this.sellStackSlot = sellStackSlot;
    }

    public int getChangeModeSlot() {
        return changeModeSlot;
    }

    public void setChangeModeSlot(int changeModeSlot) {
        this.changeModeSlot = changeModeSlot;
    }

    public ShopTemplateItemStack getBuyItem() {
        return buyItem;
    }

    public void setBuyItem(ShopTemplateItemStack buyItem) {
        this.buyItem = buyItem;
    }

    public ShopTemplateItemStack getSellItem() {
        return sellItem;
    }

    public void setSellItem(ShopTemplateItemStack sellItem) {
        this.sellItem = sellItem;
    }

    public ShopTemplateItemStack getBuyStackItem() {
        return buyStackItem;
    }

    public void setBuyStackItem(ShopTemplateItemStack buyStackItem) {
        this.buyStackItem = buyStackItem;
    }

    public ShopTemplateItemStack getSellStackItem() {
        return sellStackItem;
    }

    public void setSellStackItem(ShopTemplateItemStack sellStackItem) {
        this.sellStackItem = sellStackItem;
    }
}
