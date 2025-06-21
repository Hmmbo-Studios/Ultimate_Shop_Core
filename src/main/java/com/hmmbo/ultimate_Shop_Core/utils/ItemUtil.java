package com.hmmbo.ultimate_Shop_Core.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility methods for building ItemStacks from simple map structures in YAML.
 */
public class ItemUtil {

    /**
     * Parses an item representation from configuration. Supports either a
     * pre-serialized ItemStack or a map with keys like {@code type},
     * {@code amount}, {@code display_name}, {@code lore} and {@code enchanted}.
     *
     * @param obj YAML object describing the item
     * @return constructed ItemStack or {@code null} if the data is invalid
     */
    @SuppressWarnings("unchecked")
    public static ItemStack parseItem(Object obj) {
        if (obj instanceof ItemStack stack) {
            return stack;
        }

        if (obj instanceof Map<?, ?> rawMap) {
            Map<Object, Object> map = (Map<Object, Object>) rawMap;

            Object typeObj = map.get("type");
            if (typeObj == null) return null;

            Material material = Material.matchMaterial(typeObj.toString());
            if (material == null) return null;

            int amount = 1;
            if (map.containsKey("amount")) {
                try {
                    amount = Integer.parseInt(map.get("amount").toString());
                } catch (NumberFormatException ignored) {
                }
            }

            ItemStack stack = new ItemStack(material, amount);
            ItemMeta meta = stack.getItemMeta();
            if (meta != null) {
                Object nameObj = map.get("display_name");
                if (nameObj == null) nameObj = map.get("display-name");
                if (nameObj != null) {
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', nameObj.toString()));
                }

                Object loreObj = map.get("lore");
                if (loreObj instanceof List<?> loreList) {
                    List<String> lore = new ArrayList<>();
                    for (Object line : loreList) {
                        lore.add(ChatColor.translateAlternateColorCodes('&', line.toString()));
                    }
                    meta.setLore(lore);
                }

                Object enchObj = map.get("enchanted");
                if (enchObj != null && Boolean.parseBoolean(enchObj.toString())) {
                    meta.addEnchant(Enchantment.DURABILITY, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                }

                stack.setItemMeta(meta);
            }
            return stack;
        }

        return null;
    }
}

