package com.starquestminecraft.bukkit.apocalypse.task;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.starquestminecraft.bukkit.apocalypse.SQApocalypse;

public class ScoreUpdateTask extends BukkitRunnable {

    private final SQApocalypse plugin;

    private final RegionManager rm;
    private final WorldGuardPlugin wg;

    public ScoreUpdateTask(final SQApocalypse plugin) {
        
        this.plugin = plugin;

        this.wg = WorldGuardPlugin.inst();
        this.rm = wg.getRegionManager(plugin.getServer().getWorld(plugin.getServer().getServerName()));

    }

    @Override
    public void run() {

        plugin.getLogger().info("Updating scores.");

        for(Player player : plugin.getServer().getOnlinePlayers()) {

            ApplicableRegionSet set = rm.getApplicableRegions(player.getLocation());

            for(ProtectedRegion region : set) {

                if(region.getId().equalsIgnoreCase("OriginStation")) {
                    player.sendMessage("Your score was not updated because you are in the spawn no-pvp zone.");
                }

            }

            plugin.getDB().addScore(player.getUniqueId(), 1);

        }

    }

}
