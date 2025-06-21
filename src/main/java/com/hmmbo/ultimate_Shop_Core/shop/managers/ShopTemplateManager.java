package com.hmmbo.ultimate_Shop_Core.shop.managers;

import com.hmmbo.ultimate_Shop_Core.Ultimate_Shop_Core;
import com.hmmbo.ultimate_Shop_Core.datatypes.Range;
import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplate;
import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplateItemStack;
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

    public ShopTemplateManager(Ultimate_Shop_Core plugin) {
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
        ShopTemplate template = new ShopTemplate(rows, key, inventoryName, guiType);

        if (config.isList("items")) {
            List<?> itemList = config.getList("items");
            if (itemList != null) {
                for (int idx = 0; idx < itemList.size(); idx++) {
                    Object raw = itemList.get(idx);
                    if (!(raw instanceof Map<?, ?> map)) continue;

                    Object typeObj = map.get("type");
                    String itemTypeStr = typeObj == null ? "DECORATION" : typeObj.toString();
                    ShopTemplateItemStack.Type type;
                    try {
                        type = ShopTemplateItemStack.Type.valueOf(itemTypeStr.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid type in " + fileName + ": " + itemTypeStr);
                        continue;
                    }

                    ItemStack itemStack;
                    try {
                        itemStack = ItemUtil.parseItem(map.get("item"));
                        if (itemStack == null) throw new IllegalArgumentException("ItemStack is null");
                    } catch (Exception e) {
                        plugin.getLogger().warning("Missing or invalid item in " + fileName + ": " + e.getMessage());
                        continue;
                    }

                    String category = null;
                    if (type == ShopTemplateItemStack.Type.CATEGORY) {
                        Object catObj = map.get("category");
                        if (catObj != null) category = catObj.toString();
                    }

                    List<Range> ranges = Range.parseFromObject(map.get("slot"));
                    for (Range range : ranges) {
                        for (int i = range.getStart(); i <= range.getEnd(); i++) {
                            template.addItem(new ShopTemplateItemStack(itemStack.clone(), type, i, category));
                        }
                    }
                }
            }
        } else if (config.isConfigurationSection("items")) {
            for (String keyItem : config.getConfigurationSection("items").getKeys(false)) {
                String rootPath = "items." + keyItem;

                String itemTypeStr = config.getString(rootPath + ".type", "DECORATION");
                ShopTemplateItemStack.Type type;
                try {
                    type = ShopTemplateItemStack.Type.valueOf(itemTypeStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid type in " + fileName + ": " + itemTypeStr);
                    continue;
                }

                ItemStack itemStack;
                try {
                    itemStack = ItemUtil.parseItem(config.get(rootPath + ".item"));
                    if (itemStack == null) throw new IllegalArgumentException("ItemStack is null");
                } catch (Exception e) {
                    plugin.getLogger().warning("Missing or invalid item in " + fileName + ": " + e.getMessage());
                    continue;
                }

                String category = null;
                if (type == ShopTemplateItemStack.Type.CATEGORY) {
                    category = config.getString(rootPath + ".category");
                }

                List<Range> ranges = Range.parseFromYaml(config, rootPath + ".slot");
                for (Range range : ranges) {
                    for (int i = range.getStart(); i <= range.getEnd(); i++) {
                        template.addItem(new ShopTemplateItemStack(itemStack.clone(), type, i, category));
                    }
                }
            }
        }

        cache.put(key, template);
        return template;
    }



    private void loadAllTemplates() {
        File[] folders = templateFolder.listFiles(File::isDirectory);
        if (folders == null) return;

        for (File folder : folders) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
            if (files == null) continue;

            for (File file : files) {
                String yamlName = file.getName().replace(".yml", "");
                String key = folder.getName() + "/" + yamlName;
                if (!cache.containsKey(key)) {
                    getTemplate(folder.getName(), yamlName);
                }
            }
        }

        plugin.getLogger().info("Loaded " + cache.size() + " shop templates.");
    }



}
