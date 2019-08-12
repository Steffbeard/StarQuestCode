package com.starquestminecraft.bungeecord.greeter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

public class CryoBounce {

    private static final TextComponent TC_CRYO_MESSAGE_FAILED = new TextComponent("Took more than 15 seconds to connect to server, active cryopod message failed!");

    static {

        TC_CRYO_MESSAGE_FAILED.setColor(ChatColor.RED);

    }

    private final SQGreeter plugin;

    public CryoBounce(final SQGreeter plugin) {
        this.plugin = plugin;
    }

    public void callCryoMessage(final ProxiedPlayer player) {
        callCryoMessage(player, 0);
    }

    private void callCryoMessage(final ProxiedPlayer player, final int iteration) {

        Server server = player.getServer();

        if(server != null) {
            sendMessage(player.getName(), server.getInfo());
        }
        else if(iteration < 15) {

            final int itr2 = iteration + 1;

            plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {

                @Override
                public void run() {
                    callCryoMessage(player, itr2);
                }

            }, 1, TimeUnit.SECONDS);

        }
        else {
            player.sendMessage(TC_CRYO_MESSAGE_FAILED);
        }

    }

    public void sendMessage(final String message, final ServerInfo server) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try(DataOutputStream out = new DataOutputStream(stream)) {
            out.writeUTF(message);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        server.sendData("cryoBounce", stream.toByteArray());

    }

}
