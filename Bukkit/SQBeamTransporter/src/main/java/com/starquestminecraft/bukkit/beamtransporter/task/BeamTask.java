package com.starquestminecraft.bukkit.beamtransporter.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.starquestminecraft.bukkit.beamtransporter.SQBeamTransporter;
import com.starquestminecraft.bukkit.beamtransporter.object.BeamTransporter;

public class BeamTask extends BukkitRunnable {

    private final List<Entity> removeList = new ArrayList<>();

    @Override
    public void run() {

        Iterator<Entity> itr = SQBeamTransporter.beamEntities.iterator();

        while(itr.hasNext()) {

            Entity passenger = itr.next();
            Location holders;
            BeamTransporter beamTransporter = SQBeamTransporter.transporterMap.get(passenger);

            if(!SQBeamTransporter.timeoutMap.containsKey(beamTransporter)) {
                SQBeamTransporter.timeoutMap.put(beamTransporter, System.currentTimeMillis());
            }

            if(!SQBeamTransporter.currentlyBeaming.contains(beamTransporter)) {
                //Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Added beamtransporter to list " + beamTransporter);
                SQBeamTransporter.currentlyBeaming.add(beamTransporter);
            }

            if(beamTransporter.destFloor.getFloor() > beamTransporter.startFloor.getFloor()) {
                passenger.setVelocity(new Vector(0.0D, beamTransporter.getSpeed(), 0.0D));
            }
            else {
                passenger.setVelocity(new Vector(0.0D, -beamTransporter.getSpeed(), 0.0D));
            }

            if(passenger.getLocation().getBlockX() != beamTransporter.startFloor.getStainedGlass().getX()) {
                passenger.sendMessage(ChatColor.RED + "Please do not exit the beam.");
                Location beamLoc = new Location(beamTransporter.startFloor.getWorld(), beamTransporter.startFloor.getStainedGlass().getLocation().getX() + 0.5, passenger.getLocation().getY(), beamTransporter.startFloor.getStainedGlass().getLocation().getZ() + 0.5, passenger.getLocation().getYaw(), passenger.getLocation().getPitch());
                passenger.teleport(beamLoc);
            }

            if(passenger.getLocation().getBlockZ() != beamTransporter.startFloor.getStainedGlass().getZ()) {
                passenger.sendMessage(ChatColor.RED + "Please do not exit the beam.");
                Location beamLoc = new Location(beamTransporter.startFloor.getWorld(), beamTransporter.startFloor.getStainedGlass().getLocation().getX() + 0.5, passenger.getLocation().getY(), beamTransporter.startFloor.getStainedGlass().getLocation().getZ() + 0.5, passenger.getLocation().getYaw(), passenger.getLocation().getPitch());
                passenger.teleport(beamLoc);
            }

            passenger.setGravity(false);
            passenger.setFallDistance(0.0F);

            //Bukkit.getServer().broadcastMessage("Player: " + passenger.getLocation().getY() + " Goal: " + beamTransporter.destFloor.getY() + 1.1D);
            if(beamTransporter.goingUp && passenger.getLocation().getY() > (double)beamTransporter.destFloor.getY() + 1.1D
                || !beamTransporter.goingUp
                && passenger.getLocation().getY() < (double)beamTransporter.destFloor.getY() - 0.9D
                || passenger.isDead()
                || (System.currentTimeMillis() - SQBeamTransporter.timeoutMap.get(beamTransporter)) >= 100000) {

                SQBeamTransporter.currentlyBeaming.remove(beamTransporter);
                //Bukkit.getServer().broadcastMessage(ChatColor.RED + "Removed beamtransporter from list " + beamTransporter);

                if(System.currentTimeMillis() - SQBeamTransporter.timeoutMap.get(beamTransporter) >= 100000) {
                    passenger.sendMessage(ChatColor.RED + "Error - The beam of the beam transporter has timed out.");
                }

                passenger.setVelocity(new Vector(0, 0, 0));
                holders = passenger.getLocation().clone();

                if(beamTransporter.goingUp) {
                    holders.setY((double)beamTransporter.destFloor.getY() + 1.9D);
                }
                else {
                    holders.setY((double)beamTransporter.destFloor.getY() - 0.9D);
                }

                passenger.teleport(holders);

                removeList.add(passenger);

                itr.remove();

                if(beamTransporter.beam != null) {
                    beamTransporter.beam.remove();
                }

                if(beamTransporter.pBeam != null) {
                    beamTransporter.pBeam.remove();
                }

                beamTransporter.setStainedGlass();
                beamTransporter.setStainedGlass();
                beamTransporter.setStainedGlass();

                passenger.setGravity(true);

            }

        }

    }

}
