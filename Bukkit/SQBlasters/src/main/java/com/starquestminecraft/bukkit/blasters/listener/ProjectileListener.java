package com.starquestminecraft.bukkit.blasters.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import com.starquestminecraft.bukkit.blasters.SQBlasters;

public class ProjectileListener implements Listener {

    private final SQBlasters plugin;

    public ProjectileListener(final SQBlasters plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileHit(final ProjectileHitEvent event) {

        Projectile projectile = event.getEntity();

        if(projectile.getType() != EntityType.ARROW) {
            return;
        }

        if(isBlasterBolt(projectile)) {
            projectile.remove();
        }

    }

    private boolean isBlasterBolt(final Entity entity) {

        EntityType type = entity.getType();

        if((type != EntityType.ARROW) && (type != EntityType.SPECTRAL_ARROW) && (type != EntityType.TIPPED_ARROW)) {
            return false;
        }

        return entity.hasMetadata("blaster_bolt");

    }

}
