package com.starquestminecraft.bungeecord.greeter.command;

import com.starquestminecraft.bungeecord.greeter.SQGreeter;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ReloadCommand extends Command {

    public ReloadCommand() {
        super("priorityreload");
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {

        if(sender instanceof ProxiedPlayer) {
            sender.sendMessage(createMessage("This command can only be run from console!"));
            return;
        }

        SQGreeter.getInstance().loadSettings();

        sender.sendMessage(createMessage("Settings reloaded."));

    }

    private static BaseComponent[] createMessage(String s) {
        return new ComponentBuilder(s).create();
    }

}
