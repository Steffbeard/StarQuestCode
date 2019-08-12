package com.starquestminecraft.bungeecord.orbits;

import net.md_5.bungee.config.Configuration;

/**
 * A representation of the planet object stored in the config.
 */
public class PlanetData {

    private static final double RADIANS_PER_DEGREE = Math.PI / 180;

    private final String name;

    private final double sun_x;
    private final double sun_z;

    private final String system_name;
    private final String world_name;
    private final int orbit_y;
    private final double speed;
    private final double sun_distance;
    private final int region_min_x_relative;
    private final int region_min_y_relative;
    private final int region_min_z_relative;
    private final int region_max_x_relative;
    private final int region_max_y_relative;
    private final int region_max_z_relative;
    private final String schematic_path;

    private double rotation;
    private int center_x;
    private int center_z;

    public PlanetData(final String name, final Configuration section) {

        this.name = name;

        this.sun_x = section.getDouble("SunCoords.x");
        this.sun_z = section.getDouble("SunCoords.z");

        this.system_name = section.getString("SystemName");
        this.world_name = section.getString("WorldName");
        this.orbit_y = section.getInt("OrbitCenterY");
        this.speed = section.getDouble("Speed");
        this.sun_distance = section.getDouble("BlocksFromSun");

        this.region_min_x_relative = section.getInt("RelativeRegionMinCorner.x");
        this.region_min_y_relative = section.getInt("RelativeRegionMinCorner.y");
        this.region_min_z_relative = section.getInt("RelativeRegionMinCorner.z");

        this.region_max_x_relative = section.getInt("RelativeRegionMaxCorner.x");
        this.region_max_y_relative = section.getInt("RelativeRegionMaxCorner.y");
        this.region_max_z_relative = section.getInt("RelativeRegionMaxCorner.z");

        this.schematic_path = section.getString("SchematicPath");

        this.rotation = section.getDouble("InitialRotation");

        updateCenter();

    }

    public String getName() {
        return name;
    }

    public double getSunX() {
        return sun_x;
    }

    public double getSunZ() {
        return sun_z;
    }

    public String getSystemName() {
        return system_name;
    }

    public String getWorldName() {
        return world_name;
    }

    public int getOrbitY() {
        return orbit_y;
    }

    public double getSpeed() {
        return speed;
    }

    public double getBlocksFromSun() {
        return sun_distance;
    }

    public int getRelativeRegionMinX() {
        return region_min_x_relative;
    }

    public int getRelativeRegionMinY() {
        return region_min_y_relative;
    }

    public int getRelativeRegionMinZ() {
        return region_min_z_relative;
    }

    public int getRelativeRegionMaxX() {
        return region_max_x_relative;
    }

    public int getRelativeRegionMaxY() {
        return region_max_y_relative;
    }

    public int getRelativeRegionMaxZ() {
        return region_max_z_relative;
    }

    public String getSchematicPath() {
        return schematic_path;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(final double rotation) {

        this.rotation = rotation;

        updateCenter();

    }

    public int getCenterX() {
        return center_x;
    }

    public int getCenterY() {
        return orbit_y;
    }

    public int getCenterZ() {
        return center_z;
    }

    public int getRegionMinX() {
        return getCenterX() + getRelativeRegionMinX();
    }

    public int getRegionMinY() {
        return getCenterY() + getRelativeRegionMinY();
    }

    public int getRegionMinZ() {
        return getCenterZ() + getRelativeRegionMinZ();
    }

    public int getRegionMaxX() {
        return getCenterX() + getRelativeRegionMaxX();
    }

    public int getRegionMaxY() {
        return getCenterY() + getRelativeRegionMaxY();
    }

    public int getRegionMaxZ() {
        return getCenterZ() + getRelativeRegionMaxZ();
    }

    private void updateCenter() {

        double sin = Math.sin(rotation * RADIANS_PER_DEGREE);
        double cos = Math.cos(rotation * RADIANS_PER_DEGREE);

        center_x = (int)Math.round(sun_distance * cos + sun_x);
        center_z = (int)Math.round(sun_distance * sin + sun_z);

    }

}
