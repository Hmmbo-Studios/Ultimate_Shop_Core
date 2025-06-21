package com.hmmbo.ultimate_Shop_Core.shop.managers;

import com.hmmbo.ultimate_Shop_Core.Ultimate_Shop_Core;
import com.hmmbo.ultimate_Shop_Core.datatypes.Range;
import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplate;
import com.hmmbo.ultimate_Shop_Core.shop.template.ShopTemplateItemStack;
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

    public ShopTemplateManager(Ultimate_Shop_Core plugin) {
        this.plugin = plugin;
        this.templateFolder = new File(plugin.getDataFolder(), "templates");

        if (!templateFolder.exists()) {
            templateFolder.mkdirs();
            plugin.getLogger().info("Created templates folder: " + templateFolder.getPath());
        }
        loadAllTemplates();
    }

    public ShopTemplate getTemplate(String fileName) {
        if (fileName.endsWith(".yml")) {
            fileName = fileName.substring(0, fileName.length() - 4);
        }

        if (cache.containsKey(fileName)) {
            return cache.get(fileName);
        }

        File file = new File(templateFolder, fileName + ".yml");
        if (!file.exists()) {
            plugin.getLogger().warning("Template file not found: " + file.getPath());
            return null;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        int rows = config.getInt("rows", 4);
        ShopTemplate.Type gui_type = ShopTemplate.Type.valueOf(config.getString("type","GUI_SHOP"));
        ShopTemplate template = new ShopTemplate(rows, fileName,gui_type);

        // Get section under "items"
        for (String key : config.getConfigurationSection("items").getKeys(false)) {
            String rootPath = "items." + key;

            // Parse type
            String typeStr = config.getString(rootPath + ".type", "DECORATION");
            ShopTemplateItemStack.Type type;
            try {
                type = ShopTemplateItemStack.Type.valueOf(typeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid type in " + fileName + ": " + typeStr);
                continue;
            }

            // Parse item
            ItemStack itemStack;
            try {
                itemStack = config.getItemStack(rootPath + ".item");
                if (itemStack == null) throw new IllegalArgumentException("ItemStack is null");
            } catch (Exception e) {
                plugin.getLogger().warning("Missing or invalid item in " + fileName + ": " + e.getMessage());
                continue;
            }

            // Parse slot (supporting range syntax like 10-12, list of ranges/numbers)
            List<Range> ranges = Range.parseFromYaml(config, rootPath + ".slot");
            for (Range range : ranges) {
                for (int i = range.getStart(); i <= range.getEnd(); i++) {
                    template.addItem(new ShopTemplateItemStack(itemStack.clone(), type, i));
                }
            }
        }

        cache.put(fileName, template);
        return template;
    }



    private void loadAllTemplates() {
        File[] files = templateFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String fileName = file.getName().replace(".yml", "");
            if (!cache.containsKey(fileName)) {
                getTemplate(fileName); // lazy-load with updated parser
            }
        }

        plugin.getLogger().info("Loaded " + cache.size() + " shop templates.");
    }



}
