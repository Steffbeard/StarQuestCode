package com.starquestminecraft.bukkit.powerboost;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.massivecraft.factions.entity.Faction;
import com.starquestminecraft.bukkit.database.SQDatabase;
import com.starquestminecraft.bukkit.powerboost.boost.FactionPowerBoost;
import com.starquestminecraft.bukkit.powerboost.boost.PersonalPowerBoost;

public class SQLDatabase {

    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `powerboost_data` (`id` VARCHAR(32), `boost` INT(11), `taxes` INT(11), PRIMARY KEY (`id`))";
    private static final String SQL_INSUPD_BOOST = "INSERT INTO `powerboost_data` (`id`,`boost`,`taxes`) VALUES (?,?,?) ON DUPLICATE KEY UPDATE `boost`=VALUES(`boost`)";
    private static final String SQL_INSUPD_TAXES = "INSERT INTO `powerboost_data` (`id`,`boost`,`taxes`) VALUES (?,?,?) ON DUPLICATE KEY UPDATE `taxes`=VALUES(`taxes`)";
    private static final String SQL_SELECT_BOOST = "SELECT `boost` FROM `powerboost_data` WHERE `id`=?";
    private static final String SQL_SELECT_TAXES = "SELECT `taxes` FROM `powerboost_data` WHERE `id`=?";

    public SQLDatabase() {

        try {
            createTable(SQDatabase.getConnection());
        }
        catch(SQLException ex) {
            throw new RuntimeException(ex);
        }

    }

    public FactionPowerBoost getBoostOfFaction(final Faction faction) {
        return new FactionPowerBoost(faction, getBoost(faction.getId()));
    }

    public PersonalPowerBoost getBoostOfPlayer(final Player player) {
        return new PersonalPowerBoost(player.getUniqueId(), getBoost(player.getUniqueId().toString()));
    }

    public void setBoostOfFaction(final FactionPowerBoost boost) {
        setBoost(boost.getFaction().getId(), boost.getAmount());
    }

    public void setBoostOfPlayer(final PersonalPowerBoost boost) {
        setBoost(boost.getProfileID().toString(), boost.getAmount());
    }

    public int getTaxesOfFaction(final Faction faction) {

        String stripped_id = stripDashes(faction.getId());

        try(Connection con = SQDatabase.getConnection()) {

            try(PreparedStatement ps = con.prepareStatement(SQL_SELECT_TAXES)) {

                ps.setString(1, stripped_id);

                try(ResultSet rs = ps.executeQuery()) {

                    if(rs.next()) {
                        return rs.getInt("taxes");
                    }

                }

            }

        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }

        return 0;

    }

    public void setTaxesOfFaction(final Faction faction, final int taxes) {

        String stripped_id = stripDashes(faction.getId());

        try(Connection con = SQDatabase.getConnection()) {

            try(PreparedStatement ps = con.prepareStatement(SQL_INSUPD_TAXES)) {

                ps.setString(1, stripped_id);
                ps.setInt(2, 0);
                ps.setInt(3, taxes);

                ps.executeUpdate();

            }

        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }

    }

    private void createTable(final Connection con) throws SQLException {

        try(Statement s = con.createStatement()) {
            s.executeUpdate(SQL_CREATE_TABLE);
        }

    }

    private int getBoost(final String id) {

        String stripped_id = stripDashes(id);

        try(Connection con = SQDatabase.getConnection()) {

            try(PreparedStatement ps = con.prepareStatement(SQL_SELECT_BOOST)) {

                ps.setString(1, stripped_id);

                try(ResultSet rs = ps.executeQuery()) {

                    rs.next();

                    return rs.getInt("boost");

                }

            }

        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }

        return 0;

    }

    private void setBoost(final String id, final int boost) {

        String stripped_id = stripDashes(id);

        try(Connection con = SQDatabase.getConnection()) {

            try(PreparedStatement ps = con.prepareStatement(SQL_INSUPD_BOOST)) {

                ps.setString(1, stripped_id);
                ps.setInt(2, boost);
                ps.setInt(3, 0);

                ps.executeUpdate();

            }

        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }

    }

    private String stripDashes(final UUID profile_id) {
        return stripDashes(profile_id.toString());
    }

    private String stripDashes(final String str) {
        return str.replaceAll("-", "");
    }

}
