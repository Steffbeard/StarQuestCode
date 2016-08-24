package com.starquestminecraft.bukkit;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.bukkit.plugin.java.JavaPlugin;

import com.starquestminecraft.bukkit.database.HikariDatabase;

public class StarQuest extends JavaPlugin {

    private static StarQuest instance;

    private DataSource database;

    public StarQuest() {
        instance = this;
    }

    @Override
    public void onLoad() {

        saveDefaultConfig();

        String hostname = getConfig().getString("database.hostname","localhost");
        String username = getConfig().getString("database.username","minecraft");
        String password = getConfig().getString("database.password", "");
        String dbname = getConfig().getString("database.database", getServer().getServerName());

        if((database != null) || (database instanceof AutoCloseable)) {
            try {
                ((AutoCloseable)database).close();
            }
            catch(Exception ex) {

            }
        }

        database = HikariDatabase.create(hostname, username, password, dbname);

    }

    public static Connection getDatabaseConnection() throws SQLException {
        return instance.database.getConnection();
    }

}
