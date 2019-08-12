package com.starquestminecraft.bukkit.sailgenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Generator {

    private final SQSailGenerator plugin;

    public Generator(final SQSailGenerator plugin) {
        this.plugin = plugin;
    }

    public List<Block> toggleSail(final Block main, final BlockFace forwards, final boolean wool) {

        int height = getMastHeight(main);

        plugin.getLogger().info(String.valueOf(main.getType()));
        plugin.getLogger().info("Mast height: " + height);
        List<Block> spar = getSpar(main);
        plugin.getLogger().info("Spar length: " + spar.size());
        List<Block> sailWool = new ArrayList<>();

        for(Block b : spar) {
            sailWool.addAll(createSailBlocks(b, height, forwards, wool));
        }

        plugin.getLogger().info("Sail wool: " + sailWool.size());

        return sailWool;
    }

    private List<Block> createSailBlocks(final Block sparBlock, final int height, final BlockFace forwards, final boolean wool) {

        Material type;

        if(wool) {
            type = Material.WOOL;
        }
        else {
            type = Material.AIR;
        }

        int topHeight = height / 3;
        int bottomHeight = topHeight - 1;
        int midHeight = height - (topHeight + bottomHeight);
        List<Block> retval = new ArrayList<>();
        Block workingBlock = sparBlock;
        BlockFace back = getReverse(forwards);

        for(int i = 0; i < topHeight; i++) {
            workingBlock = workingBlock.getRelative(BlockFace.DOWN).getRelative(forwards);
            workingBlock.setType(type);
            retval.add(workingBlock);
        }

        for(int i = 0; i < midHeight; i++) {
            workingBlock = workingBlock.getRelative(BlockFace.DOWN);
            workingBlock.setType(type);
            retval.add(workingBlock);
        }

        for(int i = 0; i < bottomHeight; i++) {
            workingBlock = workingBlock.getRelative(BlockFace.DOWN).getRelative(back);
            workingBlock.setType(type);
            retval.add(workingBlock);
        }

        return retval;

    }

    private int pythag(final int a, final int b) {
        return (int)Math.sqrt(a * a + b * b);
    }

    private BlockFace getReverse(final BlockFace cardinal) {

        switch(cardinal) {

            case NORTH:
                return BlockFace.SOUTH;

            case SOUTH:
                return BlockFace.NORTH;

            case EAST:
                return BlockFace.WEST;

            case WEST:
                return BlockFace.EAST;

            default:
                return BlockFace.UP;

        }

    }

    private List<Block> getSpar(final Block main) {

        //get the blocks that are the spar block
        List<Block> retval = new ArrayList<>();
        Stack<Block> blockStack = new Stack<>();
        Material mainType = main.getType();

        blockStack.push(main);

        plugin.getLogger().info(mainType.toString());

        do {
            detectSurrounding(blockStack.pop(), mainType, blockStack, retval);
        }
        while(!blockStack.isEmpty());

        return retval;

    }

    private void detectSurrounding(final Block b, final Material mainType, final Stack<Block> blockStack, final List<Block> retval) {

        //off by one?
        for(Block relative : getBlocksSurroundingHorizontal(b)) {

            if(retval.contains(relative)) {
                continue;
            }

            detectBlock(relative, mainType, blockStack, retval);

        }

    }

    private void detectBlock(final Block b, final Material mainType, final Stack<Block> blockStack, final List<Block> retval) {

        if(!b.getType().equals(mainType)) {
            return;
        }

        Block[] adjacents = getBlocksSurroundingHorizontal(b);
        int num = getNumContains(adjacents, mainType);

        if(num >= 2) {
            blockStack.push(b);
            retval.add(b);
        }

    }

    private int getNumContains(final Block[] blocks, final Material type) {

        int retval = 0;

        for(Block b : blocks) {

            if(b.getType().equals(type)) {
                retval++;
            }

        }

        plugin.getLogger().info("Returning " + retval + " from getNumContains()");

        return retval;

    }

    private Block[] getBlocksSurroundingHorizontal(final Block block) {

        Block[] retval = new Block[8];
        int index = 0;

        for(int x = -1; x < 2; x++) {

            for(int z = -1; z < 2; z++) {

                if(!((x == 0) && (z == 0))) {
                    retval[index] = block.getRelative(x, 0, z);
                    index++;
                }

            }

        }

        return retval;

    }

    private int getMastHeight(final Block main) {

        //the mast height is the distance from the main block to the next solid block below
        Block test = main.getRelative(0, -1, 0);
        int height = 0;

        while(test.getType() == Material.AIR) {
            height++;
            test = test.getRelative(0, -1, 0);
        }

        return height;

    }

}
