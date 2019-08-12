package com.starquestminecraft.bungeecord.orbits;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.md_5.bungee.config.Configuration;

import com.starquestminecraft.bungeecord.SQBungeePlugin;
import com.starquestminecraft.bungeecord.StarQuest;

public class SQOrbits extends SQBungeePlugin {

    private static final String TABLE_NAME = "planet_orbits";
    private static final String SQL_DELETE_ORBIT = "DELETE FROM `" + TABLE_NAME + "` WHERE `name`=?";
    private static final String SQL_INSUPD_ORBITS = "INSERT INTO `" + TABLE_NAME + "` (`name`,`rotation`,`system_name`,`world_name`,`x`,`y`,`z`,`region_max_x`,`region_max_y`,`region_max_z`,`region_min_x`,`region_min_y`,`region_min_z`,`schematic_path`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE `rotation`=VALUES(`rotation`),`system_name`=VALUES(`system_name`),`world_name`=VALUES(`world_name`),`x`=VALUES(`x`),`y`=VALUES(`y`),`z`=VALUES(`z`),`region_max_x`=VALUES(`region_max_x`),`region_max_y`=VALUES(`region_max_y`),`region_max_z`=VALUES(`region_max_z`),`region_min_x`=VALUES(`region_min_x`),`region_min_y`=VALUES(`region_min_y`),`region_min_z`=VALUES(`region_min_z`),`schematic_path`=VALUES(`schematic_path`)";
    private static final String SQL_SELECT_ROTATIONS = "SELECT `name`,`rotation` FROM `" + TABLE_NAME + "`";

    private final Map<String, PlanetData> planets = new HashMap<>();

    @Override
    public void load() {

        try(Connection con = StarQuest.getDatabaseConnection()) {

            try(Statement s = con.createStatement()) {

                s.executeUpdate("CREATE TABLE IF NOT EXISTS `" + TABLE_NAME + "` ("
                    + "`name` VARCHAR(32) NOT NULL,"
                    + "`rotation` DOUBLE DEFAULT 0,"
                    + "`system_name` VARCHAR(32) NOT NULL,"
                    + "`world_name` VARCHAR(32) NOT NULL,"
                    + "`x` INTEGER DEFAULT 0,"
                    + "`y` INTEGER DEFAULT 0,"
                    + "`z` INTEGER DEFAULT 0,"
                    + "`region_max_x` INTEGER DEFAULT 0,"
                    + "`region_max_y` INTEGER DEFAULT 0,"
                    + "`region_max_z` INTEGER DEFAULT 0,"
                    + "`region_min_x` INTEGER DEFAULT 0,"
                    + "`region_min_y` INTEGER DEFAULT 0,"
                    + "`region_min_z` INTEGER DEFAULT 0,"
                    + "`schematic_path` VARCHAR(256) NOT NULL,"
                    + "PRIMARY KEY(`name`))");

                logInfo("SQL table creation/checking was successful.");

            }

        }
        catch(SQLException ex) {
            logSevere("Exception creating database table:", ex);
        }

    }

    @Override
    public void enable() {

        planets.clear();

        loadFromConfig();

        try(Connection con = StarQuest.getDatabaseConnection()) {

            Set<String> to_delete = loadCurrentRotations(con);

            if(!to_delete.isEmpty()) {
                deleteOldPlanets(con, to_delete);
            }

            updatePlanets(con);

        }
        catch(SQLException ex) {
            logSevere("Exception accessing database:", ex);
        }

    }

    private void addPlanetData(final PlanetData data) {
        planets.put(data.getName().toLowerCase(), data);
    }

    public PlanetData getPlanetData(final String name) {
        return planets.get(name.toLowerCase());
    }

    private Set<String> loadCurrentRotations(final Connection con) {

        Set<String> to_delete = new HashSet<>();

        try(Statement s = con.createStatement()) {

            try(ResultSet rs = s.executeQuery(SQL_SELECT_ROTATIONS)) {

                while(rs.next()) {

                    String name = rs.getString("name");
                    PlanetData data = getPlanetData(name);

                    //if planet not present in config (meaning it is old and should be deleted from the DB)
                    //marks planet for deletion in DB
                    if(data == null) {
                        to_delete.add(name);
                    }
                    //else (not a new planet as it is in DB but not one to be deleted as still in config)
                    //changes the rotation value of the planet so it is moved
                    else {
                        data.setRotation(rs.getDouble("rotation") + data.getSpeed());
                    }

                }

            }

        }
        catch(SQLException ex) {
            logSevere("Exception loading current planet rotations:", ex);
        }

        return to_delete;

    }

    private void updatePlanets(final Connection con) {

        // I'm reasonably certain this can be batched
        try(PreparedStatement ps = con.prepareStatement(SQL_INSUPD_ORBITS)) {

            for(PlanetData data : planets.values()) {

                ps.setString(1, data.getName());
                ps.setDouble(2, data.getRotation());
                ps.setString(3, data.getSystemName());
                ps.setString(4, data.getWorldName());
                ps.setInt(5, data.getCenterX());
                ps.setInt(6, data.getCenterY());
                ps.setInt(7, data.getCenterZ());
                ps.setInt(8, data.getRegionMaxX());
                ps.setInt(9, data.getRegionMaxY());
                ps.setInt(10, data.getRegionMaxZ());
                ps.setInt(11, data.getRegionMinX());
                ps.setInt(12, data.getRegionMinY());
                ps.setInt(13, data.getRegionMinZ());
                ps.setString(14, data.getSchematicPath());

                ps.addBatch();

            }

            ps.executeBatch();

        }
        catch(SQLException ex) {
            logSevere("Exception updating planet orbits:", ex);
        }

    }

    //NB: this method does not delete the planet structure+region in the world,
    //it only cleans it from the database so it won't move anymore
    private void deleteOldPlanets(final Connection con, final Set<String> planets) {

        try(PreparedStatement ps = con.prepareStatement(SQL_DELETE_ORBIT)) {

            for(String name : planets) {

                ps.setString(1, name);

                ps.addBatch();

            }

            ps.executeBatch();

        }
        catch(SQLException ex) {
            logSevere("Exception deleting planets from database:", ex);
        }

    }

    private void loadFromConfig() {

        Configuration section_planets = getConfig().getSection("Planets");

        for(String planet_name : section_planets.getKeys()) {

            Configuration section = section_planets.getSection(planet_name);
            PlanetData data = new PlanetData(planet_name, section);

            addPlanetData(data);

        }

    }

}
