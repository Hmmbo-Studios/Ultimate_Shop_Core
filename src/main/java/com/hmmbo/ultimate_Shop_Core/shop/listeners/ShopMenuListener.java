package com.hmmbo.ultimate_Shop_Core.shop.listeners;

import com.hmmbo.ultimate_Shop_Core.datatypes.Custom_Inventory;
import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplate;
import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplateItemStack;
import com.hmmbo.ultimate_Shop_Core.shop.managers.ShopTemplateManager;
import com.hmmbo.ultimate_Shop_Core.shop.datatypes.BuySellInventory;
import com.hmmbo.ultimate_Shop_Core.economy.VaultUtil;
import net.milkbowl.vault.economy.Economy;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class ShopMenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryView view = event.getView();
        Inventory top = view.getTopInventory();
        Inventory clicked = event.getClickedInventory();

        if (top.getHolder() instanceof Custom_Inventory shopHolder) {
            ShopTemplate template = shopHolder.getTemplate();
            ShopTemplateItemStack.Type type = ShopTemplateItemStack.extractType(event.getCurrentItem());
            event.setCancelled(true);
            
            if (type == null) return;

            switch (type) {
                case CATEGORY -> {
                    String category = ShopTemplateItemStack.extractCategory(event.getCurrentItem());
                    if (category == null) return;
                    String folder = template.getName().split("/")[0];
                    ShopTemplate cat = ShopTemplateManager.get().getTemplate(folder, category);
                    if (cat != null) {
                        event.getWhoClicked().openInventory(cat.createInventory());
                    }
                }
                case SHOP_ITEM -> {
                    String folder = template.getName().split("/")[0];
                    ShopTemplate buySell = ShopTemplateManager.get().getTemplate(folder, "buy_sell");
                    if (buySell != null) {
                        BuySellInventory holder = new BuySellInventory(
                                buySell,
                                new ShopTemplateItemStack(event.getCurrentItem().clone(), ShopTemplateItemStack.Type.SHOP_ITEM, 0, null, null,
                                        ShopTemplateItemStack.extractBuyPrice(event.getCurrentItem()),
                                        ShopTemplateItemStack.extractSellPrice(event.getCurrentItem())),
                                template);
                        Inventory newInv = buySell.createInventory(holder, event.getCurrentItem().clone());
                        for (ShopTemplateItemStack it : buySell.getItems()) {
                            if (it.getType() == ShopTemplateItemStack.Type.SELECTED_ITEM) {
                                holder.setDisplaySlot(it.getIndex());
                                newInv.setItem(it.getIndex(), event.getCurrentItem().clone());
                            }
                            if (it.getType() == ShopTemplateItemStack.Type.ACTION) {
                                String a = it.getAction();
                                if (a == null) continue;
                                switch (a.toLowerCase()) {
                                    case "buy" -> { holder.setBuySlot(it.getIndex()); holder.setBuyItem(it); }
                                    case "sell" -> { holder.setSellSlot(it.getIndex()); holder.setSellItem(it); }
                                    case "buy_stack" -> { holder.setBuyStackSlot(it.getIndex()); holder.setBuyStackItem(it); }
                                    case "sell_stack" -> { holder.setSellStackSlot(it.getIndex()); holder.setSellStackItem(it); }
                                    case "change_mode" -> { holder.setToggleMode(true); holder.setChangeModeSlot(it.getIndex()); }
                                }
                            }
                        }
                        if (holder.hasToggleMode()) {
                            if (holder.getBuyStackSlot() >= 0) newInv.setItem(holder.getBuyStackSlot(), null);
                            if (holder.getSellStackSlot() >= 0) newInv.setItem(holder.getSellStackSlot(), null);
                        }
                        event.getWhoClicked().openInventory(newInv);
                    }
                }
                case BACK -> {
                    String folder = template.getName().split("/")[0];
                    ShopTemplate rootTemplate = ShopTemplateManager.get().getTemplate(folder, "shop");
                    if (rootTemplate != null) {
                        event.getWhoClicked().openInventory(rootTemplate.createInventory());
                    }
                }
                case CLOSE -> event.getWhoClicked().closeInventory();
                default -> {
                }
            }
        } else if (top.getHolder() instanceof BuySellInventory buySellHolder) {
            event.setCancelled(true);
            ShopTemplateItemStack.Type type = ShopTemplateItemStack.extractType(event.getCurrentItem());
            String action = ShopTemplateItemStack.extractAction(event.getCurrentItem());
            if (type == null) return;
            Player player = (Player) event.getWhoClicked();
            switch (type) {
                case ACTION -> handleAction(player, buySellHolder, action, top);
                case BACK -> player.openInventory(buySellHolder.getParentTemplate().createInventory());
                case CLOSE -> player.closeInventory();
                default -> {
                }
            }
        }
    }

    private void handleAction(Player player, BuySellInventory holder, String action, Inventory inv) {
        if (action == null) return;
        switch (action.toLowerCase()) {
            case "add1" -> modifyAmount(holder, inv, 1);
            case "add16" -> modifyAmount(holder, inv, 16);
            case "add32" -> modifyAmount(holder, inv, 32);
            case "add64" -> modifyAmount(holder, inv, 64);
            case "buy_stack" -> processBuy(player, holder, 64);
            case "sell_stack" -> processSell(player, holder, 64);
            case "buy" -> processBuy(player, holder, holder.getAmount());
            case "sell" -> processSell(player, holder, holder.getAmount());
            case "input" -> openAnvil(player, holder, inv);
            case "change_mode" -> toggleMode(holder, inv);
            default -> {}
        }
    }

    private void modifyAmount(BuySellInventory holder, Inventory inv, int add) {
        holder.setAmount(holder.getAmount() + add);
        updateDisplay(inv, holder);
    }

    private void updateDisplay(Inventory inv, BuySellInventory holder) {
        if (holder.getDisplaySlot() < 0) return;
        ItemStack stack = holder.getShopItem().getItemStack().clone();
        int amt = Math.min(64, holder.getAmount());
        stack.setAmount(amt);
        inv.setItem(holder.getDisplaySlot(), stack);
    }

    private void toggleMode(BuySellInventory holder, Inventory inv) {
        if (!holder.hasToggleMode()) return;
        holder.setStackMode(!holder.isStackMode());
        if (holder.isStackMode()) {
            if (holder.getBuySlot() >= 0 && holder.getBuyStackItem() != null)
                inv.setItem(holder.getBuySlot(), holder.getBuyStackItem().getItemStack());
            if (holder.getSellSlot() >= 0 && holder.getSellStackItem() != null)
                inv.setItem(holder.getSellSlot(), holder.getSellStackItem().getItemStack());
        } else {
            if (holder.getBuySlot() >= 0 && holder.getBuyItem() != null)
                inv.setItem(holder.getBuySlot(), holder.getBuyItem().getItemStack());
            if (holder.getSellSlot() >= 0 && holder.getSellItem() != null)
                inv.setItem(holder.getSellSlot(), holder.getSellItem().getItemStack());
        }
    }

    private void processBuy(Player player, BuySellInventory holder, int amount) {
        Economy econ = VaultUtil.getEconomy();
        if (econ == null) return;
        double total = holder.getShopItem().getBuyPrice() * amount;
        if (!econ.has(player, total)) {
            player.sendMessage(ChatColor.RED + "Not enough money");
            return;
        }
        ItemStack item = holder.getShopItem().getItemStack().clone();
        item.setAmount(amount);
        if (!player.getInventory().addItem(item).isEmpty()) {
            player.sendMessage(ChatColor.RED + "Not enough inventory space");
            return;
        }
        econ.withdrawPlayer(player, total);
        player.sendMessage(ChatColor.GREEN + "You bought " + amount + " " + item.getType().toString().toLowerCase() + " for $" + total);
    }

    private void processSell(Player player, BuySellInventory holder, int amount) {
        Economy econ = VaultUtil.getEconomy();
        if (econ == null) return;
        ItemStack item = holder.getShopItem().getItemStack();
        ItemStack remove = item.clone();
        remove.setAmount(amount);
        if (!player.getInventory().containsAtLeast(item, amount)) {
            player.sendMessage(ChatColor.RED + "Not enough items");
            return;
        }
        player.getInventory().removeItem(remove);
        double total = holder.getShopItem().getSellPrice() * amount;
        econ.depositPlayer(player, total);
        player.sendMessage(ChatColor.GREEN + "You sold " + amount + " " + item.getType().toString().toLowerCase() + " for $" + total);
    }

    private void openAnvil(Player player, BuySellInventory holder, Inventory inv) {
        new AnvilGUI.Builder()
                .onClick((slot, state) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) return java.util.Collections.emptyList();
                    try {
                        int val = Integer.parseInt(state.getText());
                        holder.setAmount(val);
                        updateDisplay(inv, holder);
                    } catch (Exception e) {
                        player.sendMessage(ChatColor.RED + "Invalid number");
                    }
                    return java.util.List.of(AnvilGUI.ResponseAction.close());
                })
                .onClose(state -> player.sendMessage("Closed Inventory"))
                .text("5-10 or 8")
                .title("Enter Amount")
                .plugin(com.hmmbo.ultimate_Shop_Core.Ultimate_Shop_Core.instance)
                .open(player);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        InventoryView view = event.getView();
        Inventory top = view.getTopInventory();
        if (top.getHolder() instanceof Custom_Inventory || top.getHolder() instanceof BuySellInventory) {
            event.setCancelled(true);
        }
    }
}
