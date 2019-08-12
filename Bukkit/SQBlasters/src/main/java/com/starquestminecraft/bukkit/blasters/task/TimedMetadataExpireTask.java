package com.starquestminecraft.bukkit.blasters.task;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.starquestminecraft.bukkit.blasters.Blaster;
import com.starquestminecraft.bukkit.blasters.SQBlasters;

public class TimedMetadataExpireTask implements Runnable {

    private final SQBlasters plugin;
    private final Player player;
    private final String metadata_key;
    private final int ammo;
    private final int ammo_max;

    public TimedMetadataExpireTask(final SQBlasters plugin, final Player player, final String metadata_key, final int ammo, final int ammo_max) {

        this.plugin = plugin;
        this.player = player;
        this.metadata_key = metadata_key;
        this.ammo = ammo;
        this.ammo_max = ammo_max;

    }

    @Override
    public void run() {

        player.removeMetadata(metadata_key, plugin);

        ItemStack item = player.getInventory().getItemInMainHand();

        if(!item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        if(meta.getDisplayName().equals(ChatColor.GREEN + "Reloading...")) {

            meta.setDisplayName(Blaster.formatDisplayName(ammo, ammo_max));

            item.setItemMeta(meta);

            player.updateInventory();

        }

    }

}
