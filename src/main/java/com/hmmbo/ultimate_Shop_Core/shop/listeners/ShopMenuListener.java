package com.hmmbo.ultimate_Shop_Core.shop.listeners;

import com.hmmbo.ultimate_Shop_Core.Ultimate_Shop_Core;
import com.hmmbo.ultimate_Shop_Core.datatypes.Custom_Inventory;
import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplate;
import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplateItemStack;
import com.hmmbo.ultimate_Shop_Core.shop.managers.ShopTemplateManager;
import com.hmmbo.ultimate_Shop_Core.utils.sign.SignInput;
import org.bukkit.ChatColor;
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
                        Inventory newInv = bs.createInventory(clicked, buy, sell);
                        Custom_Inventory newHolder = (Custom_Inventory) newInv.getHolder();
                        applyMode(newInv, newHolder.isStackMode(), bs.hasChangeMode());
                        player.openInventory(newInv);
                    }
                }
                case ADD1 -> shopHolder.addAmount(1);
                case ADD8 -> shopHolder.addAmount(8);
                case ADD16 -> shopHolder.addAmount(16);
                case ADD32 -> shopHolder.addAmount(32);
                case ADD1_STACK -> shopHolder.addAmount(64);
                case ADD8_STACK -> shopHolder.addAmount(8 * 64);
                case ADD16_STACK -> shopHolder.addAmount(16 * 64);
                case ADD32_STACK -> shopHolder.addAmount(32 * 64);
                case CHANGE_MODE -> {
                    boolean mode = shopHolder.toggleStackMode();
                    Inventory newInv = template.createInventory(shopHolder.getDynamicItem(), shopHolder.getBuyPrice(), shopHolder.getSellPrice());
                    Custom_Inventory newHolder = (Custom_Inventory) newInv.getHolder();
                    newHolder.setAmount(shopHolder.getAmount());
                    newHolder.setStackMode(mode);
                    applyMode(newInv, mode, template.hasChangeMode());
                    player.openInventory(newInv);
                }
                case INPUT -> {
                    SignInput.open(player, lines -> {
                        if (lines.length == 0) return;
                        try {
                            int amt = Integer.parseInt(lines[0]);
                            shopHolder.setAmount(amt);
                        } catch (NumberFormatException ignored) {
                            player.sendMessage("Invalid amount");
                        }
                    });
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

    private void applyMode(Inventory inv, boolean stackMode, boolean hasToggle) {
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null) continue;
            ShopTemplateItemStack.Type t = ShopTemplateItemStack.extractType(item);
            if (t == null) continue;
            boolean show = true;
            String name = null;
            switch (t) {
                case ADD1 -> name = ChatColor.GREEN + "Add 1";
                case ADD8 -> name = ChatColor.GREEN + "Add 8";
                case ADD16 -> name = ChatColor.GREEN + "Add 16";
                case ADD32 -> name = ChatColor.GREEN + "Add 32";
                case ADD1_STACK -> {
                    name = ChatColor.GREEN + "Add 1 Stack";
                    if (hasToggle && !stackMode) show = false;
                }
                case ADD8_STACK -> {
                    name = ChatColor.GREEN + "Add 8 Stacks";
                    if (hasToggle && !stackMode) show = false;
                }
                case ADD16_STACK -> {
                    name = ChatColor.GREEN + "Add 16 Stacks";
                    if (hasToggle && !stackMode) show = false;
                }
                case ADD32_STACK -> {
                    name = ChatColor.GREEN + "Add 32 Stacks";
                    if (hasToggle && !stackMode) show = false;
                }
                default -> {}
            }

            if (hasToggle) {
                switch (t) {
                    case ADD1, ADD8, ADD16, ADD32 -> {
                        if (stackMode) show = false;
                    }
                    default -> {}
                }
            }

            if (!show) {
                inv.setItem(i, null);
                continue;
            }

            if (name != null) {
                var meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(name);
                    item.setItemMeta(meta);
                }
                inv.setItem(i, item);
            }

            if (t == ShopTemplateItemStack.Type.CHANGE_MODE && hasToggle) {
                var meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(stackMode ? ChatColor.YELLOW + "Items" : ChatColor.YELLOW + "Stacks");
                    item.setItemMeta(meta);
                    inv.setItem(i, item);
                }
            }
        }
    }
}
