
package com.starquestminecraft.bukkit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.plugin.java.JavaPlugin;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class StarQuest extends JavaPlugin {

    private static StarQuest instance;

    private HikariDataSource source;

    public StarQuest() {
        instance = this;
    }

    @Override
    public void onLoad() {

        saveDefaultConfig();

        String hostname = getConfig().getString("hostname","localhost");
        String username = getConfig().getString("username","minecraft");
        String password = getConfig().getString("password", "");
        String database = getConfig().getString("database", getServer().getServerName());

        if(source != null) {
            source.close();
        }

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + hostname);
        config.setUsername(username);
        config.setPassword(password);
        config.setCatalog(database);

        config.setConnectionTimeout(10000);
        config.setIdleTimeout(0);
        config.setLeakDetectionThreshold(120000);
        config.setMaxLifetime(0);
        config.setMaximumPoolSize(16);
        config.setMinimumIdle(1);

        config.addDataSourceProperty("autoReconnect", "false");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "384");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("zeroDateTimeBehavior", "convertToNull");

        source = new HikariDataSource(config);

        if(source.getIdleTimeout() == 0) {
            try(Connection con = source.getConnection()) {
                try(Statement s = con.createStatement()) {
                    try(ResultSet rs = s.executeQuery("SHOW VARIABLES LIKE 'wait_timeout'")) {
                        if(rs.next()) {
                            long timeout = Math.max(0, (rs.getInt(2) - 60) * 1000);
                            config.setIdleTimeout(timeout);
                            source.setIdleTimeout(timeout);
                        }
                    }
                }
            }
            catch(Exception ex) {

            }
        }

    }

    public static Connection getConnection() throws SQLException {
        return instance.source.getConnection();
    }

}
