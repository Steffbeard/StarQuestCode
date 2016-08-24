package com.starquestminecraft.bukkit;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import com.starquestminecraft.bukkit.database.HikariDatabase;

public class StarQuest {

    private static DataSource database;
    private static Chat vault_chat;
    private static Economy vault_economy;
    private static Permission vault_permission;

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

    static void setupVault(final SQBase plugin) {

        if(plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }

        vault_chat = setupVaultInterface(plugin, Chat.class);
        vault_economy = setupVaultInterface(plugin, Economy.class);
        vault_permission = setupVaultInterface(plugin, Permission.class);

    }

    private static <T> T setupVaultInterface(final SQBase plugin, final Class<T> clazz) {

        RegisteredServiceProvider<T> provider = plugin.getServer().getServicesManager().getRegistration(clazz);

        if(provider == null) {
            return null;
        }

        return provider.getProvider();

    }

    public static Connection getDatabaseConnection() throws SQLException {
        return database.getConnection();
    }

    public static Chat getVaultChat() {
        return vault_chat;
    }

    public static Economy getVaultEconomy() {
        return vault_economy;
    }

    public static Permission getVaultPermission() {
        return vault_permission;
    }

}
