package com.starquestminecraft.bukkit.apocalypse;

import com.starquestminecraft.bukkit.apocalypse.task.ScoreUpdateTask;
import com.starquestminecraft.bukkit.apocalypse.task.DestroyTask;
import com.starquestminecraft.bukkit.apocalypse.task.PlayerBurnTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class SQApocalypse extends JavaPlugin implements Listener {

    public static int stage;
    public static boolean verbose;
    public static int radius = getRadius();
    public static double day;
    private static SQApocalypse instance;

    private SQLDatabase database;
    private DestroyTask task;
    private PlayerBurnTask task2;
    private ScoreUpdateTask task3;

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if(args.length == 0) {
            return false;
        }

        String arg = args[0];

        if(arg.equals("score")) {
            System.out.println("Displaying score.");
            displayScore(sender);
            return true;
        }

        if(arg.equals("top")) {
            System.out.println("Displaying top.");
            displayTop(sender);
            return true;
        }

        if(arg.equals("modify")) {

            if(sender instanceof ConsoleCommandSender) {
                database.addScore(Bukkit.getOfflinePlayer(args[1]).getUniqueId(), Integer.parseInt(args[2]));
            }

        }

        return false;

    }

    private void displayTop(final CommandSender sender) {

        if(!(sender instanceof Player)) {
            return;
        }

        getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {

            @Override
            public void run() {
                database.displayTop((Player)sender);
            }

        });

    }

    private void displayScore(final CommandSender sender) {

        if(!(sender instanceof Player)) {
            return;
        }

        final Player player = (Player)sender;

        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {

            @Override
            public void run() {

                int score = database.getScore(player.getUniqueId());

                player.sendMessage("Your score is: " + score);

            }

        });

    }

    @Override
    public void onEnable() {

        instance = this;

        String name = Bukkit.getServerName();

        database = new SQLDatabase(this);

        getServer().getPluginManager().registerEvents(this, this);

        saveDefaultConfig();

        day = getConfig().getDouble("day");
        verbose = getConfig().getBoolean("verbose");
        stage = getStageFromDay(day);

        //every time the server restarts update it by half a day
        getConfig().set("day", day + 0.5);
        saveConfig();

        task3 = new ScoreUpdateTask(this);
        task3.runTaskTimerAsynchronously(this, 60 * 20, 60 * 20);

        if(name.equals("Regalis") || name.equals("Defalos") || name.equals("Digitalia")) {
            return;
        }

        if(stage > 0) {

            World world = getServer().getWorld(getServer().getServerName());

            task2 = new PlayerBurnTask(world, stage);
            task2.runTaskTimer(this, 20, 20);

            if(stage > 2) {
                task = new DestroyTask(world, stage);
                task.runTaskTimer(this, 20 * 60, 20 * 60);
            }

        }

    }

    public SQLDatabase getDB() {
        return database;
    }

    private int getStageFromDay(final double day) {

        if(day < 3.0) {
            return 0;
        }

        if(day < 5.0) {
            return 1;
        }

        if(day < 7.0) {
            return 2;
        }

        if(day < 9.0) {
            return 3;
        }

        if(day < 11.0) {
            return 4;
        }

        return 5;

    }

    private static int getRadius() {

        String name = Bukkit.getServerName();

        switch(name.toLowerCase()) {

            case "boletarian":
            case "boskevine":
            case "quavara":
            case "kelakaria":
            case "iffrizar":
            case "valadro":
                return 3000;

            default:
                return 2000;

        }

    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {

        final Player player = event.getPlayer();

        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

            @Override
            public void run() {

                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

                player.sendMessage(ChatColor.RED + "=========================================");
                player.sendMessage(ChatColor.GOLD + "Star" + ChatColor.BLUE + "Quest" + ChatColor.RED + " Solar Apocalypse: Day " + (int)day);
                player.sendMessage(ChatColor.RED + "New Players - come back in a week! SQ is resetting its worlds, this is the end-of-world event!");
                player.sendMessage(ChatColor.RED + "We're glad you're here, but if you stay, you'll probably die a fiery death. :(");
                player.sendMessage(ChatColor.RED + "Check http://starquestminecraft.com for more information.");
                player.sendMessage(ChatColor.RED + "=========================================");

            }

        }, 20 * 10L);

    }

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {

        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {

            @Override
            public void run() {

                int killedScore = database.getScore(event.getEntity().getUniqueId());

                if(killedScore - 30 < 0) {
                    database.updateScore(event.getEntity().getUniqueId(), 0);
                }
                else {
                    database.addScore(event.getEntity().getUniqueId(), -30);
                }

                event.getEntity().sendMessage("You lost 30 points because you died.");

                Entity killer = event.getEntity().getKiller();

                if(killer instanceof Player) {

                    Player pkill = (Player)killer;

                    if(pkill == event.getEntity()) {
                        return;
                    }

                    if(RecentKillHandler.hasKilledWithinHour(pkill, event.getEntity())) {
                        killer.sendMessage("You have killed this player within an hour. No points were awarded.");
                        return;
                    }
                    else {
                        RecentKillHandler.addRecentKill(pkill, event.getEntity());
                        pkill.sendMessage("This kill has been added to the recent kills database. You cannot kill this player again within an hour.");
                    }

                    int newDeadScore = killedScore - 30;

                    if(newDeadScore <= 0) {
                        database.addScore(pkill.getUniqueId(), killedScore);
                        event.getEntity().sendMessage("You gained " + killedScore + " points for the kill. (they don't have 30 to lose)");
                    }
                    else {
                        database.addScore(pkill.getUniqueId(), 30);
                        event.getEntity().sendMessage("You gained 30 points for this kill.");
                    }

                }

            }

        });

    }

    public static Plugin getInstance() {
        return instance;
    }

}
