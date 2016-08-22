package com.starquestminecraft.bukkit.autorestart.task;

import java.util.Calendar;

import com.starquestminecraft.bukkit.autorestart.SQAutoRestart;
import com.whirlwindgames.dibujaron.sqempire.SQEmpire;

public class TimeCheckTask implements Runnable {

    private final SQAutoRestart plugin;

    public TimeCheckTask(final SQAutoRestart plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        //should restart at 9 AM and 9 PM server time (12 AM and 12 PM EST)
        //9 PM is hour 21, java calendar is military time
        if((hour == 9) || (hour == 21)) {

            //time for a restart
            SQEmpire.automaticRestart = true;

            plugin.getServer().getScheduler().runTaskTimer(plugin, new DelayedRestartTask(plugin, 3), 1200, 1200);

        }

    }

}
