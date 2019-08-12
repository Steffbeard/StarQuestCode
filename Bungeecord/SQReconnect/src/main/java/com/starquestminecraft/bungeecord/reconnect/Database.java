package com.starquestminecraft.bungeecord.reconnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import com.starquestminecraft.bungeecord.StarQuest;

class Database {

    private static final String SQL_INSUPD_BOTH = "INSERT INTO `reconnect_data` (`uuid`,`lastServer`,`lastSQServer`) VALUES (?,?,?) ON DUPLICATE KEY UPDATE `lastServer`=VALUES(`lastServer`),`lastSQServer`=VALUES(`lastSQServer`)";
    private static final String SQL_INSUPD_OTHER_ONLY = "INSERT INTO `reconnect_data` (`uuid`,`lastServer`) VALUES (?,?) ON DUPLICATE KEY UPDATE `lastServer`=VALUES(`lastServer`)";
    private static final String SQL_SELECT_LAST = "SELECT `lastServer` FROM `reconnect_data` WHERE `uuid`=?";
    private static final String SQL_SELECT_MAIN = "SELECT `lastSQServer` FROM `reconnect_data` WHERE `uuid`=?";

    private final SQReconnect plugin;

    Database(final SQReconnect plugin) {
        this.plugin = plugin;
    }

    public void setUp() throws Exception {

        try(Connection con = StarQuest.getDatabaseConnection()) {

            try(Statement s = con.createStatement()) {

                s.executeUpdate("CREATE TABLE IF NOT EXISTS `reconnect_data` ("
                    + "`uuid` VARCHAR(36),"
                    + "`lastServer` VARCHAR(32),"
                    + "`lastSQServer` VARCHAR(32),"
                    + "PRIMARY KEY(`uuid`))");

                plugin.logInfo("Table check/creation sucessful.");

            }

        }
        catch(SQLException ex) {
            throw new SQLException("Exception creating database table:", ex);
        }

    }

    public void updateServer(final ProxiedPlayer player, final String server, final boolean is_main) {

        final String uuid = player.getUniqueId().toString();

        plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {

            @Override
            public void run() {

                try(Connection con = StarQuest.getDatabaseConnection()) {

                    try(PreparedStatement ps = con.prepareStatement(is_main ? SQL_INSUPD_BOTH : SQL_INSUPD_OTHER_ONLY)) {

                        ps.setString(1, uuid);
                        ps.setString(2, server);

                        if(is_main) {
                            ps.setString(3, server);
                        }

                        ps.executeUpdate();

                    }

                }
                catch(SQLException ex) {
                    plugin.logSevere("Exception storing player's last server:", ex);
                }

            }

        });

    }

    public String getServer(final ProxiedPlayer player, final boolean main_only) {

        final String uuid = player.getUniqueId().toString();

        try(Connection con = StarQuest.getDatabaseConnection()) {

            try(PreparedStatement ps = con.prepareStatement(main_only ? SQL_SELECT_MAIN : SQL_SELECT_LAST)) {

                ps.setString(1, uuid);

                try(ResultSet rs = ps.executeQuery()) {

                    if(rs.next()) {
                        return rs.getString(1);
                    }

                }

            }

        }
        catch(SQLException ex) {
            plugin.logSevere("Exception fetching player's last server:", ex);
        }

        return null;

    }

}
