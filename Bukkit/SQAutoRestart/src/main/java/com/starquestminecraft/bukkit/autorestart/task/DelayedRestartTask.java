package com.starquestminecraft.bukkit.autorestart.task;

import com.starquestminecraft.bukkit.autorestart.SQAutoRestart;

public class DelayedRestartTask implements Runnable {

    private final SQAutoRestart plugin;

    private int time;

    public DelayedRestartTask(final SQAutoRestart plugin, final int time) {

        this.plugin = plugin;
        this.time = time;

    }

    @Override
    public void run() {

        switch(time) {

            case 0:
                broadcast("StarQuest is restarting!");
                plugin.restart();
                return;

            case 1:
                broadcast("StarQuest will be restarting in 1 minute!");
                broadcast("Be sure to unpilot your ships before the restart!");
                break;

            default:
                broadcast("StarQuest will be restarting in " + time + " minutes!");
                break;

        }

        time--;

    }

    private void broadcast(final String msg) {
        
        plugin.command("eb janesudo " + msg);

        plugin.getLogger().info(msg);

    }

}
