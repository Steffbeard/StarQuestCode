package com.starquestminecraft.bungeecord.greeter.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.event.EventHandler;

import com.starquestminecraft.bungeecord.SQBungeeListener;
import com.starquestminecraft.bungeecord.greeter.SQGreeter;

public class PingListener extends SQBungeeListener<SQGreeter> {

    private static final String LINE_1 = ChatColor.DARK_BLUE + "=====" + ChatColor.GOLD + "Star" + ChatColor.BLUE + "Quest " + ChatColor.RED + "4.0" + ChatColor.DARK_BLUE + "=====\n";

    public PingListener(final SQGreeter plugin) {
        super(plugin);
    }

    @EventHandler
    public void onServerListPing(final ProxyPingEvent event) {

        String username = plugin.getDatabase().getUsername(event.getConnection().getAddress().getAddress().getHostAddress());
        String line2;

        if(username != null) {
            line2 = ChatColor.WHITE + "Welcome back, " + ChatColor.GOLD + username + ChatColor.WHITE + "!";
        }
        else {
            line2 = ChatColor.WHITE + "Welcome to StarQuest!";
        }

        event.getResponse().setDescription(LINE_1 + line2);

    }

}
