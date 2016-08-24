package com.starquestminecraft.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class SQBase extends JavaPlugin {

    private static SQBase instance;

    public static SQBase getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {

        instance = this;

        saveDefaultConfig();

        StarQuest.initialize(this);

    }

    @Override
    public void onEnable() {

        StarQuest.setupEconomy(this);

    }

}
