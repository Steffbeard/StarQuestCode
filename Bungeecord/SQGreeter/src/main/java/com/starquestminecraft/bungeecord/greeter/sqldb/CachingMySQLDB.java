package com.starquestminecraft.bungeecord.greeter.sqldb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.ProxyServer;

import com.starquestminecraft.bungeecord.StarQuest;
import com.starquestminecraft.bungeecord.greeter.SQGreeter;

public class CachingMySQLDB {

    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `greeter_data` (`ip` VARCHAR(36),`username` VARCHAR(32),PRIMARY KEY(`ip`))";
    private static final String SQL_INSUPD_DATA = "INSERT INTO `greeter_data` (`ip`,`username`) VALUES (?,?) ON DUPLICATE KEY UPDATE `username`=VALUES(`username`)";

    private Map<String, String> allPlayerData = new HashMap<>();

    public CachingMySQLDB() {

        try(Connection con = StarQuest.getDatabaseConnection()) {

            try(Statement s = con.createStatement()) {

                s.executeUpdate(SQL_CREATE_TABLE);

                System.out.println("[SQGreeter] Table check/creation sucessful");

            }

        }
        catch(SQLException ex) {
            System.out.println("[SQGreeter] Table Creation Error");
        }

    }

    public void initialize() {

        System.out.println("[Greeter] Database initializing.");

        allPlayerData = loadAll();

        System.out.println("[Greeter] Done: " + allPlayerData.size() + " known players found!");

    }

    public HashMap<String, String> loadAll() {

        HashMap<String, String> retval = new HashMap<>();

        try(Connection con = StarQuest.getDatabaseConnection()) {

            try(Statement s = con.createStatement()) {

                try(ResultSet rs = s.executeQuery("SELECT * from `greeter_data`")) {

                    while(rs.next()) {

                        String ip = rs.getString("ip");
                        String username = rs.getString("username");

                        retval.put(ip, username);

                    }

                }

            }

        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }

        return retval;

    }

    public String getUsername(final String ip) {
        return allPlayerData.get(ip);
    }

    public void updateIP(final String ip, final String username) {

        allPlayerData.put(ip, username);

        ProxyServer.getInstance().getScheduler().runAsync(SQGreeter.getInstance(), new Runnable() {

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
