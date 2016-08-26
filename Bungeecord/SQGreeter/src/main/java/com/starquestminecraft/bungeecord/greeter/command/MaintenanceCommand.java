package com.starquestminecraft.bungeecord.greeter.command;

import java.util.StringJoiner;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import com.starquestminecraft.bungeecord.SQBungeeCommand;
import com.starquestminecraft.bungeecord.greeter.SQGreeter;
import com.starquestminecraft.bungeecord.util.UUIDFetcher;

public class MaintenanceCommand extends SQBungeeCommand<SQGreeter> {

    private static final TextComponent TC_DISABLED = new TextComponent("Maintenance mode disabled.");
    private static final TextComponent TC_ENABLED = new TextComponent("Maintenance mode enabled.");
    private static final TextComponent TC_ERROR_UNKNOWN_PLAYER = new TextComponent("Unknown player!");
    private static final TextComponent TC_USAGE = new TextComponent("Usage: /maintenance <toggle/player>");

    static {

        TC_DISABLED.setColor(ChatColor.GREEN);
        TC_ENABLED.setColor(ChatColor.GREEN);
        TC_ERROR_UNKNOWN_PLAYER.setColor(ChatColor.RED);

    }

    public MaintenanceCommand(final SQGreeter plugin) {
        super(plugin, "maintenance");
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {

        if(sender instanceof ProxiedPlayer) {
            sender.sendMessage(TC_MESSAGE_CONSOLE_ONLY);
            return;
        }

        if(args.length == 0) {
            sender.sendMessage(TC_USAGE);
            return;
        }

        if(args[0].equals("toggle")) {

            StringJoiner sj = new StringJoiner(" ");

            for(int i = 1; i < args.length; i++) {
                sj.add(args[i]);
            }

            plugin.getMaintenanceMode().toggleEnabled(sj.toString());

            if(plugin.getMaintenanceMode().isEnabled()) {
                sender.sendMessage(TC_ENABLED);
            }
            else {
                sender.sendMessage(TC_DISABLED);
            }

            return;

        }

        UUIDFetcher.Profile profile = UUIDFetcher.getProfile(args[0]);

        if(profile == null) {
            sender.sendMessage(TC_ERROR_UNKNOWN_PLAYER);
            return;
        }

        plugin.getMaintenanceMode().addPlayer(profile.getID());

        TextComponent tc = new TextComponent("Player ");
        tc.setColor(ChatColor.GREEN);

        TextComponent tc_player = new TextComponent(profile.getName());
        tc_player.setColor(ChatColor.GRAY);

        tc.addExtra(tc_player);
        tc.addExtra(" added to maintenance list.");

        sender.sendMessage(tc);

    }

}
