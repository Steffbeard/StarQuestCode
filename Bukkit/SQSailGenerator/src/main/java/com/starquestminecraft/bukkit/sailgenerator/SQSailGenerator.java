package com.starquestminecraft.bukkit.sailgenerator;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SQSailGenerator extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if(event.getPlayer().getItemInHand().getType() != Material.POISONOUS_POTATO) {
            return;
        }

        Block block = event.getClickedBlock();
        BlockFace facing = getFacing(event.getPlayer());

        getLogger().info(String.valueOf(facing));
        getLogger().info(String.valueOf(block.getType()));

        Block main = block.getRelative(facing);

        getLogger().info(String.valueOf(main.getType()));

        if(main.getType() == Material.AIR) {
            return;
        }

        long time = System.currentTimeMillis();
        Generator g = new Generator(this);
        List<Block> sailWool = g.toggleSail(main, facing, true);
        long time2 = System.currentTimeMillis();

        getLogger().info("Sail generation took " + (time - time2) + " ms.");
        getLogger().info("Created sail of size " + sailWool.size());

    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if(!sender.isOp()) {
            return false;
        }

        if(!cmd.getName().equalsIgnoreCase("spawnsail")) {
            return false;
        }

        Player player = (Player)sender;
        BlockFace facing = getFacing(player);
        Block b = player.getEyeLocation().getBlock().getRelative(facing);
        Block main = b.getRelative(facing);
        sender.sendMessage("Deploying sail!");

        long time = System.currentTimeMillis();
        Generator g = new Generator(this);
        List<Block> sailWool = g.toggleSail(main, facing, true);
        long time2 = System.currentTimeMillis();

        sender.sendMessage("Sail generation took " + (time - time2) + " ms.");
        sender.sendMessage("Created sail of size " + sailWool.size());

        return true;

    }

    private BlockFace getFacing(final Player player) {

        int yaw = (int)player.getLocation().getYaw();

        switch(yaw) {

            case 0:
                return BlockFace.SOUTH;

            case 90:
                return BlockFace.WEST;

            case 180:
                return BlockFace.NORTH;

            case 270:
                return BlockFace.EAST;

        }

        //Let's apply angle differences
        if((yaw >= -45) && (yaw < 45)) {
            return BlockFace.SOUTH;
        }
        else if((yaw >= 45) && (yaw < 135)) {
            return BlockFace.WEST;
        }
        else if((yaw >= -135) && (yaw < -45)) {
            return BlockFace.EAST;
        }
        else {
            return BlockFace.NORTH;
        }

    }

}
