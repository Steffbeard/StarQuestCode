package com.starquestminecraft.bungeecord.orbits;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class SQOrbits extends Plugin {

    private static final double RADIANS_PER_DEGREE = Math.PI / 180;     //useful math constant
    private static final String CHAT_PREFIX = "[SQOrbitsBungee]";

    private File configFile;
    private Configuration config;
    private Map<String, PlanetData> planetData;
    private Set<String> planetsToDelete;
    private String hostName;
    private String dbName;
    private Integer port;
    private String userName;
    private String password;
    private String tableName;
    private String dbURL;
    private Connection connection;

    @Override
    public void onEnable() {

        planetData = new HashMap<>();
        planetsToDelete = new HashSet<>();

        try {
            setupFromConfig();
        }
        catch(BadConfigException ex) {
            print("Errors setting up from config mean the plugin will stop running.");
            return;
        }

        try {

            connectToDB();

            Driver driver = (Driver)Class.forName("com.mysql.jdbc.Driver").newInstance();
            DriverManager.registerDriver(driver);

            String query = "CREATE TABLE IF NOT EXISTS `" + tableName + "` ("
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
                + "PRIMARY KEY(`name`))";

            try(PreparedStatement ps = connection.prepareStatement(query)) {
                ps.execute();
            }

            print("SQL table creation/checking was successful.");

            try(Statement s = connection.createStatement()) {

                try(ResultSet rs = s.executeQuery("SELECT `name`, `rotation` FROM `" + tableName + "`")) {

                    planetsToDelete = new HashSet<>();

                    while(rs.next()) {   //iterates through all rows in set

                        String planetName = rs.getString("name");
                        PlanetData data = planetData.get(planetName);

                        //if planet not present in config (meaning it is old and should be deleted from the DB)
                        //marks planet for deletion in DB
                        if(data == null) {
                            planetsToDelete.add(planetName);
                        }
                        //else (not a new planet as it is in DB but not one to be deleted as still in config)
                        //changes the rotation value of the planet so it is moved
                        else {
                            data.rotation = rs.getDouble("rotation") + data.speed;
                        }

                    }

                }

            }

        }
        catch(ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException ex) {
            print("An error connecting to or reading from the database mean the plugin will stop running. Stack trace: ");
            ex.printStackTrace();
            return;
        }

        deleteOldPlanets();
        updateDB();

        try {
            connection.close();
        }
        catch(SQLException ex) {
            print("An error occurred whilst closing the SQL connection. Stack trace:");
            ex.printStackTrace();
        }

    }

    private void updateDB() {

        String query = "INSERT INTO `" + tableName + "` ("
            + "`name`,"
            + "`rotation`,"
            + "`system_name`,"
            + "`world_name`,"
            + "`x`,"
            + "`y`,"
            + "`z`,"
            + "`region_max_x`,"
            + "`region_max_y`,"
            + "`region_max_z`,"
            + "`region_min_x`,"
            + "`region_min_y`,"
            + "`region_min_z`,"
            + "`schematic_path`"
            + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
            + "ON DUPLICATE KEY UPDATE "
            + "`rotation`=VALUES(`rotation`),"
            + "`system_name`=VALUES(`system_name`),"
            + "`world_name`=VALUES(`world_name`),"
            + "`x`=VALUES(`x`),"
            + "`y`=VALUES(`y`),"
            + "`z`=VALUES(`z`),"
            + "`region_max_x`=VALUES(`region_max_x`),"
            + "`region_max_y`=VALUES(`region_max_y`),"
            + "`region_max_z`=VALUES(`region_max_z`),"
            + "`region_min_x`=VALUES(`region_min_x`),"
            + "`region_min_y`=VALUES(`region_min_y`),"
            + "`region_min_z`=VALUES(`region_min_z`),"
            + "`schematic_path`=VALUES(`schematic_path`)";

        for(String planet_name : planetData.keySet()) {

            PlanetData data = planetData.get(planet_name);
            int[] centreCoords = calculatePlanetCoords(planet_name, planetData.get(planet_name).rotation);
            int centreX = centreCoords[0];
            int centreY = centreCoords[1];
            int centreZ = centreCoords[2];
            int maxX = centreX + data.relativeRegionMaxCornerX;
            int maxY = centreY + data.relativeRegionMaxCornerY;
            int maxZ = centreZ + data.relativeRegionMaxCornerZ;
            int minX = centreX + data.relativeRegionMinCornerX;
            int minY = centreY + data.relativeRegionMinCornerY;
            int minZ = centreZ + data.relativeRegionMinCornerZ;

            try(PreparedStatement ps = connection.prepareStatement(query)) {

                ps.setString(1, planet_name);
                ps.setDouble(2, data.rotation);
                ps.setString(3, data.systemName);
                ps.setString(4, data.worldName);
                ps.setInt(5, centreX);
                ps.setInt(6, centreY);
                ps.setInt(7, centreZ);
                ps.setInt(8, maxX);
                ps.setInt(9, maxY);
                ps.setInt(10, maxZ);
                ps.setInt(11, minX);
                ps.setInt(12, minY);
                ps.setInt(13, minZ);
                ps.setString(14, data.schematicPath);

                ps.execute();

            }
            catch(SQLException ex) {
                print("An error merging data for planet '" + planet_name + "' into the database. "
                    + "As a result the planet will not be added. Stack trace: ");
                ex.printStackTrace();
            }

        }

    }

    //NB: this method does not delete the planet structure+region in the world,
    //it only cleans it from the database so it won't move anymore
    private void deleteOldPlanets() {

        String query = "DELETE FROM `" + tableName + "` WHERE `name`=?";

        for(String planetName : planetsToDelete) {

            try(PreparedStatement ps = connection.prepareStatement(query)) {
                
                ps.setString(1, planetName);
                
                ps.execute();

            }
            catch(SQLException ex) {
                print("An SQLException occured when attempting to delete the planet '" + planetName + "' from the database. "
                    + "As a result, the planet will not be deleted from the database.");
            }

        }

    }

    //fetches all data but rotation from respective PlanetData object stored in map
    private int[] calculatePlanetCoords(final String planetName, final double rotation) {

        PlanetData data = planetData.get(planetName);
        double sin = Math.sin(rotation * RADIANS_PER_DEGREE);
        double cos = Math.cos(rotation * RADIANS_PER_DEGREE);
        double rawCentreX = data.blocksFromSun * cos + data.sunX;
        double rawCentreZ = data.blocksFromSun * sin + data.sunZ;
        int x = (int)Math.round(rawCentreX);
        int z = (int)Math.round(rawCentreZ);
        int y = data.orbitY;

        return new int[] {x, y, z};

    }

    private void print(final String msg) {
        getProxy().getLogger().info(CHAT_PREFIX + " " + msg);
    }

    private void setupFromConfig() throws BadConfigException {

        saveDefaultConfig();

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        }
        catch(IOException ex) {
            print("An error occurred loading the config file.");
            ex.printStackTrace();
            return;
        }

        hostName = getFromConfig("Host_Name");
        dbName = getFromConfig("Database_Name");
        port = getFromConfig("Port");
        userName = getFromConfig("Username");
        password = getFromConfig("Password");
        tableName = getFromConfig("TableName");

        LinkedHashMap<String, Object> planetSection = getFromConfig("Planets");
        Collection<String> planetNames = planetSection.keySet();

        for(String planet_name : planetNames) {

            PlanetData planet = new PlanetData();

            planet.sunX = getFromConfig("Planets." + planet_name + ".SunCoords.x");
            planet.sunZ = getFromConfig("Planets." + planet_name + ".SunCoords.z");

            planet.systemName = getFromConfig("Planets." + planet_name + ".SystemName");
            planet.worldName = getFromConfig("Planets." + planet_name + ".WorldName");
            planet.orbitY = getFromConfig("Planets." + planet_name + ".OrbitCenterY");
            planet.speed = getFromConfig("Planets." + planet_name + ".Speed");
            planet.blocksFromSun = getFromConfig("Planets." + planet_name + ".BlocksFromSun");

            planet.relativeRegionMinCornerX = getFromConfig("Planets." + planet_name + ".RelativeRegionMinCorner.x");
            planet.relativeRegionMinCornerY = getFromConfig("Planets." + planet_name + ".RelativeRegionMinCorner.y");
            planet.relativeRegionMinCornerZ = getFromConfig("Planets." + planet_name + ".RelativeRegionMinCorner.z");

            planet.relativeRegionMaxCornerX = getFromConfig("Planets." + planet_name + ".RelativeRegionMaxCorner.x");
            planet.relativeRegionMaxCornerY = getFromConfig("Planets." + planet_name + ".RelativeRegionMaxCorner.y");
            planet.relativeRegionMaxCornerZ = getFromConfig("Planets." + planet_name + ".RelativeRegionMaxCorner.z");

            planet.schematicPath = getFromConfig("Planets." + planet_name + ".SchematicPath");

            planet.rotation = getFromConfig("Planets." + planet_name + ".InitialRotation");

            planetData.put(planet_name, planet);

        }

    }

    @SuppressWarnings("unchecked")
    private <T extends Object> T getFromConfig(String configName) throws BadConfigException {

        Object fromConfig = config.get(configName);

        if(fromConfig == null) {
            print("Error retrieving value from config field '" + configName + "': is null.");
            throw new BadConfigException();
        }

        T tObject;
        try {
            tObject = (T)fromConfig;
        }
        catch(ClassCastException ex) {
            print("Error retrieving value from config field '" + configName + "': invalid datatype.");
            throw new BadConfigException();
        }

        return tObject;

    }

    private void connectToDB() throws SQLException {

        dbURL = "jdbc:mysql://" + hostName + ":" + port + "/" + dbName;
        if((userName.isEmpty()) && (password.isEmpty())) {
            connection = DriverManager.getConnection(dbURL);
        }
        else {
            connection = DriverManager.getConnection(dbURL, userName, password);
        }

    }

    private void saveDefaultConfig() {

        File dataFolder = getDataFolder();

        if(!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        configFile = new File(dataFolder, "config.yml");

        if(!configFile.exists()) {

            try {

                configFile.createNewFile();

                ByteStreams.copy(getResourceAsStream("config.yml"), new FileOutputStream(configFile));

            }
            catch(IOException ex) {
                print("An error occurred creating the default config file.");
                ex.printStackTrace();
            }

        }

    }

}
