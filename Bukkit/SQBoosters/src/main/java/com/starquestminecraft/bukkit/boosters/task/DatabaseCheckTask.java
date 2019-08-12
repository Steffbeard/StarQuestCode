package com.starquestminecraft.bukkit.boosters.task;

import java.util.Map;

import net.md_5.bungee.api.ChatColor;

import com.starquestminecraft.bukkit.boosters.Booster;
import com.starquestminecraft.bukkit.boosters.SQBoosters;

public class DatabaseCheckTask implements Runnable {

    private final SQBoosters plugin;

    public DatabaseCheckTask(final SQBoosters plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {

        Map<Booster.Type, Booster> old_boosters = plugin.getBoosters();

        plugin.refreshBoosters();

        Map<Booster.Type, Booster> new_boosters = plugin.getBoosters();

        for(Booster booster : new_boosters.values()) {

            if(old_boosters.containsKey(booster.getType())) {
                continue;
            }

            String boosterName = booster.getType().getName();
            String multiplierName = booster.getType().getMultiplierName();
            double multiplier = booster.getMultiplier();
            String purchaser = booster.getPurchaser();

            if(purchaser == null) {
                purchaser = "An anonymous player";
            }

            if(booster.getType() == Booster.Type.SHOP) {
                multiplier = plugin.applyExponentialMultiplier(multiplier);
            }

            plugin.getServer().broadcastMessage(ChatColor.GOLD + boosterName + ": " + purchaser + " has purchased the " + multiplierName + " booster for " + getTimeLeft(booster.getExpireTime()) + "! The multiplier is now " + multiplier + "x.");

            if(booster.hasPurchaser()) {
                plugin.getServer().broadcastMessage(ChatColor.GOLD + "You can thank them by giving them money using /thank " + purchaser + " <amount>");
            }

        }

        for(Booster booster : old_boosters.values()) {

            if(new_boosters.containsKey(booster.getType())) {
                continue;
            }

            String boosterName = booster.getType().getName();
            String multiplierName = booster.getType().getMultiplierName();
            double multiplier = booster.getMultiplier();
            String purchaser = booster.getPurchaser();

            if(purchaser == null) {
                purchaser = "an anonymous player";
            }

            if(booster.getType() == Booster.Type.SHOP) {
                multiplier = plugin.applyExponentialMultiplier(multiplier);
            }

            plugin.getServer().broadcastMessage(ChatColor.GOLD + boosterName + ": The " + multiplierName + " booster purchased by " + purchaser + " has expired! The multiplier is now " + multiplier + "x.");

        }

    }

    private static String getTimeLeft(final long timestamp) {

        long diff = ((timestamp / 60000) - (System.currentTimeMillis() / 60000));
        long hours = (diff / 60);
        long minutes = (diff % 60);

        StringBuilder sb = new StringBuilder(24);

        sb.append(hours);
        sb.append(" hour");

        if(hours != 1) {
            sb.append('s');
        }

        if((hours != 0) && (minutes != 0)) {
            sb.append(" and ");
        }

        sb.append(minutes);
        sb.append(" minute");

        if(minutes != 1) {
            sb.append('s');
        }

        return sb.toString();

    }

}
