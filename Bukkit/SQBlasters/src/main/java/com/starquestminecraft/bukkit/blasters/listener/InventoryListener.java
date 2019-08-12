package com.starquestminecraft.bukkit.blasters.listener;

import com.starquestminecraft.bukkit.blasters.SQBlasters;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.starquestminecraft.bukkit.blasters.Blaster;

public class InventoryListener implements Listener {

    private final SQBlasters plugin;

    public InventoryListener(final SQBlasters plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event) {

        Player player = (Player)event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        ItemStack item = event.getCurrentItem();

        if(inventory == null) {
            return;
        }

        if(item == null) {
            return;
        }

        if(inventory.getName().equals(SQBlasters.INVENTORY_TITLE_BLASTER_RECIPE)) {
            event.setCancelled(true);
            return;
        }

        if(!inventory.getName().equals(SQBlasters.INVENTORY_TITLE_BLASTER_SELECTION)) {
            return;
        }

        event.setCancelled(true);

        String type = item.getItemMeta().getLore().get(0).substring(Blaster.LORE_PREFIX_TYPE.length());

        ItemStack blaster = plugin.createNewBlaster(type, item.getItemMeta());

        plugin.clearCachedNewBlasterItem(player.getUniqueId());

        addOrDropItem(player, blaster);

        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {

            @Override
            public void run() {

                if(player.isOnline()) {
                    player.closeInventory();
                }

            }

        });

    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {

        Player player = (Player)event.getPlayer();
        Inventory inventory = event.getInventory();

        if(inventory == null) {
            return;
        }

        if(!inventory.getName().equals(SQBlasters.INVENTORY_TITLE_BLASTER_SELECTION)) {
            return;
        }

        ItemStack cached_new_blaster = plugin.getCachedNewBlasterItem(player.getUniqueId());

        if(cached_new_blaster != null) {
            addOrDropItem(player, cached_new_blaster);
        }

    }

    private void addOrDropItem(final Player player, final ItemStack item) {

        Map<Integer, ItemStack> overflow = player.getInventory().addItem(item);

        if(!overflow.isEmpty()) {
            Location loc = player.getLocation();
            for(ItemStack overflow_item : overflow.values()) {
                player.getWorld().dropItemNaturally(loc, overflow_item);
            }
        }

    }

}
