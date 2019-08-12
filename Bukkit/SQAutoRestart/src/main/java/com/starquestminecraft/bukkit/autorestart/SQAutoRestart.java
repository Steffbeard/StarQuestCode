package com.starquestminecraft.bukkit.autorestart;

import java.io.File;
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.starquestminecraft.bukkit.autorestart.task.DelayedRestartTask;
import com.starquestminecraft.bukkit.autorestart.task.TimeCheckTask;

public class SQAutoRestart extends JavaPlugin {

    @Override
    public void onEnable() {

        if(getServer().getServerName().equals("CoreSystem")) {

            getLogger().info("Server is Core, scheduling restart!");
            // delay an hour and a half; there's no reason to check before then,
            // and if you check too soon it'll restart loop.
            // check every minute after that. players may get picky about their
            // restart times.
            getServer().getScheduler().runTaskTimer(this, new TimeCheckTask(this), 108000, 12000);

        }

    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if(cmd.getName().equalsIgnoreCase("restartall") && sender.hasPermission("SQAutoRestart.restartall")) {

            if(args.length != 1) {
                sender.sendMessage("put a time argument.");
                return true;
            }

            int time = Integer.parseInt(args[0]);

            sender.sendMessage("Restarting servers!");

            restartDelayed(time);

            return true;

        }

        if(cmd.getName().equalsIgnoreCase("autorestart") && (sender instanceof ConsoleCommandSender)) {

            System.out.println("Server stopping in five seconds...");

            getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

                @Override
                public void run() {
                    getServer().shutdown();
                }

            }, 20 * 5L);

            return true;

        }

        return false;

    }

    private void restartDelayed(final int time) {

        getServer().getScheduler().runTaskTimer(this, new DelayedRestartTask(this, time), 0, 1200);

    }

    public void restart() {

        command("ee autorestart");
        command("eb end");

        executeBatch("autorestart-helper");

    }

    public void command(final String command) {
        getServer().dispatchCommand(getServer().getConsoleSender(), command);
    }

    private static void executeBatch(final String filename) {

        try {
            Runtime.getRuntime().exec("c:\\windows\\system32\\cmd.exe /d /c " + filename + ".bat", null, new File("C:\\SQ4\\BungeeUtils"));
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }

    }

}
