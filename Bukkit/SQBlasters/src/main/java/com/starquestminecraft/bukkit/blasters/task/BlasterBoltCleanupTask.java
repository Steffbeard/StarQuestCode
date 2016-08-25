package com.starquestminecraft.bukkit.blasters.task;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Arrow;

import com.starquestminecraft.bukkit.blasters.SQBlasters;

import org.bukkit.metadata.MetadataValue;

public class BlasterBoltCleanupTask implements Runnable {

    private final SQBlasters plugin;
    private final Set<Arrow> arrows;

    public BlasterBoltCleanupTask(final SQBlasters plugin) {

        this.plugin = plugin;

        this.arrows = Collections.newSetFromMap(new ConcurrentHashMap<>());

    }

    @Override
    public void run() {

        if(arrows.isEmpty()) {
            return;
        }

        Iterator<Arrow> itr = arrows.iterator();

        while(itr.hasNext()) {

            Arrow arrow = itr.next();

            if(arrow.isDead()) {
                itr.remove();
                continue;
            }

            int max_age = 500;
            List<MetadataValue> metadata = arrow.getMetadata("max_age");

            if(!metadata.isEmpty()) {
                for(MetadataValue value : metadata) {
                    if(value.getOwningPlugin().equals(plugin)) {
                        max_age = value.asInt();
                        break;
                    }
                }
            }

            if(arrow.getTicksLived() > max_age) {
                arrow.remove();
                itr.remove();
            }

        }

    }

    public void addArrow(final Arrow arrow) {

        if(arrow.hasGravity()) {
            return;
        }

        this.arrows.add(arrow);

    }

}
