package com.starquestminecraft.bukkit.beamtransporter.task;

import org.bukkit.scheduler.BukkitRunnable;

import com.starquestminecraft.bukkit.beamtransporter.object.ParticleBeam;

public class ParticleBeamTask extends BukkitRunnable {
	
	private final ParticleBeam beam;
	
	public ParticleBeamTask(final ParticleBeam beam) {
		
		this.beam = beam;
		
	}
	
    @Override
	public void run() {
		
		beam.spawnHelix();
		
	}

}
