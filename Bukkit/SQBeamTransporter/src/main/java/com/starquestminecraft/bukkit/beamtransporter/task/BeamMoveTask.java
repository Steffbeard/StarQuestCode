package com.starquestminecraft.bukkit.beamtransporter.task;

import org.bukkit.scheduler.BukkitRunnable;

import com.starquestminecraft.bukkit.beamtransporter.object.Beam;

public class BeamMoveTask extends BukkitRunnable {

    private final Beam beam;

    public BeamMoveTask(final Beam beam) {
        this.beam = beam;
    }

    @Override
    public void run() {

        beam.taskMove();

    }

}
