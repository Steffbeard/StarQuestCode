package com.starquestminecraft.bukkit.automators;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class AutomatorListener implements Listener {

    private final SQAutomators plugin;
    private final List<String> inventory_titles;

    public AutomatorListener(final SQAutomators plugin) {

        this.plugin = plugin;

        this.inventory_titles = new ArrayList<>();

        this.inventory_titles.add(AutoCrafter.INVENTORY_TITLE);

    }

    @EventHandler
    public void onItemPickup(final InventoryClickEvent event) {

        if(inventory_titles.contains(event.getInventory().getName())) {
            event.setCancelled(true);
        }

    }

}
