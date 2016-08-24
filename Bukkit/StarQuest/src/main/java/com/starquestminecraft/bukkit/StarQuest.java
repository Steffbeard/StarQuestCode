package com.starquestminecraft.bukkit;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;

import com.starquestminecraft.bukkit.database.HikariDatabase;

public class StarQuest {

    private static DataSource database;
    private static Economy economy;

    private StarQuest() {

    }

    static void initialize(final SQBase plugin) {

        setupDatabase(plugin);

    }

    static void setupDatabase(final SQBase plugin) {

        String hostname = plugin.getConfig().getString("database.hostname", "localhost");
        String username = plugin.getConfig().getString("database.username", "minecraft");
        String password = plugin.getConfig().getString("database.password", "");
        String dbname = plugin.getConfig().getString("database.database", plugin.getServer().getServerName());

        if((database != null) || (database instanceof AutoCloseable)) {
            try {
                ((AutoCloseable)database).close();
            }
            catch(Exception ex) {

            }
        }

        database = HikariDatabase.create(hostname, username, password, dbname);

    }

    static void setupEconomy(final SQBase plugin) {

        if(plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }

        RegisteredServiceProvider<Economy> provider = plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if(provider == null) {
            return;
        }

        economy = provider.getProvider();

    }

    public static Connection getDatabaseConnection() throws SQLException {
        return database.getConnection();
    }

    public static Economy getEconomy() {
        return economy;
    }

}
