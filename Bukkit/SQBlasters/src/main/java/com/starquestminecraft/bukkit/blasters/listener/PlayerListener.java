package com.starquestminecraft.bukkit.blasters.listener;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.starquestminecraft.bukkit.blasters.Blaster;
import com.starquestminecraft.bukkit.blasters.SQBlasters;
import com.starquestminecraft.bukkit.blasters.util.ItemUtil;

public class PlayerListener implements Listener {

    private final SQBlasters plugin;

    public PlayerListener(final SQBlasters plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {

        switch(event.getAction()) {

            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                onPlayerRightClick(event);
                break;

            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                onPlayerLeftClick(event);
                break;

        }

    }

    private void onPlayerLeftClick(final PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        Blaster blaster = plugin.getBlaster(item);

        if(blaster == null) {
            return;
        }

        plugin.toggleScope(player, blaster.getScope());

        event.setCancelled(true);

    }

    private void onPlayerRightClick(final PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if(!ItemUtil.isTypeWithMeta(item, Blaster.MATERIAL)) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        Blaster blaster = plugin.getBlaster(item, meta);

        if(blaster == null) {
            return;
        }

        List<String> lore = meta.getLore();

        //Checking to see if the blaster has correct lore
        if(!Blaster.checkLore(lore)) {

            blaster.resetLore(lore);

            meta.setLore(lore);
            meta.setDisplayName(blaster.formatDisplayName());

            item.setItemMeta(meta);

            player.updateInventory();

        }

        if(blaster.getType() == Blaster.Type.AUTOMATIC) {
            plugin.toggleAutomatic(player);
        }
        else {
            plugin.useBlaster(player, blaster, item, meta);
        }

        event.setCancelled(true);

    }

}
