package com.starquestminecraft.bukkit.asteroidbays;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.plugin.java.JavaPlugin;

public class SQAsteroidBays extends JavaPlugin {

    private final List<File> schematic_files = new ArrayList<>();
    private final Random rand = new Random();

    private File schematic_dir;
    private SchematicFilenameFilter schematic_filter;

    private String paste_world;
    private double paste_x;
    private double paste_y;
    private double paste_z;

    @Override
    public void onEnable() {

        paste_world = getConfig().getString("world", "world");
        paste_x = getConfig().getDouble("x", 0);
        paste_y = getConfig().getDouble("y", 0);
        paste_z = getConfig().getDouble("z", 0);

        schematic_dir = new File(getConfig().getString("schematic.dir","plugins/WorldEdit/schematics/"));
        schematic_filter = new SchematicFilenameFilter(getConfig().getString("schematic.prefix", "Asteroids"));

        schematic_files.addAll(Arrays.asList(schematic_dir.listFiles(schematic_filter)));

        getServer().getScheduler().runTaskTimer(this, new GenerateAsteroidTask(this), 200, 12000);

        getLogger().info("SQAsteroidBays has been enabled");

    }

    @Override
    public void onDisable() {

        schematic_files.clear();

        getLogger().info("SQAsteroidBays has been disabled");

    }

    public String getPasteWorld() {
        return paste_world;
    }

    public double getPasteX() {
        return paste_x;
    }

    public double getPasteY() {
        return paste_y;
    }

    public double getPasteZ() {
        return paste_z;
    }

    public File getNextSchematic() {
        return schematic_files.get(rand.nextInt(schematic_files.size()));
    }

}
