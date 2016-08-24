package com.starquestminecraft.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class SQBase extends JavaPlugin {

    @Override
    public void onLoad() {

        saveDefaultConfig();

        StarQuest.initialize(this);

    }

    @Override
    public void onEnable() {

        StarQuest.setupEconomy(this);

    }

}
