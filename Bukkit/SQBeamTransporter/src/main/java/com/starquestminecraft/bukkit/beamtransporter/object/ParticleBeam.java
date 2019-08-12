package com.starquestminecraft.bukkit.beamtransporter.object;

import org.bukkit.Location;
import org.bukkit.Particle;

import com.starquestminecraft.bukkit.beamtransporter.task.ParticleBeamTask;

public class ParticleBeam {

    private final Location bottom;
    private final double height;
    private final ParticleBeamTask task;

    public ParticleBeam(Location bottom, double height) {

        this.bottom = bottom.add(0.5, 0, 0.5); //So it's the middle of the block
        this.height = height;
        this.task = new ParticleBeamTask(this);

    }

    public ParticleBeamTask getTask() {
        return task;
    }

    public void spawnHelix() {

        for(double y = 0; y <= this.height; y += 0.2) {

            double adjustedX = 1 * Math.cos(y);
            double adjustedZ = 1 * Math.sin(y);
            Location loc = this.bottom;

            loc.add(adjustedX, y, adjustedZ);

            loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 0, -10, 0, 1);

            loc.subtract(adjustedX, y, adjustedZ);

        }

    }

    public void remove() {

        task.cancel();

    }

}
