package com.starquestminecraft.bukkit.blasters.util;

import net.minecraft.server.v1_10_R1.EntityArrow;

import org.bukkit.entity.Arrow;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftArrow;

public final class NMSUtil {

    private NMSUtil() {

    }

    public static void setArrowPickup(final Arrow arrow, final boolean pickup) {

        EntityArrow nmsarrow = ((CraftArrow)arrow).getHandle();

        nmsarrow.fromPlayer = (pickup ? EntityArrow.PickupStatus.ALLOWED : EntityArrow.PickupStatus.DISALLOWED);

    }

}
