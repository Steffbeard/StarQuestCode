package com.starquestminecraft.bukkit.explosionfix;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ExplosionFix extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(this, this);

    }

    @EventHandler
    public void onBlockExplosion(final BlockExplodeEvent event) {

        Fireball fireball = (Fireball)event.getBlock().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.FIREBALL);

        fireball.setYield(0.0f);

        getServer().getPluginManager().callEvent(new EntityExplodeEvent(fireball, event.getBlock().getLocation(), event.blockList(), event.getYield()));

        fireball.remove();

    }

}
