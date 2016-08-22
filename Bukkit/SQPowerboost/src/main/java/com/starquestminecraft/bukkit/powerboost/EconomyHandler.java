package com.starquestminecraft.bukkit.powerboost;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

public class EconomyHandler {

    private static Economy economy;

    public static int getCost() {
        return 100;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public static void setupEconomy() {

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

        if(rsp != null) {
            economy = rsp.getProvider();
        }

    }

}
