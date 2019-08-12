package com.starquestminecraft.bungeecord.greeter;

import com.starquestminecraft.bungeecord.SQBungeePlugin;
import com.starquestminecraft.bungeecord.greeter.command.MaintenanceCommand;
import com.starquestminecraft.bungeecord.greeter.command.ReloadCommand;
import com.starquestminecraft.bungeecord.greeter.listener.PingListener;
import com.starquestminecraft.bungeecord.greeter.listener.PlayerListener;

public class SQGreeter extends SQBungeePlugin {

    private final GreeterDatabase database;
    private final CryoBounce cryobounce;
    private final MaintenanceMode maintenance;

    public SQGreeter() {

        this.cryobounce = new CryoBounce(this);
        this.database = new GreeterDatabase(this);
        this.maintenance = new MaintenanceMode();

    }

    @Override
    public void enable() {

        database.initialize();

        getProxy().getPluginManager().registerCommand(this, new MaintenanceCommand(this));
        getProxy().getPluginManager().registerCommand(this, new ReloadCommand(this));

        getProxy().getPluginManager().registerListener(this, new PingListener(this));
        getProxy().getPluginManager().registerListener(this, new PlayerListener(this));

        getProxy().registerChannel("cryoBounce");

    }

    public CryoBounce getCryoBounce() {
        return cryobounce;
    }

    public GreeterDatabase getDatabase() {
        return database;
    }

    public MaintenanceMode getMaintenanceMode() {
        return maintenance;
    }

}
