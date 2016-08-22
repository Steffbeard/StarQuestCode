package com.starquestminecraft.bukkit.apocalypse.task;

import java.util.Random;

import com.starquestminecraft.bukkit.apocalypse.SQApocalypse;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class DestroyTask extends ApocalypseTask {

    private final Random rand;

    public DestroyTask(final World world, final int stage) {
        
        super(world, stage);

        this.rand = new Random();

    }

    @Override
    public void run() {

        if(rand.nextDouble() < 0.1) {

            Chunk chunk = getRandomChunk(world);
            Block block = getBlockInChunk(chunk);
            Location loc = block.getLocation();

            world.strikeLightning(loc);
            world.createExplosion(loc, 4.0F);

        }

    }

    private Block getBlockInChunk(final Chunk chunk) {

        int x = chunk.getX() + rand.nextInt(16);
        int z = chunk.getZ() + rand.nextInt(16);

        return chunk.getWorld().getHighestBlockAt(x, z);

    }

    private Chunk getRandomChunk(final World world) {

        if(rand.nextDouble() > 0.5) {

            int radius = SQApocalypse.radius;
            int x = rand.nextInt(radius * 2) - radius;
            int z = rand.nextInt(radius * 2) - radius;
            Chunk c = world.getChunkAt(x, z);

            return c;

        }

        Chunk[] chunks = world.getLoadedChunks();
        Chunk c = chunks[rand.nextInt(chunks.length)];

        if(Math.abs(c.getX()) < 100 && Math.abs(c.getZ()) < 100) {

            //it's a spawn chunk pick again
            int radius = SQApocalypse.radius;
            int x = rand.nextInt(radius * 2) - radius;
            int z = rand.nextInt(radius * 2) - radius;

            c = world.getChunkAt(x, z);

        }

        return c;


    }

}
