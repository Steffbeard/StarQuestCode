package com.starquestminecraft.bukkit.beamtransporter.detection;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Detection {

    public static boolean detectTransporter(final Block stainedGlass) {

        if(stainedGlass.getType() != Material.STAINED_GLASS) {
            return false;
        }

        if(stainedGlass.getRelative(BlockFace.NORTH, 2).getType() == Material.WALL_SIGN
            || stainedGlass.getRelative(BlockFace.EAST, 2).getType() == Material.WALL_SIGN
            || stainedGlass.getRelative(BlockFace.WEST, 2).getType() == Material.WALL_SIGN
            || stainedGlass.getRelative(BlockFace.SOUTH, 2).getType() == Material.WALL_SIGN) {

            Material type = stainedGlass.getRelative(BlockFace.NORTH).getType();
            if((type != Material.STEP) && (type != Material.DOUBLE_STEP)) {
                return false;
            }

            type = stainedGlass.getRelative(BlockFace.EAST).getType();
            if((type != Material.STEP) && (type != Material.DOUBLE_STEP)) {
                return false;
            }

            type = stainedGlass.getRelative(BlockFace.WEST).getType();
            if((type != Material.STEP) && (type != Material.DOUBLE_STEP)) {
                return false;
            }

            type = stainedGlass.getRelative(BlockFace.SOUTH).getType();
            if((type != Material.STEP) && (type != Material.DOUBLE_STEP)) {
                return false;
            }

            Block sign = stainedGlass.getRelative(BlockFace.DOWN);

            if(sign.getRelative(BlockFace.NORTH).getType() != Material.LAPIS_BLOCK) {
                return false;
            }

            if(sign.getRelative(BlockFace.EAST).getType() != Material.LAPIS_BLOCK) {
                return false;
            }

            if(sign.getRelative(BlockFace.WEST).getType() != Material.LAPIS_BLOCK) {
                return false;
            }

            if(sign.getRelative(BlockFace.SOUTH).getType() != Material.LAPIS_BLOCK) {
                return false;
            }

            return true;

        }

        return false;

    }

    public static BlockFace getSignDirection(final Block sign) {

        if(detectTransporter(sign.getRelative(BlockFace.NORTH, 2))) {
            return BlockFace.NORTH;
        }

        if(detectTransporter(sign.getRelative(BlockFace.EAST, 2))) {
            return BlockFace.EAST;
        }

        if(detectTransporter(sign.getRelative(BlockFace.WEST, 2))) {
            return BlockFace.WEST;
        }

        if(detectTransporter(sign.getRelative(BlockFace.SOUTH, 2))) {
            return BlockFace.SOUTH;
        }

        return null;

    }

    public static BlockFace getSignDirectionFromStainedGlass(final Block stainedGlass) {

        if(stainedGlass.getRelative(BlockFace.NORTH, 2).getType() == Material.WALL_SIGN) {
            return BlockFace.NORTH;
        }

        if(stainedGlass.getRelative(BlockFace.EAST, 2).getType() == Material.WALL_SIGN) {
            return BlockFace.EAST;
        }

        if(stainedGlass.getRelative(BlockFace.WEST, 2).getType() == Material.WALL_SIGN) {
            return BlockFace.WEST;
        }

        if(stainedGlass.getRelative(BlockFace.SOUTH, 2).getType() == Material.WALL_SIGN) {
            return BlockFace.SOUTH;
        }

        return null;

    }

}
