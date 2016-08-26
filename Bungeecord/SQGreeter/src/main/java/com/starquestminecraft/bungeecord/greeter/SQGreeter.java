package com.starquestminecraft.bungeecord.greeter;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import com.starquestminecraft.bungeecord.greeter.command.MaintenanceCommand;
import com.starquestminecraft.bungeecord.greeter.command.ReloadCommand;
import com.starquestminecraft.bungeecord.greeter.sqldb.CachingMySQLDB;

public class SQGreeter extends Plugin implements Listener {

    private static SQGreeter instance;

    private CachingMySQLDB database;

    @Override
    public void onEnable() {

        instance = this;

        loadSettings();

        database = new CachingMySQLDB();

        database.initialize();

        getProxy().getPluginManager().registerListener(this, this);

        getProxy().getPluginManager().registerCommand(this, new ReloadCommand());
        getProxy().getPluginManager().registerCommand(this, new MaintenanceCommand());

        getProxy().registerChannel("cryoBounce");

    }

    public void loadSettings() {

        try {

            System.out.println("saving default config.");
            saveDefaultConfig();

            System.out.println("loading config");
            Configuration config = getConfig();

        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

    }

    private void saveDefaultConfig() throws Exception {

        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), "config.yml");

        System.out.println("file exists check.");
        if(!file.exists()) {

            System.out.println("attempting to save config");
            Files.copy(getResourceAsStream("config.yml"), file.toPath(), new CopyOption[0]);

            System.out.println("saved config.");

        }

    }

    private Configuration getConfig() throws Exception {
        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
    }

    @EventHandler
    public void onServerListPing(final ProxyPingEvent event) {

        ServerPing ping = event.getResponse();
        String username = database.getUsername(event.getConnection().getAddress().getAddress().getHostAddress());
        String line2;

        if(username != null) {
            line2 = ChatColor.WHITE + "Welcome back, " + ChatColor.GOLD + username + ChatColor.WHITE + "!";
        }
        else {
            line2 = ChatColor.WHITE + "Welcome to StarQuest!";
        }

        ping.setDescription(ChatColor.DARK_BLUE + "=====" + ChatColor.GOLD + "Star" + ChatColor.BLUE + "Quest " + ChatColor.RED + "4.0" + ChatColor.DARK_BLUE + "===== \n" + line2);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(final LoginEvent event) {

        if(!event.isCancelled()) {

            UUID u = event.getConnection().getUniqueId();

            if(MaintenanceMode.isEnabled()) {

                if(!MaintenanceMode.isAllowed(u)) {

                    event.setCancelReason("StarQuest is in maintenance mode: " + MaintenanceMode.message);
                    event.setCancelled(true);

                }

            }

        }

    }

    @EventHandler
    public void onPostLogin(final PostLoginEvent event) {

        CryoBounce.callCryoMessage(event.getPlayer(), 0);

        InetSocketAddress inet = event.getPlayer().getAddress();
        String ip = inet.getAddress().getHostAddress();
        String username = event.getPlayer().getName();

        database.updateIP(ip, username);

    }

    public static SQGreeter getInstance() {
        return instance;
    }

}
