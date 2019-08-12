package com.starquestminecraft.bukkit.beamtransporter.object;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.starquestminecraft.bukkit.beamtransporter.task.BeamMoveTask;

public class Beam {

    public static final List<Block> beamBlocks = new ArrayList<>();
    public static final List<Block> groundBlocks = new ArrayList<>();

    private final BlockFace direction;
    private final Material material;
    private final Byte color;
    private final BeamMoveTask task;

    private Block bottom;
    private Block middle;
    private Block top;

    public Beam(final Block bottom, final Block middle, final Block top, final BlockFace direction, final Material material, final Byte color) {

        this.bottom = bottom;
        this.middle = middle;
        this.top = top;
        this.direction = direction;
        this.material = material;
        this.color = color;

        task = new BeamMoveTask(this);

    }

    public BeamMoveTask getTask() {
        return task;
    }

    public void taskMove() {

        if(this.direction.equals(BlockFace.UP)) {

            Block target = this.top.getRelative(this.direction);

            //If target block is not air, something is in the way, stop
            if(target.getType() != Material.AIR) {
                remove();
                return;
            }

        }
        else if(this.direction.equals(BlockFace.DOWN)) {

            Block target = this.bottom.getRelative(this.direction);

            //If target block is not air, something is in the way, stop
            if(target.getType() != Material.AIR) {
                remove();
                return;
            }

        }

        move();

    }

    public void move() {

        if(this.direction.equals(BlockFace.UP)) {

            Block target = this.top.getRelative(this.direction);

            if(target.getType() == Material.AIR) {

                //Bukkit.getServer().broadcastMessage("Replaced " + target.getType());
                target.setType(this.material);
                target.setData(this.color);

                beamBlocks.add(target);

            }
            if((this.bottom.getType() == this.material) && (this.bottom.getData() == this.color) && !groundBlocks.contains(this.bottom)) {

                //Bukkit.getServer().broadcastMessage("Replaced " + this.bottomBlock.getType());
                this.bottom.setType(Material.AIR);
                this.bottom.setData((byte)0);

                if(beamBlocks.contains(this.bottom)) {
                    beamBlocks.remove(this.bottom);
                }

            }

            this.bottom = this.middle;
            this.middle = top;
            this.top = target;

        }
        else if(this.direction.equals(BlockFace.DOWN)) {

            Block target = this.bottom.getRelative(this.direction);

            if(target.getType() == Material.AIR) {

                //Bukkit.getServer().broadcastMessage("Replaced " + target.getType());
                target.setType(this.material);
                target.setData(this.color);

                beamBlocks.add(target);

            }
            if(this.top.getType() == this.material && this.top.getData() == this.color) {

                //Bukkit.getServer().broadcastMessage("Replaced " + this.topBlock.getType());
                this.top.setType(Material.AIR);
                this.top.setData((byte)0);

                if(beamBlocks.contains(this.top)) {
                    beamBlocks.remove(this.top);
                }

            }

            this.top = this.middle;
            this.middle = this.bottom;
            this.bottom = target;

        }

    }

    public void remove() {

        //Bukkit.getServer().broadcastMessage("Replaced " + this.topBlock.getType());
        this.top.setType(Material.AIR);
        this.top.setData((byte)0);

        //Bukkit.getServer().broadcastMessage("Replaced " + this.middleBlock.getType());
        this.middle.setType(Material.AIR);
        this.middle.setData((byte)0);

        //Bukkit.getServer().broadcastMessage("Replaced " + this.bottomBlock.getType());
        this.bottom.setType(Material.AIR);
        this.bottom.setData((byte)0);

        beamBlocks.clear();

        this.task.cancel();

    }

}
