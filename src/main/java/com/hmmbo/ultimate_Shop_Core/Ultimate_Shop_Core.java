package com.hmmbo.ultimate_Shop_Core;

import com.hmmbo.ultimate_Shop_Core.shop.listeners.ShopInventoryClickListener;
import com.hmmbo.ultimate_Shop_Core.shop.managers.ShopTemplateManager;
import com.hmmbo.ultimate_Shop_Core.utils.commands.ShopCommand;
import com.hmmbo.ultimate_Shop_Core.utils.sign.SignInput;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

/**
 * Main plugin entry.
 */
public final class Ultimate_Shop_Core extends JavaPlugin {

    public static Plugin instance;
    public static Economy economy;

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        saveDefaultConfig();
        saveResource("templates/default/shop.yml", false);
        saveResource("templates/default/buy_sell.yml", false);
        saveResource("templates/default/categories/ores.yml", false);
        saveResource("templates/default/categories/blocks.yml", false);
        Lamp<BukkitCommandActor> lamp = BukkitLamp.builder(this).build();
        lamp.register(new ShopCommand());

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            economy = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        }


        //Managers
        new ShopTemplateManager(this);
        SignInput.init();

        // Listeners
        Bukkit.getPluginManager().registerEvents(new ShopInventoryClickListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
