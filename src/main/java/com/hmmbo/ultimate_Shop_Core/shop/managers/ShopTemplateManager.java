package com.hmmbo.ultimate_Shop_Core.shop.managers;

import com.hmmbo.ultimate_Shop_Core.UltimateShopCore;
import com.hmmbo.ultimate_Shop_Core.datatypes.Range;
import com.hmmbo.ultimate_Shop_Core.shop.template.*;
import com.hmmbo.ultimate_Shop_Core.utils.ItemUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopTemplateManager {

    private final JavaPlugin plugin;
    private final File templateFolder;
    public static final HashMap<String, ShopTemplate> cache = new HashMap<>();
    private static ShopTemplateManager instance;

    public ShopTemplateManager(UltimateShopCore plugin) {
        this.plugin = plugin;
        this.templateFolder = new File(plugin.getDataFolder(), "templates");

        if (!templateFolder.exists()) {
            templateFolder.mkdirs();
            plugin.getLogger().info("Created templates folder: " + templateFolder.getPath());
        }
        loadAllTemplates();
        instance = this;
    }

    public static ShopTemplateManager get() {
        return instance;
    }

    public ShopTemplate getTemplate(String folderName, String fileName) {
        if (fileName.endsWith(".yml")) {
            fileName = fileName.substring(0, fileName.length() - 4);
        }
        String key = folderName + "/" + fileName;

        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        File folder = new File(templateFolder, folderName);
        if (!folder.exists()) {
            plugin.getLogger().warning("Template folder not found: " + folder.getPath());
            return null;
        }

        File file = new File(folder, fileName + ".yml");
        if (!file.exists()) {
            plugin.getLogger().warning("Template file not found: " + file.getPath());
            return null;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        int rows = config.getInt("rows", 4);
        String inventoryName = config.getString("inventory_name", fileName);
        String typeStr = config.getString("type", "GUI_SHOP");
        ShopTemplate.Type guiType = ShopTemplate.Type.fromString(typeStr);
        if (guiType == null) {
            plugin.getLogger().warning("Unknown shop type '" + typeStr + "' in " + file.getName() + ", defaulting to GUI_SHOP");
            guiType = ShopTemplate.Type.GUI_SHOP;
        }

        ShopTemplate template;
        if ("buy_sell".equals(fileName)) {
            template = new BuySellTemplate(rows, key, inventoryName, guiType);
        } else if (folderName.contains("categories")) {
            template = new CategoryTemplate(rows, key, inventoryName, guiType);
        } else {
            template = new ShopMenuTemplate(rows, key, inventoryName, guiType);
        }

        if (config.isList("items")) {
            List<?> itemList = config.getList("items");
            if (itemList != null) {
                for (int idx = 0; idx < itemList.size(); idx++) {
                    Object raw = itemList.get(idx);
                    if (!(raw instanceof Map<?, ?> map)) continue;

                    Object typeObj = map.get("type");
                    boolean typeSpecified = typeObj != null;
                    String itemTypeStr = typeSpecified ? typeObj.toString() : "DECORATION";

                    ItemStack itemStack;
                    try {
                        itemStack = ItemUtil.parseItem(map.get("item"));
                        if (itemStack == null) throw new IllegalArgumentException("ItemStack is null");
                    } catch (Exception e) {
                        plugin.getLogger().warning("Missing or invalid item in " + fileName + ": " + e.getMessage());
                        continue;
                    }

                    double buyPrice = map.containsKey("buy_price") ? Double.parseDouble(map.get("buy_price").toString()) : 0;
                    double sellPrice = map.containsKey("sell_price") ? Double.parseDouble(map.get("sell_price").toString()) : 0;

                    String category = null;
                    if ("CATEGORY".equalsIgnoreCase(itemTypeStr)) {
                        Object catObj = map.get("category");
                        if (catObj != null) category = catObj.toString();
                    }

                    List<Range> ranges = Range.parseFromObject(map.get("slot"));
                    for (Range range : ranges) {
                        for (int i = range.getStart(); i <= range.getEnd(); i++) {
                            template.addItem(createItem(template, itemTypeStr, typeSpecified, itemStack.clone(), i, category, buyPrice, sellPrice));
                        }
                    }
                }
            }
        } else if (config.isConfigurationSection("items")) {
            for (String keyItem : config.getConfigurationSection("items").getKeys(false)) {
                String rootPath = "items." + keyItem;

                boolean typeSpecified = config.contains(rootPath + ".type");
                String itemTypeStr = typeSpecified ? config.getString(rootPath + ".type") : "DECORATION";

                ItemStack itemStack;
                try {
                    itemStack = ItemUtil.parseItem(config.get(rootPath + ".item"));
                    if (itemStack == null) throw new IllegalArgumentException("ItemStack is null");
                } catch (Exception e) {
                    plugin.getLogger().warning("Missing or invalid item in " + fileName + ": " + e.getMessage());
                    continue;
                }

                String category = null;
                if ("CATEGORY".equalsIgnoreCase(itemTypeStr)) {
                    category = config.getString(rootPath + ".category");
                }

                double buyPrice = config.contains(rootPath + ".buy_price") ? config.getDouble(rootPath + ".buy_price") : 0;
                double sellPrice = config.contains(rootPath + ".sell_price") ? config.getDouble(rootPath + ".sell_price") : 0;

                List<Range> ranges = Range.parseFromYaml(config, rootPath + ".slot");
                for (Range range : ranges) {
                    for (int i = range.getStart(); i <= range.getEnd(); i++) {
                        template.addItem(createItem(template, itemTypeStr, typeSpecified, itemStack.clone(), i, category, buyPrice, sellPrice));
                    }
                }
            }
        }

        cache.put(key, template);
        return template;
    }

    private ShopTemplateItemStack createItem(ShopTemplate template, String typeName, boolean typeSpecified, ItemStack stack, int index, String category, double buy, double sell) {
        try {
            String normalized = typeName.toUpperCase().replace(' ', '_');
            if (template instanceof BuySellTemplate) {
                BuySellItemStack.BuySellType t = BuySellItemStack.BuySellType.valueOf(normalized);
                BuySellItemStack item = new BuySellItemStack(stack, t, index, buy, sell);
                if (!typeSpecified) item.setDynamicItem(true);
                return item;
            } else if (template instanceof CategoryTemplate) {
                CategoryItemStack.CategoryType t = CategoryItemStack.CategoryType.valueOf(normalized);
                return new CategoryItemStack(stack, t, index, buy, sell);
            } else {
                ShopMenuItemStack.MenuType t = ShopMenuItemStack.MenuType.valueOf(normalized);
                return new ShopMenuItemStack(stack, t, index, category);
            }
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().warning("Unknown item type '" + typeName + "' in template " + template.getName());
            ShopTemplateItemStack item = new ShopTemplateItemStack(stack, ShopTemplateItemStack.Type.DECORATION, index, category, buy, sell);
            if (template instanceof BuySellTemplate && !typeSpecified) item.setDynamicItem(true);
            return item;
        }
    }



    private void loadAllTemplates() {
        scanFolder("");
        plugin.getLogger().info("Loaded " + cache.size() + " shop templates.");
    }

    private void scanFolder(String path) {
        File folder = path.isEmpty() ? templateFolder : new File(templateFolder, path);
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                String sub = path.isEmpty() ? file.getName() : path + "/" + file.getName();
                scanFolder(sub);
            } else if (file.getName().endsWith(".yml")) {
                String yamlName = file.getName().replace(".yml", "");
                String key = (path.isEmpty() ? "" : path + "/") + yamlName;
                if (!cache.containsKey(key)) {
                    getTemplate(path, yamlName);
                }
            }
        }
    }



}
