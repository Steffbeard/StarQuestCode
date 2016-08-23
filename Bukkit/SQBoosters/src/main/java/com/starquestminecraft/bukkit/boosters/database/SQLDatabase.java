package com.starquestminecraft.bukkit.boosters.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.starquestminecraft.bukkit.boosters.Booster;
import com.starquestminecraft.bukkit.boosters.SQBoosters;
import com.starquestminecraft.bukkit.database.SQDatabase;

public class SQLDatabase {

    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `minecraft`.`boosters2` (`id` INT NOT NULL AUTO_INCREMENT, `booster` VARCHAR(32) NOT NULL, `multiplier` TINYINT NOT NULL, `purchaser` VARCHAR(32) NULL, `expirationdate` TIMESTAMP NOT NULL, PRIMARY KEY (`id`))";
    private static final String SQL_READ_OBJECT = "SELECT * FROM `minecraft`.`boosters2` WHERE `expirationdate`>NOW()";
    private static final String SQL_WRITE_OBJECT = "INSERT INTO `minecraft`.`boosters2`(`booster`,`multiplier`,`purchaser`,`expirationdate`) VALUES (?,?,?,?)";

    private final SQBoosters plugin;

    public SQLDatabase(final SQBoosters plugin) {

        this.plugin = plugin;

        try {
            createTable(SQDatabase.getConnection());
        }
        catch(SQLException ex) {
            throw new RuntimeException(ex);
        }

    }

    public void addMultiplier(final String booster, final int multiplier, final String purchaser, final int minutes) {

        try(Connection con = SQDatabase.getConnection()) {

            try(PreparedStatement ps = con.prepareStatement(SQL_WRITE_OBJECT)) {

                ps.setString(1, booster);
                ps.setInt(2, multiplier);
                ps.setString(3, purchaser);
                ps.setTimestamp(4, new Timestamp(System.currentTimeMillis() + (minutes * 60000)));

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

    public Map<Booster.Type, Booster> getBoosters() {

        Map<Booster.Type, Booster> boosters = new HashMap<>();

        try(Connection con = SQDatabase.getConnection()) {

            try(Statement s = con.createStatement()) {

                try(ResultSet rs = s.executeQuery(SQL_READ_OBJECT)) {

                    while(rs.next()) {

                        int id = rs.getInt("id");
                        String name = rs.getString("booster");
                        int multiplier = rs.getInt("multiplier");
                        String purchaser = rs.getString("purchaser");
                        long expire_time = rs.getTimestamp("expirationdate").getTime();
                        boolean enabled = plugin.getConfig().getBoolean(name);
                        Booster.Type type = Booster.Type.byName(name);

                        if(type == null) {
                            plugin.getLogger().warning("Unknown booster '" + name + "'! (id=" + id + ")");
                            continue;
                        }

                        boosters.put(type, new Booster(id, type, multiplier, purchaser, expire_time, enabled));

                    }

                }

            }

        }
        catch(SQLException ex) {
            ex.printStackTrace();
        }

        return boosters;

    }

}
