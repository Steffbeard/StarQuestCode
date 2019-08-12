package com.starquestminecraft.bukkit.ingameide;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class IngameIDE extends JavaPlugin implements Listener {

    private static IngameIDE instance;

    private final Map<Player, CodeSession> userMap = new HashMap<>();

    @Override
    public void onEnable() {

        instance = this;

        getServer().getPluginManager().registerEvents(this, this);

    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        String name = cmd.getName().toLowerCase();

        if(name.equals("java")) {

            if(sender instanceof Player) {

                Player plr = (Player)sender;

                if(args.length == 1) {

                    String arg = args[0].toLowerCase();

                    if(arg.equals("new")) {
                        newCode(plr);
                    }
                    else if(arg.equals("execute")) {
                        executeCode(plr);
                    }
                    else if(arg.equals("exit")) {
                        exitCode(plr);
                    }

                    return true;

                }

            }

        }

        return false;

    }

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent event) {

        CodeSession session = userMap.get(event.getPlayer());

        if(session != null) {

            event.setCancelled(true);

            session.addLine(event.getMessage());

        }

    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        userMap.remove(event.getPlayer());
    }

    private void newCode(final Player sender) {

        CodeSession session = userMap.get(sender);

        if(session != null) {
            exitCode(sender);
        }

        userMap.put(sender, new CodeSession(sender));

    }

    public ClassLoader getLoader() {
        return this.getClassLoader();
    }

    private void executeCode(final Player sender) {

        CodeSession session = userMap.get(sender);

        if(session != null) {
            session.execute();
        }
        else {
            sender.sendMessage("You have no code to execute.");
        }

    }

    private void exitCode(final Player sender) {

        userMap.remove(sender);

        sender.sendMessage(ChatColor.GREEN + "========================");

    }

    public static IngameIDE getInstance() {
        return instance;
    }

}
