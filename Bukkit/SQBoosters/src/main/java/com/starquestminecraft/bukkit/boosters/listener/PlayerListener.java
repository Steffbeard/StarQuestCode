package com.starquestminecraft.bukkit.boosters.listener;

import java.util.Random;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.material.Wool;

import com.starquestminecraft.bukkit.boosters.Booster;
import com.starquestminecraft.bukkit.boosters.SQBoosters;

public class PlayerListener implements Listener {

    private final SQBoosters plugin;
    private final Random rand;

    public PlayerListener(final SQBoosters plugin) {

        this.plugin = plugin;

        this.rand = new Random();

    }

    @EventHandler
    public void onPlayerExpChange(final PlayerExpChangeEvent event) {

        int amount = event.getAmount();

        if(amount <= 0) {
            return;
        }

        event.setAmount(amount * SQBoosters.multipliers[0]);

    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {

        plugin.sendActiveBoosters(event.getPlayer());

    }

    @EventHandler
    public void onPlayerShearEntity(final PlayerShearEntityEvent event) {

        Booster booster = plugin.getBooster(Booster.Type.SHEEP_SHEAR);

        if((booster == null) || !booster.isActive()) {
            return;
        }

        int multiplier = booster.getMultiplier() - 1;

        if(multiplier <= 0) {
            return;
        }

        Entity entity = event.getEntity();

        if(!entity.getType().equals(EntityType.SHEEP)) {
            return;
        }

        Sheep sheep = (Sheep)entity;
        int amount = 0;

        for(int i = 0; i < multiplier; i++) {
            amount += (rand.nextInt(3) + 1);
        }

        entity.getWorld().dropItem(entity.getLocation(), new Wool(sheep.getColor()).toItemStack(amount));

    }

}
