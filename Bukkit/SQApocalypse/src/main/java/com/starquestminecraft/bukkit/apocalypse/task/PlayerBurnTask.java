package com.starquestminecraft.bukkit.apocalypse.task;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerBurnTask extends ApocalypseTask {

    public PlayerBurnTask(final World world, final int stage) {
        super(world, stage);
    }

    @Override
    public void run() {

        long time = (world.getTime() % 24000);

        if(time > 12000) {
            return;
        }

        for(Player player : world.getPlayers()) {

            if(player.getGameMode() == GameMode.CREATIVE) {
                continue;
            }

            if(stage < 4) {

                ItemStack helmet = player.getInventory().getHelmet();

                if((helmet != null) && (helmet.getType() == Material.PUMPKIN)) {
                    continue;
                }

            }

            Location loc = player.getLocation();
            int sky_light = loc.getBlock().getLightFromSky();

            if(sky_light < 13) {
                continue;
            }
            
            player.setFireTicks(40);

        }

    }

}
