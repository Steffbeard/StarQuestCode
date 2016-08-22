package com.starquestminecraft.bukkit.chestfix.listener;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.starquestminecraft.bukkit.chestfix.Checker;
import com.starquestminecraft.bukkit.chestfix.ChestFix;
import me.lyneira.MachinaCore.ArtificialPlayerInteractEvent;

public class PlayerListener implements Listener {

    private final ChestFix plugin;
    private final Checker checker;

    public PlayerListener(final ChestFix plugin) {

        this.plugin = plugin;
        this.checker = new Checker(plugin);

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockInteract(final PlayerInteractEvent event) {

        if(event instanceof ArtificialPlayerInteractEvent) {
            return;
        }

        if(event.isCancelled() || ((event.getAction() != Action.RIGHT_CLICK_BLOCK) && (event.getAction() != Action.LEFT_CLICK_BLOCK))) {
            return;
        }

        if(event.getPlayer().hasPermission("chestfix.bypass")) {
            return;
        }

        Block block = event.getClickedBlock();

        if((event.getAction() == Action.LEFT_CLICK_BLOCK) && (plugin.getRightClickOnly().contains(block.getType()))) {
            return;
        }

        if((plugin.getInteractBlocks().contains(block.getType())) && (!checker.canSee(event.getPlayer(), event.getClickedBlock()))) {
            sendError(event.getPlayer());
            event.setCancelled(true);
        }

    }

    private void sendError(final Player player) {

        if(this.plugin.getConfig().getBoolean("message")) {
            player.sendMessage(ChatColor.RED + "[ChestFix] " + ChatColor.YELLOW + "You tried to use something you can't see.");
        }

        if(this.plugin.getConfig().getBoolean("log.server-log")) {
            this.plugin.getLogger().info(player.getName() + " freecammed through something.");
        }

        if(this.plugin.getConfig().getBoolean("notify-mods")) {
            plugin.getServer().broadcast(ChatColor.RED + "[ChestFix] " + ChatColor.YELLOW + player.getName() + " used something they couldn't see. This might be lag or a hack.", "chestfix.notify");
        }

    }

}
