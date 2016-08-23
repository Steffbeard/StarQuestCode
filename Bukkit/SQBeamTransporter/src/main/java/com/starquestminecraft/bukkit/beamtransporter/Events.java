package com.starquestminecraft.bukkit.beamtransporter;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.starquestminecraft.bukkit.beamtransporter.detection.Detection;
import com.starquestminecraft.bukkit.beamtransporter.object.Beam;
import com.starquestminecraft.bukkit.beamtransporter.object.BeamTransporter;
import com.starquestminecraft.bukkit.beamtransporter.object.Floor;

public class Events implements Listener {

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {

        Action action = event.getAction();
        Block block = event.getClickedBlock();

        if(action.equals(Action.LEFT_CLICK_BLOCK)) {

            if(block.getType() != Material.WALL_SIGN) {
                return;
            }

            if(!(block.getState() instanceof Sign)) {
                return;
            }

            Sign sign = (Sign)block.getState();

            if(!sign.getLine(2).equals(ChatColor.GOLD + "Transporter")) {
                return;
            }

            BlockFace signDirection = Detection.getSignDirection(sign.getBlock());

            if(signDirection == null) {
                return;
            }

            if(!event.getPlayer().isSneaking()) {
                return;
            }

            if(Detection.detectTransporter(sign.getBlock().getRelative(signDirection).getRelative(signDirection))) {

                Block stainedGlass = sign.getBlock().getRelative(signDirection).getRelative(signDirection);

                BeamTransporter beamTransporter = new BeamTransporter(stainedGlass, event.getPlayer());

                if(event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                    event.setCancelled(true);
                }

                event.getPlayer().sendMessage(ChatColor.GREEN + "Successfully created a beam transporter!");
                event.getPlayer().sendMessage(ChatColor.AQUA + "Detected " + ChatColor.GOLD + beamTransporter.floorMap.size() + ChatColor.AQUA + " floors.");

            }

        }
        else if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

            if(block.getType() != Material.WALL_SIGN) {
                return;
            }

            if(!(block.getState() instanceof Sign)) {
                return;
            }

            Sign sign = (Sign)block.getState();

            if(sign.getLine(0).equalsIgnoreCase("[btransporter]")) {

                sign.setLine(0, "");
                sign.setLine(1, ChatColor.GOLD + "Beam");
                sign.setLine(2, ChatColor.GOLD + "Transporter");
                sign.update();

            }
            else if(sign.getLine(2).equals(ChatColor.GOLD + "Transporter")) {

                BlockFace signDirection = Detection.getSignDirection(sign.getBlock());

                if(signDirection == null) {
                    event.getPlayer().sendMessage(ChatColor.RED + "Error - Improperly built transporter.");
                    return;
                }

                Block stainedGlass = sign.getBlock().getRelative(signDirection).getRelative(signDirection);

                Entity passenger = event.getPlayer();

                if(Detection.detectTransporter(sign.getBlock().getRelative(signDirection).getRelative(signDirection))) {

                    BeamTransporter beamTransporter = BeamTransporter.getBeamTransporterFromStainedGlass(stainedGlass);

                    for(Entity entity : event.getPlayer().getNearbyEntities(7, 7, 7)) {
                        if(BeamTransporter.isEntityOnTransporter(entity)) {
                            passenger = entity;
                            break;
                        }
                    }

                    if(beamTransporter == null) {
                        event.getPlayer().sendMessage(ChatColor.RED + "Error - You have to redetect the transporter before using it.");
                    }
                    else {
                        beamTransporter.beamToGround(passenger);
                    }

                }

            }

        }
        else if(event.getAction().equals(Action.RIGHT_CLICK_AIR)) {

            if(event.getPlayer().getItemInHand().getType() == Material.WATCH) {

                Block stainedGlass = event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);

                if(BeamTransporter.getBeamTransporterFromStainedGlass(stainedGlass) != null) {

                    BeamTransporter bt = BeamTransporter.getBeamTransporterFromStainedGlass(stainedGlass);
                    Floor from = BeamTransporter.getFloorFromStainedGlass(stainedGlass, bt);
                    Floor to = BeamTransporter.getAboveFloor(event.getPlayer().getLocation(), bt);

                    if(to == null) {
                        event.getPlayer().sendMessage(ChatColor.RED + "Error - No floor above you.");
                        return;
                    }

                    bt.beamToFloor(from, to, event.getPlayer(), event.getPlayer(), false);

                    return;

                }
                else if(SQBeamTransporter.beamEntities.contains(event.getPlayer())) {
                    event.getPlayer().sendMessage(ChatColor.RED + "Error - You are already getting beamed.");
                    return;
                }

                for(BeamTransporter bt : SQBeamTransporter.beamTransporterList) {

                    int x = bt.floorMap.firstEntry().getValue().getStainedGlass().getLocation().getBlockX();
                    int z = bt.floorMap.firstEntry().getValue().getStainedGlass().getLocation().getBlockZ();

                    if(event.getPlayer().getLocation().getBlockX() == x && event.getPlayer().getLocation().getBlockZ() == z) {

                        Floor to = bt.floorMap.firstEntry().getValue();
                        Block elevatorGlass = to.getStainedGlass();
                        Floor from = BeamTransporter.getGroundFloor(elevatorGlass);

                        bt.isGoingFromGround = true;
                        bt.beamToFloor(from, to, event.getPlayer(), event.getPlayer(), true);

                    }

                }

            }

        }

    }

    @EventHandler
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {

        if(event.getHand().equals(EquipmentSlot.OFF_HAND)) {
            return;
        }

        if(event.getPlayer().getItemInHand().getType() == Material.WATCH) {

            if(event.getRightClicked().equals(event.getPlayer())) {
                return;
            }

            Block stainedGlass = event.getRightClicked().getLocation().getBlock().getRelative(BlockFace.DOWN);

            if(BeamTransporter.getBeamTransporterFromStainedGlass(stainedGlass) != null) {

                BeamTransporter bt = BeamTransporter.getBeamTransporterFromStainedGlass(stainedGlass);
                Floor from = BeamTransporter.getFloorFromStainedGlass(stainedGlass, bt);
                Floor to = BeamTransporter.getAboveFloor(event.getRightClicked().getLocation(), bt);

                bt.beamToFloor(from, to, event.getRightClicked(), event.getPlayer(), false);
                event.getPlayer().sendMessage(ChatColor.GREEN + "Beaming up the entity: " + event.getRightClicked().getType());

                return;

            }

            for(BeamTransporter bt : SQBeamTransporter.beamTransporterList) {

                int x = bt.floorMap.firstEntry().getValue().getStainedGlass().getLocation().getBlockX();
                int z = bt.floorMap.firstEntry().getValue().getStainedGlass().getLocation().getBlockZ();

                if(event.getRightClicked().getLocation().getBlockX() == x && event.getRightClicked().getLocation().getBlockZ() == z) {

                    Floor to = bt.floorMap.firstEntry().getValue();
                    Block elevatorGlass = to.getStainedGlass();
                    Floor from = BeamTransporter.getGroundFloor(elevatorGlass);

                    bt.isGoingFromGround = true;

                    bt.beamToFloor(from, to, event.getRightClicked(), event.getPlayer(), true);

                    event.getPlayer().sendMessage(ChatColor.GREEN + "Beaming up the entity: " + event.getRightClicked().getType());

                    return;

                }

            }

        }

    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {

        if(Beam.groundBlocks.contains(event.getBlock())) {
            Beam.groundBlocks.remove(event.getBlock());
        }

    }

}
