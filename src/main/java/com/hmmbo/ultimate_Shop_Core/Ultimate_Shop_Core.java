package com.hmmbo.ultimate_Shop_Core;

import com.hmmbo.ultimate_Shop_Core.shop.listeners.ShopMenuListener;
import com.hmmbo.ultimate_Shop_Core.shop.managers.ShopTemplateManager;
import com.hmmbo.ultimate_Shop_Core.utils.commands.ShopCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

public final class Ultimate_Shop_Core extends JavaPlugin {

    public static Plugin instance;

    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        saveDefaultConfig();
        saveResource("templates/default/shop.yml", false);
        saveResource("templates/default/food.yml", false);
        Lamp<BukkitCommandActor> lamp = BukkitLamp.builder(this).build();
        lamp.register(new ShopCommand());


        //Managers
        new ShopTemplateManager(this);

        // Listeners
        Bukkit.getPluginManager().registerEvents(new ShopMenuListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
