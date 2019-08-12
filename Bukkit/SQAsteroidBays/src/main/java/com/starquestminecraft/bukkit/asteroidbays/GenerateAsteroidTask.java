package com.starquestminecraft.bukkit.asteroidbays;

import java.io.File;
import java.io.IOException;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;

public class GenerateAsteroidTask implements Runnable {

    private final SQAsteroidBays plugin;

    public GenerateAsteroidTask(final SQAsteroidBays plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {

        File file = plugin.getNextSchematic();
        CuboidClipboard clipboard;

        try {
            clipboard = SchematicFormat.MCEDIT.load(file);
        }
        catch(DataException | IOException ex) {
            ex.printStackTrace();
            return;
        }

        BukkitWorld world = new BukkitWorld(plugin.getServer().getWorld(plugin.getPasteWorld()));
        EditSession session = new EditSession(world, 1000);
        Vector mp = new Vector(plugin.getPasteX(), plugin.getPasteY(), plugin.getPasteZ());

        try {
            clipboard.paste(session, mp, true);
        }
        catch(MaxChangedBlocksException ex) {
            ex.printStackTrace();
        }

    }

}
