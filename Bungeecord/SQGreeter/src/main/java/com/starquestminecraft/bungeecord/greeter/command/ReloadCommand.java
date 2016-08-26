package com.starquestminecraft.bungeecord.greeter.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import com.starquestminecraft.bungeecord.SQBungeeCommand;
import com.starquestminecraft.bungeecord.greeter.SQGreeter;

public class ReloadCommand extends SQBungeeCommand<SQGreeter> {

    private final TextComponent TC_RELOADED = new TextComponent("Settings reloaded.");

    public ReloadCommand(final SQGreeter plugin) {
        super(plugin, "priorityreload");
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {

        if(sender instanceof ProxiedPlayer) {
            sender.sendMessage(TC_MESSAGE_CONSOLE_ONLY);
            return;
        }

        plugin.reloadConfig();

        sender.sendMessage(TC_RELOADED);

    }

}
