package com.starquestminecraft.bukkit.apocalypse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.starquestminecraft.bukkit.StarQuest;

public class SQLDatabase {

    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `apoc_data` (`uuid` VARCHAR(36),`score` INT(11), PRIMARY KEY(`uuid`))";
    private static final String SQL_INSADD_SCORE = "INSERT INTO `apoc_data` (`uuid`, `score`) VALUES (?,?) ON DUPLICATE KEY UPDATE `score`=`score`+VALUES(`score`)";
    private static final String SQL_INSUPD_SCORE = "INSERT INTO `apoc_data` (`uuid`, `score`) VALUES (?,?) ON DUPLICATE KEY UPDATE `score`=VALUES(`score`)";
    private static final String SQL_SELECT_SCORE = "SELECT `score` FROM `apoc_data` WHERE `uuid`=?";
    private static final String SQL_SELECT_TOP = "SELECT * FROM `apoc_data` ORDER BY `score` DESC LIMIT 10";

    private final SQApocalypse plugin;

    public SQLDatabase(final SQApocalypse plugin) {

        this.plugin = plugin;
        
        createTable();

    }

    public int getScore(final UUID profile_id) {

        int score = 0;

        try(Connection con = StarQuest.getDatabaseConnection()) {

            try(PreparedStatement ps = con.prepareStatement(SQL_SELECT_SCORE)) {

                ps.setString(1, profile_id.toString());

                try(ResultSet rs = ps.executeQuery()) {

                    if(rs.next()) {
                        score = rs.getInt("score");
                    }

                }

            }

        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }

        return score;

    }

    public void displayTop(final Player requester) {

        try(Connection con = StarQuest.getDatabaseConnection()) {
        
            try(PreparedStatement pstmt = con.prepareStatement(SQL_SELECT_TOP)) {

                try(ResultSet rs = pstmt.executeQuery()) {

                    int num = 1;

                    while(rs.next()) {

                        int score = rs.getInt("score");
                        UUID profile_id = UUID.fromString(rs.getString("uuid"));
                        OfflinePlayer oplayer = Bukkit.getOfflinePlayer(profile_id);

                        if(oplayer != null) {
                            requester.sendMessage(num + "): " + oplayer.getName() + ", " + score + " points");
                            num++;
                        }

                    }

                }

            }

        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }

    }

    public void addScore(final UUID profile_id, final int addition) {

        try(Connection con = StarQuest.getDatabaseConnection()) {

            try(PreparedStatement ps = con.prepareStatement(SQL_INSADD_SCORE)) {

                ps.setString(1, profile_id.toString());
                ps.setInt(2, addition);

                ps.executeUpdate();

            }

        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }

    }

    public void updateScore(final UUID profile_id, final int score) {

        try(Connection con = StarQuest.getDatabaseConnection()) {

            try(PreparedStatement ps = con.prepareStatement(SQL_INSUPD_SCORE)) {

                ps.setString(1, profile_id.toString());
                ps.setInt(2, score);

                ps.executeUpdate();

            }

        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }

    }

    private void createTable() {

        try(Connection con = StarQuest.getDatabaseConnection()) {
        
            try(Statement s = con.createStatement()) {

                s.executeUpdate(SQL_CREATE_TABLE);

                plugin.getLogger().info("Table check/creation sucessful");

            }

        }
        catch(SQLException ex) {
            throw new RuntimeException(ex);
        }

    }

}
