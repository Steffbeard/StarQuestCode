package com.starquestminecraft.bukkit.space;

import java.util.Arrays;
import java.util.List;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeResetTask extends BukkitRunnable {

    private static final List<String> WORLDS = Arrays.asList("AsteroidBelt", "Defalos", "Digitalia", "Regalis");

    private final SQSpace plugin;

    public TimeResetTask(final SQSpace plugin) {

        this.plugin = plugin;

        runTaskTimer(plugin, 0, 600);

    }

    @Override
    public void run() {
        
        for(String name : WORLDS) {
            
            World world = plugin.getServer().getWorld(name);
            
            if(world != null) {
                world.setTime(16000);
            }
            
        }

    }
}
