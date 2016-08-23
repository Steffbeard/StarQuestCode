package com.starquestminecraft.bukkit.beamtransporter.object;

import org.bukkit.World;
import org.bukkit.block.Block;

public class Floor {

    private String name = "";
    private int y;
    private int floor;
    private World world;
    private Block stainedGlass;

    public Floor(final Block block) {
        this.stainedGlass = block;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
        if(name == null) {
            this.name = "";
        }

    }

    public int getY() {
        return this.y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public int getFloor() {
        return this.floor;
    }

    public void setFloor(final int floor) {
        this.floor = floor;
    }

    public World getWorld() {
        return this.world;
    }

    public void setWorld(final World world) {
        this.world = world;
    }

    public Block getStainedGlass() {
        return this.stainedGlass;
    }

    public void setStainedGlass(final Block stainedGlass) {
        this.stainedGlass = stainedGlass;
    }
}
