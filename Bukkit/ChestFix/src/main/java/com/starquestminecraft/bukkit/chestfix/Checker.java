package com.starquestminecraft.bukkit.chestfix;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.material.Door;
import org.bukkit.util.BlockIterator;

public class Checker {

    private final ChestFix plugin;

    public Checker(final ChestFix plugin) {
        this.plugin = plugin;
    }

    public boolean isFacing(final Player p, final Block b) {
        return isFacing(p.getLocation(), b);
    }

    public boolean isFacing(final Location loc, final Block b) {

        BlockIterator itr = new BlockIterator(loc, 0.0D, 5);

        while(itr.hasNext()) {

            Block c = itr.next();

            if(c.equals(b)) {
                return true;
            }

            if(!this.plugin.getTransparentBlocks().contains(c.getType())) {

                if((b.getType() == org.bukkit.Material.CHEST) && (getChestNextTo(b) == c)) {
                    return true;
                }

                if(!(c.getState().getData() instanceof Door)) {
                    break;
                }

                Door d = (Door)c.getState().getData();

                if(d.isTopHalf()) {
                    d = (Door)c.getRelative(0, -1, 0).getState().getData();
                }

                if(!d.isOpen()) {
                    break;
                }
            }
        }

        return false;
    }

    public boolean canSee(final Player p, final Block b) {

        Location pLoc = p.getLocation().clone();
        Location bLoc = b.getLocation().clone();

        pLoc.add(0.0D, 1.62D, 0.0D);

        if(isFacing(pLoc, b)) {
            return true;
        }

        Location corner = lookAt(pLoc, bLoc);

        if(isFacing(corner, b)) {
            return true;
        }

        bLoc = b.getLocation().clone().add(1.0D, 0.0D, 0.0D);
        corner = lookAt(pLoc, bLoc);

        if(isFacing(corner, b)) {
            return true;
        }

        bLoc = b.getLocation().clone().add(0.0D, 1.0D, 0.0D);
        corner = lookAt(pLoc, bLoc);

        if(isFacing(corner, b)) {
            return true;
        }

        bLoc = b.getLocation().clone().add(0.0D, 0.0D, 1.0D);
        corner = lookAt(pLoc, bLoc);

        if(isFacing(corner, b)) {
            return true;
        }

        bLoc = b.getLocation().clone().add(1.0D, 1.0D, 0.0D);
        corner = lookAt(pLoc, bLoc);

        if(isFacing(corner, b)) {
            return true;
        }

        bLoc = b.getLocation().clone().add(1.0D, 0.0D, 1.0D);
        corner = lookAt(pLoc, bLoc);

        if(isFacing(corner, b)) {
            return true;
        }

        bLoc = b.getLocation().clone().add(0.0D, 1.0D, 1.0D);
        corner = lookAt(pLoc, bLoc);

        if(isFacing(corner, b)) {
            return true;
        }

        bLoc = b.getLocation().clone().add(1.0D, 1.0D, 1.0D);
        corner = lookAt(pLoc, bLoc);

        if(isFacing(corner, b)) {
            return true;
        }

        return false;

    }

    public Location lookAt(Location loc, final Location lookat) {

        loc = loc.clone();

        double dx = lookat.getX() - loc.getX();
        double dy = lookat.getY() - loc.getY();
        double dz = lookat.getZ() - loc.getZ();

        if(dx != 0.0D) {

            if(dx < 0.0D) {
                loc.setYaw(4.712389F);
            }
            else {
                loc.setYaw(1.5707964F);
            }

            loc.setYaw(loc.getYaw() - (float)Math.atan(dz / dx));

        }
        else if(dz < 0.0D) {
            loc.setYaw(3.1415927F);
        }

        double dxz = Math.sqrt(Math.pow(dx, 2.0D) + Math.pow(dz, 2.0D));

        float pitch = (float)-Math.atan(dy / dxz);

        loc.setYaw(-loc.getYaw() * 180.0F / 3.1415927F + 360.0F);

        loc.setPitch(pitch * 180.0F / 3.1415927F);

        return loc;

    }

    private Block getChestNextTo(final Block b) {

        BlockFace areaAround[] = {BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH};

        Block[] c = new Block[4];

        int count = 0;
        // Simple block checker in an area
        for(BlockFace f : areaAround) {
            c[count] = b.getRelative(f);
            count++;
        }

        Block[] arrayOfBlock1;
        int j = (arrayOfBlock1 = c).length;

        for(int i = 0; i < j; i++) {

            Block d = arrayOfBlock1[i];

            if(d.getType() == org.bukkit.Material.CHEST) {
                return d;
            }

        }

        return null;

    }

}
