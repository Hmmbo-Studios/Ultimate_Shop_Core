package com.hmmbo.ultimate_Shop_Core;

import com.hmmbo.ultimate_Shop_Core.utils.commands.ShopCommand;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

public final class Ultimate_Shop_Core extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Lamp<BukkitCommandActor> lamp = BukkitLamp.builder(this).build();
        lamp.register(new ShopCommand());


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
