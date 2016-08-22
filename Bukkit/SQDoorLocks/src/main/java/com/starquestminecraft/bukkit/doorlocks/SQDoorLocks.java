package com.starquestminecraft.bukkit.doorlocks;

import java.util.EnumSet;
import java.util.Set;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

public class SQDoorLocks extends JavaPlugin implements Listener {

    private final Set<Material> doors = EnumSet.of(
        Material.WOODEN_DOOR,
        Material.IRON_DOOR_BLOCK,
        Material.ACACIA_DOOR,
        Material.BIRCH_DOOR,
        Material.SPRUCE_DOOR,
        Material.DARK_OAK_DOOR,
        Material.JUNGLE_DOOR
    );

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(this, this);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {

        if(event.getBlock().getType() != Material.WALL_SIGN) {
            return;
        }

        Sign sign = (Sign)event.getBlock().getState();

        if(!sign.getLine(0).equalsIgnoreCase("[Private]")) {
            return;
        }

        if(event.getPlayer().hasPermission("lockette.admin.bypass")) {
            event.setCancelled(false);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(final PlayerInteractEvent event) {

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Material type = block.getType();

        if(doors.contains(type)) {

            if(player.hasPermission("lockette.admin.bypass")) {

                event.setUseInteractedBlock(PlayerInteractEvent.Result.ALLOW);
                event.setUseItemInHand(PlayerInteractEvent.Result.ALLOW);

                event.setCancelled(false);

            }
            else if(checkForBrokenSign(block)) {

                player.sendMessage("There was a broken lock sign on this door. Try again.");

                event.setCancelled(true);

            }

        }

        if(!(block.getState() instanceof InventoryHolder)) {
            return;
        }

        Block[] edges = getEdges(block, false, false);

        for(Block b : edges) {

            if(b.getType() != Material.WALL_SIGN) {
                continue;
            }

            BlockFace face = block.getFace(b);

            if(face != getFacingBlockFace(b)) {
                continue;
            }

            Sign sign = (Sign)b.getState();
            String line0 = sign.getLine(0);
            String line1 = sign.getLine(1);

            if(line0.equalsIgnoreCase("[private]")) {

                Player p = getServer().getPlayer(line1);

                if(p != null) {
                    p.sendMessage("Don't lock container blocks.");
                }

                b.breakNaturally();

            }

        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignChange(final SignChangeEvent event) {

        String line0 = event.getLine(0).toLowerCase();

        if(!line0.equals("[private]")) {
            return;
        }

        Block block = event.getBlock();

        if(!checkForDoor(block)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "[SQDoorLocks] Only doors can be locked.");
            return;
        }

        if((doors.contains(block.getRelative(-1, 1, 0).getType())
            || doors.contains(block.getRelative(1, 1, 0).getType())
            || doors.contains(block.getRelative(0, 1, -1).getType())
            || doors.contains(block.getRelative(0, 1, 1).getType()))) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "[SQDoorLocks] Conflict with an existing protected door.");
        }

    }

    private boolean checkForBrokenSign(final Block block) {

        Block down = block.getRelative(BlockFace.DOWN);
        Block up = block.getRelative(BlockFace.UP);
        Material upType = up.getType();
        Material downType = down.getType();

        if(doors.contains(upType)) {

            //sign is mounted on the low door
            if(checkForBrokenSignAroundBlock(up) || checkForBrokenSignAroundBlock(block)) {
                return true;
            }

        }
        else if(doors.contains(downType)) {

            //sign is mounted on high door
            if(checkForBrokenSignAroundBlock(down) || checkForBrokenSignAroundBlock(block)) {
                return true;
            }

        }

        return false;

    }

    private boolean checkForBrokenSignAroundBlock(final Block block) {

        for(Block b : getEdges(block, true, false)) {

            if((b.getType() != Material.WALL_SIGN) && (b.getType() != Material.SIGN_POST)) {
                continue;
            }

            Sign sign = (Sign)b.getState();
            String line0lc = sign.getLine(0).toLowerCase();

            if(line0lc.contains("private") && !line0lc.contains("[private]")) {

                sign.setLine(0, "[Private]");
                sign.update();

                return true;

            }

        }

        return false;

    }

    private boolean checkForDoor(final Block block) {

        BlockFace dir = getGateDirection(block);

        if(dir == null) {
            return false;
        }

        Block front = block.getRelative(dir);

        switch(front.getType()) {

            case CHEST:
            case DISPENSER:
            case DROPPER:
            case FURNACE:
                return false;

        }

        if(doors.contains(front.getType())) {
            return true;
        }

        Block frontdown = front.getRelative(BlockFace.DOWN);

        if(doors.contains(frontdown.getType())) {
            return true;
        }

        Block frontup = front.getRelative(BlockFace.UP);

        if(doors.contains(frontup.getType())) {
            return true;
        }

        return false;

    }

    public static BlockFace getGateDirection(final Block sign) {

        if(sign.getType().equals(Material.WALL_SIGN)) {

            switch(sign.getData()) {

                case 2:
                    return BlockFace.SOUTH;

                case 3:
                    return BlockFace.NORTH;

                case 4:
                    return BlockFace.EAST;

                case 5:
                    return BlockFace.WEST;

                default:
                    return null;
            }

        }

        return null;

    }

    public static Block[] getEdges(final Block block, final boolean includeDiagonals, final boolean includeSelf) {

        int size;

        if(includeDiagonals) {
            size = 18;
        }
        else {
            size = 6;
        }

        if(includeSelf) {
            size++;
        }

        Block[] rval = new Block[size];
        int index = 0;

        //block itself
        if(includeSelf) {
            rval[index++] = block;
        }

        //faces
        rval[index++] = block.getRelative(0, 1, 0);
        rval[index++] = block.getRelative(0, -1, 0);
        rval[index++] = block.getRelative(1, 0, 0);
        rval[index++] = block.getRelative(-1, 0, 0);
        rval[index++] = block.getRelative(0, 0, 1);
        rval[index++] = block.getRelative(0, 0, -1);

        if(includeDiagonals) {

            //edges on the upper side
            rval[index++] = block.getRelative(1, 1, 0);
            rval[index++] = block.getRelative(-1, 1, 0);
            rval[index++] = block.getRelative(0, 1, 1);
            rval[index++] = block.getRelative(0, 1, -1);

            //edges on the lower side
            rval[index++] = block.getRelative(1, -1, 0);
            rval[index++] = block.getRelative(-1, -1, 0);
            rval[index++] = block.getRelative(0, -1, 1);
            rval[index++] = block.getRelative(0, -1, -1);

            //edges on the same plane
            rval[index++] = block.getRelative(1, 0, 1);
            rval[index++] = block.getRelative(-1, 0, 1);
            rval[index++] = block.getRelative(1, 0, -1);
            rval[index++] = block.getRelative(-1, 0, -1);

        }

        return rval;

    }

    public static BlockFace getFacingBlockFace(final Block block) {

        int data = block.getData();

        switch(data) {

            case 2:
                return BlockFace.NORTH;

            case 3:
                return BlockFace.SOUTH;

            case 4:
                return BlockFace.WEST;

            case 5:
                return BlockFace.EAST;

            default:
                return null;

        }

    }

}
