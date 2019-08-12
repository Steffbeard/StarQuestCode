package com.starquestminecraft.bungeecord.reconnect;

import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SQLReconnectHandler extends AbstractReconnectHandler {

    private final SQReconnect plugin;
    private final ReconnectHandler fallback;

    public SQLReconnectHandler(final SQReconnect plugin, final ReconnectHandler fallback) {

        this.plugin = plugin;
        this.fallback = fallback;

    }

    @Override
    public void close() {
        fallback.close();
    }

    @Override
    public ServerInfo getStoredServer(final ProxiedPlayer player) {

        // when getting server, we want to return the main server they were on
        // TODO: THIS REALLY NEEDS TO BE CACHED
        String server = plugin.getDatabase().getServer(player, false);

        plugin.logInfo(player.getName() + "'s last server is " + server);

        if(server != null) {
            return plugin.getProxy().getServerInfo(server);
        }

        ServerInfo info = fallback.getServer(player);

        plugin.logInfo("Sending to conventional location: " + info);

        return info;

    }

    @Override
    public void save() {
        fallback.save();
    }

    @Override
    public void setServer(final ProxiedPlayer player) {

        // set the fallback just to make sure.
        fallback.setServer(player);

        // if it's a main server, set main. Else set alt.
        String server = player.getServer().getInfo().getName();
        boolean is_main = plugin.isSQServer(server);

        if(is_main) {
            plugin.logInfo(server + " is a main server.");
        }
        else {
            plugin.logInfo(server + " is not a main server.");
        }

        plugin.getDatabase().updateServer(player, server, is_main);

    }

}
