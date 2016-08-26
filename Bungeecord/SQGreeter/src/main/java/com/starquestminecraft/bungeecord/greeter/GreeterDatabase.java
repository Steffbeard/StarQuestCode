package com.starquestminecraft.bungeecord.greeter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.starquestminecraft.bungeecord.StarQuest;
import com.starquestminecraft.bungeecord.greeter.SQGreeter;

public class GreeterDatabase {

    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `greeter_data` (`ip` VARCHAR(36),`username` VARCHAR(32),PRIMARY KEY(`ip`))";
    private static final String SQL_INSUPD_DATA = "INSERT INTO `greeter_data` (`ip`,`username`) VALUES (?,?) ON DUPLICATE KEY UPDATE `username`=VALUES(`username`)";
    private static final String SQL_SELECT_DATA = "SELECT * from `greeter_data`";

    private final SQGreeter plugin;
    private final Map<String, String> usernames_by_ip;

    public GreeterDatabase(final SQGreeter plugin) {

        this.plugin = plugin;

        this.usernames_by_ip = new HashMap<>();

    }

    public void initialize() {

        plugin.getLogger().info("Database initializing.");

        try(Connection con = StarQuest.getDatabaseConnection()) {

            try(Statement s = con.createStatement()) {

                s.executeUpdate(SQL_CREATE_TABLE);

                plugin.getLogger().info("Table check/creation sucessful");

            }

            loadData(con);

            plugin.getLogger().info("Done: " + usernames_by_ip.size() + " known players found!");

        }
        catch(SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Database initialization error", ex);
        }

    }

    private void loadData(final Connection con) throws SQLException {

        Map<String, String> map = new HashMap<>();

        try(Statement s = con.createStatement()) {

            try(ResultSet rs = s.executeQuery(SQL_SELECT_DATA)) {

                while(rs.next()) {
                    map.put(rs.getString("ip"), rs.getString("username"));
                }

            }

        }

        usernames_by_ip.clear();
        usernames_by_ip.putAll(map);

    }

    public String getUsername(final String ip) {
        return usernames_by_ip.get(ip);
    }

    public void updateIP(final String ip, final String username) {

        usernames_by_ip.put(ip, username);

        plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {

            @Override
            public void run() {

                try(Connection con = StarQuest.getDatabaseConnection()) {

                    try(PreparedStatement ps = con.prepareStatement(SQL_INSUPD_DATA)) {

                        ps.setString(1, ip);
                        ps.setString(2, username);

                        ps.execute();

                    }

                }
                catch(SQLException ex) {
                    ex.printStackTrace();
                }

            }

        });

    }

}
