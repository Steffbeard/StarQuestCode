package com.starquestminecraft.bukkit.buttonblocker;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ButtonBlocker extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(this, this);

    }

    @EventHandler
    public void onEntityInteract(final EntityInteractEvent event) {

        if(event.getBlock().getType() != Material.WOOD_BUTTON) {
            return;
        }

        if(!(event.getEntity() instanceof Arrow)) {
            return;
        }

        Arrow arrow = (Arrow)event.getEntity();

        if(!(arrow.getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player)arrow.getShooter();

        if(!canBuild(player, event.getBlock())) {
            event.setCancelled(true);
        }

    }

    public void onPlayerInteract(final PlayerInteractEvent event) {

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if(event.getClickedBlock().getType() != Material.WOOD_BUTTON) {
            return;
        }

        if(!canBuild(event.getPlayer(), event.getClickedBlock())) {
            event.setCancelled(true);
        }

    }

    private boolean canBuild(final Player player, final Block block) {

        BlockBreakEvent event = new BlockBreakEvent(block, player);

        getServer().getPluginManager().callEvent(event);

        return !event.isCancelled();

    }

}
