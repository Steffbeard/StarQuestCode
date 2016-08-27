package com.starquestminecraft.bukkit.space;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

class SuffocationTask extends BukkitRunnable {

    private final SQSpace plugin;
    private final Player player;
    private boolean canceled;

    SuffocationTask(final SQSpace plugin, final Player player) {

        this.plugin = plugin;
        this.player = player;

    }

    @Override
    public void cancel() {

        canceled = true;

        super.cancel();

        // Clear the player from the list of suffocating players
        plugin.removeSuffocating(player);

        // Check if we need to trigger a different speed suffocation task, otherwise tell them they are not suffocating
        if(!plugin.doSuffocationCheck(player)) {
            player.sendMessage(ChatColor.AQUA + "[Space] " + ChatColor.GREEN + "You are no longer suffocating!");
        }

    }

    @Override
    public void run() {

        if(!plugin.canSuffocate(player)) {
            this.cancel();
        }

        if(canceled) {
            return;
        }

        if((player.getGameMode() == GameMode.SURVIVAL) || (player.getGameMode() == GameMode.ADVENTURE)) {
            player.damage(1.0D);
        }

    }

}
