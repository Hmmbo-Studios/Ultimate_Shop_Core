package com.hmmbo.ultimate_Shop_Core.shop.listeners;

import com.hmmbo.ultimate_Shop_Core.Ultimate_Shop_Core;
import com.hmmbo.ultimate_Shop_Core.datatypes.Custom_Inventory;
import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplate;
import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplateItemStack;
import com.hmmbo.ultimate_Shop_Core.shop.managers.ShopTemplateManager;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopMenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();

        if (inv.getHolder() instanceof Custom_Inventory shopHolder) {
            if (event.getCurrentItem() == null) {
                event.setCancelled(true);
                return;
            }
            ShopTemplate template = shopHolder.getTemplate();
            ShopTemplateItemStack.Type type = ShopTemplateItemStack.extractType(event.getCurrentItem());
            event.setCancelled(true);

            if (type == null) return;

            Player player = (Player) event.getWhoClicked();

            switch (type) {
                case CATEGORY -> {
                    String category = ShopTemplateItemStack.extractCategory(event.getCurrentItem());
                    if (category == null) return;
                    String folder = template.getName().split("/")[0];
                    ShopTemplate cat = ShopTemplateManager.get().getTemplate(folder, category);
                    if (cat != null) {
                        player.openInventory(cat.createInventory());
                    }
                }
                case SHOP_ITEM -> {
                    ItemStack clicked = event.getCurrentItem();
                    double buy = ShopTemplateItemStack.extractBuyPrice(clicked);
                    double sell = ShopTemplateItemStack.extractSellPrice(clicked);
                    String folder = template.getName().split("/")[0];
                    ShopTemplate bs = ShopTemplateManager.get().getTemplate(folder, "buy_sell");
                    if (bs != null) {
                        player.openInventory(bs.createInventory(clicked, buy, sell));
                    }
                }
                case ADD1 -> shopHolder.addAmount(1);
                case ADD16 -> shopHolder.addAmount(16);
                case ADD32 -> shopHolder.addAmount(32);
                case ADD64 -> shopHolder.addAmount(64);
                case INPUT -> {
                    new AnvilGUI.Builder()
                            .onClick((slot, state) -> {
                                if (slot != AnvilGUI.Slot.OUTPUT) return java.util.Collections.emptyList();
                                try {
                                    int amt = Integer.parseInt(state.getText());
                                    shopHolder.setAmount(amt);
                                } catch (NumberFormatException ignored) {
                                    player.sendMessage("Invalid amount");
                                }
                                return java.util.List.of(AnvilGUI.ResponseAction.close());
                            })
                            .onClose(state -> player.sendMessage("Closed Inventory"))
                            .text("5-10 or 8")
                            .title("Enter Amount")
                            .plugin(Ultimate_Shop_Core.instance)
                            .open(player);
                }
                case BUY -> {
                    if (Ultimate_Shop_Core.economy != null) {
                        double cost = shopHolder.getBuyPrice() * shopHolder.getAmount();
                        if (Ultimate_Shop_Core.economy.getBalance(player) >= cost) {
                            Ultimate_Shop_Core.economy.withdrawPlayer(player, cost);
                            ItemStack item = shopHolder.getDynamicItem().clone();
                            item.setAmount(shopHolder.getAmount());
                            player.getInventory().addItem(item);
                            player.sendMessage("You bought " + shopHolder.getAmount() + " " + item.getType() + " for $" + cost);
                        } else {
                            player.sendMessage("Not enough money!");
                        }
                    }
                }
                case BUY_STACK -> {
                    if (Ultimate_Shop_Core.economy != null) {
                        int amt = 64;
                        double cost = shopHolder.getBuyPrice() * amt;
                        if (Ultimate_Shop_Core.economy.getBalance(player) >= cost) {
                            Ultimate_Shop_Core.economy.withdrawPlayer(player, cost);
                            ItemStack item = shopHolder.getDynamicItem().clone();
                            item.setAmount(amt);
                            player.getInventory().addItem(item);
                            player.sendMessage("You bought " + amt + " " + item.getType() + " for $" + cost);
                        } else {
                            player.sendMessage("Not enough money!");
                        }
                    }
                }
                case SELL -> {
                    int amt = shopHolder.getAmount();
                    ItemStack check = new ItemStack(shopHolder.getDynamicItem().getType(), amt);
                    if (player.getInventory().containsAtLeast(check, amt)) {
                        player.getInventory().removeItem(check);
                        double gain = shopHolder.getSellPrice() * amt;
                        if (Ultimate_Shop_Core.economy != null) {
                            Ultimate_Shop_Core.economy.depositPlayer(player, gain);
                        }
                        player.sendMessage("You sold " + amt + " " + check.getType() + " for $" + gain);
                    } else {
                        player.sendMessage("You don't have enough items!");
                    }
                }
                case SELL_STACK -> {
                    int amt = 64;
                    ItemStack check = new ItemStack(shopHolder.getDynamicItem().getType(), amt);
                    if (player.getInventory().containsAtLeast(check, amt)) {
                        player.getInventory().removeItem(check);
                        double gain = shopHolder.getSellPrice() * amt;
                        if (Ultimate_Shop_Core.economy != null) {
                            Ultimate_Shop_Core.economy.depositPlayer(player, gain);
                        }
                        player.sendMessage("You sold " + amt + " " + check.getType() + " for $" + gain);
                    } else {
                        player.sendMessage("You don't have enough items!");
                    }
                }
                case BACK -> {
                    String folder = template.getName().split("/")[0];
                    ShopTemplate rootTemplate = ShopTemplateManager.get().getTemplate(folder, "shop");
                    if (rootTemplate != null) {
                        player.openInventory(rootTemplate.createInventory());
                    }
                }
                case CLOSE -> player.closeInventory();
                default -> {
                }
            }
        }
    }
}
