package com.starquestminecraft.bungeecord.greeter.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import com.starquestminecraft.bungeecord.greeter.MaintenanceMode;

public class MaintenanceCommand extends Command {

    public MaintenanceCommand() {
        super("maintenance");
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {

        if(sender instanceof ProxiedPlayer) {
            sender.sendMessage(createMessage("This command can only be run from console!"));
            return;
        }

        if(args.length >= 1) {

            if(args[0].equals("toggle")) {

                String message = "";

                for(int i = 1; i < args.length; i++) {
                    message = message + " " + args[i];
                }

                MaintenanceMode.toggleEnabled(message);

                if(MaintenanceMode.isEnabled()) {
                    sender.sendMessage(createMessage("Maintenance mode enabled."));
                }
                else {
                    sender.sendMessage(createMessage("Maintenance mode disabled."));
                }

            }
            else {

                MaintenanceMode.addPlayer(args[0]);

                sender.sendMessage(createMessage("Player " + args[0] + " added to maintenance list."));

            }

        }
        else {
            sender.sendMessage(createMessage("maintenance <toggle/player>"));
        }

    }

    private static BaseComponent[] createMessage(String s) {
        return new ComponentBuilder(s).create();
    }

}
