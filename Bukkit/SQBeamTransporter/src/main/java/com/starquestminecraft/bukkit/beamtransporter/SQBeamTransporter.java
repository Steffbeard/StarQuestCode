package com.starquestminecraft.bukkit.beamtransporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.starquestminecraft.bukkit.beamtransporter.object.Beam;
import com.starquestminecraft.bukkit.beamtransporter.object.BeamTransporter;

public class SQBeamTransporter extends JavaPlugin {

    public static List<Entity> beamEntities = new ArrayList<>();
    public static Map<Entity, BeamTransporter> transporterMap = new HashMap<>();
    public static Map<BeamTransporter, Long> timeoutMap = new HashMap<>();
    public static List<BeamTransporter> currentlyBeaming = new ArrayList<>();
    public static SQBeamTransporter plugin;
    public static List<BeamTransporter> beamTransporterList = new ArrayList<>();

    @Override
    public void onEnable() {

        plugin = this;

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new Events(), this);

        if(getConfig().getConfigurationSection("BeamTransporters") == null) {
            getLogger().warning("Could not find any players in config, moving on.");
            return;
        }

        for(String uuid : getConfig().getConfigurationSection("BeamTransporters").getKeys(false)) {

            for(String xz : getConfig().getConfigurationSection("BeamTransporters." + uuid).getKeys(false)) {

                if(uuid == null) {
                    return;
                }

                int x = getConfig().getInt("BeamTransporters." + uuid + "." + xz + ".x");
                int z = getConfig().getInt("BeamTransporters." + uuid + "." + xz + ".z");
                String worldName = getConfig().getString("BeamTransporters." + uuid + "." + xz + ".world");

                OfflinePlayer p = getServer().getOfflinePlayer(uuid);

                if(p == null) {
                    getLogger().warning("!!!!Player is null!!!!");
                    continue;
                }

                BeamTransporter.createBeamTransporterFromXZ(x, z, worldName, p);
                getLogger().info("Successfully created beam transporter at X: " + x + " Z: " + z + " In world: " + worldName + ". Owned by player: " + p.getName());

            }

        }

    }

    @Override
    public void onDisable() {

        for(BeamTransporter bt : SQBeamTransporter.beamTransporterList) {

            Block stainedGlass = bt.floorMap.firstEntry().getValue().getStainedGlass();
            OfflinePlayer owner = bt.owner;

            if(!owner.hasPlayedBefore()) {
                getLogger().warning("!!!!Player is null!!!!");
                continue;
            }

            getConfig().set("BeamTransporters." + owner.getUniqueId() + "." + stainedGlass.getLocation().getBlockX() + stainedGlass.getLocation().getBlockZ() + ".x", stainedGlass.getLocation().getBlockX());
            getConfig().set("BeamTransporters." + owner.getUniqueId() + "." + stainedGlass.getLocation().getBlockX() + stainedGlass.getLocation().getBlockZ() + ".z", stainedGlass.getLocation().getBlockZ());
            getConfig().set("BeamTransporters." + owner.getUniqueId() + "." + stainedGlass.getLocation().getBlockX() + stainedGlass.getLocation().getBlockZ() + ".world", stainedGlass.getLocation().getWorld().getName());

        }

        saveConfig();

    }

    public static SQBeamTransporter getPluginMain() {
        return plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if(label.equalsIgnoreCase("newbeam")) {

            if(args[0] == null) {
                return false;
            }

            if(args[0].equalsIgnoreCase("up")) {

                Player player = (Player)sender;

                //Prepare variables for beam
                Block bottomBlock = player.getLocation().getBlock();
                Block middleBlock = bottomBlock.getRelative(BlockFace.UP);
                Block topBlock = middleBlock.getRelative(BlockFace.UP);
                BlockFace direction = BlockFace.UP;

                //Create new beam and start it
                Beam beam = new Beam(bottomBlock, middleBlock, topBlock, direction, Material.STAINED_GLASS, (byte)3);
                beam.getTask().runTaskTimer(this, 3, 3);

                return true;

            }
            else if(args[0].equalsIgnoreCase("down")) {

                Player player = (Player)sender;

                //Prepare variables for beam
                Block bottomBlock = player.getLocation().getBlock();
                Block middleBlock = bottomBlock.getRelative(BlockFace.UP);
                Block topBlock = middleBlock.getRelative(BlockFace.UP);
                BlockFace direction = BlockFace.DOWN;

                //Create new beam and start it
                Beam beam = new Beam(bottomBlock, middleBlock, topBlock, direction, Material.STAINED_GLASS, (byte)2);
                beam.getTask().runTaskTimer(this, 3, 3);

                return true;

            }

        }
        else if(label.equalsIgnoreCase("beamblocks")) {
            sender.sendMessage("BeamBlocks: " + Beam.beamBlocks);
            return true;
        }
        else if(label.equalsIgnoreCase("groundblocks")) {
            sender.sendMessage("GroundBlocks: " + Beam.groundBlocks);
            return true;
        }
        else if(label.equalsIgnoreCase("helix")) {

            new BukkitRunnable() {

                Player player = (Player)sender;
                Location loc = player.getLocation();
                double t = 0;
                double r = 1;

                @Override
                public void run() {

                    t = t + (Math.PI / 16);

                    double x = r * Math.cos(t);
                    double y = 0.1 * t; //Vertical speed
                    double z = r * Math.sin(t);

                    loc.add(x, y, z);
                    loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 0, 0, 0, 100);
                    loc.subtract(x, y, z);

                    if(t > (Math.PI * 20)) {
                        this.cancel();
                    }

                }

            }.runTaskTimer(SQBeamTransporter.this, 0, 1);

        }
        else if(label.equalsIgnoreCase("helix2")) {

            Player player = (Player)sender;

            new BukkitRunnable() {

                @Override
                public void run() {

                    for(double y = 0; y <= 8; y += 0.01) {

                        double adjustedX = 1 * Math.cos(y);
                        double adjustedZ = 1 * Math.sin(y);
                        Location loc = player.getLocation();

                        loc.add(adjustedX, y, adjustedZ);
                        loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 0, -10, 0, 1);
                        loc.subtract(adjustedX, y, adjustedZ);

                    }

                }

            }.runTaskTimer(SQBeamTransporter.this, 0, 10);

        }

        return false;

    }

}
