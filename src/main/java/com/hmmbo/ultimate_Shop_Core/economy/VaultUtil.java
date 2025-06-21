package com.hmmbo.ultimate_Shop_Core.economy;

import com.hmmbo.ultimate_Shop_Core.Ultimate_Shop_Core;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultUtil {
    private static Economy economy;

    public static void setup() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
        }
    }

    public static Economy getEconomy() {
        return economy;
    }
}
