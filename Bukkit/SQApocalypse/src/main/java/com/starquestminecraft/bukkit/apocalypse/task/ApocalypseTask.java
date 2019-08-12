package com.starquestminecraft.bukkit.apocalypse.task;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class ApocalypseTask extends BukkitRunnable {

    protected final World world;
    protected final int stage;

    public ApocalypseTask(final World world, final int stage) {

        this.world = world;
        this.stage = stage;

    }

}
