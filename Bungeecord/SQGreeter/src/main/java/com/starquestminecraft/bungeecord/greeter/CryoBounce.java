package com.starquestminecraft.bungeecord.greeter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class CryoBounce extends Plugin implements Listener {

    public static void callCryoMessage(final ProxiedPlayer player, final int iteration) {

        Server server = player.getServer();

        if(server != null) {
            sendMessage(player.getName(), server.getInfo());
        }
        else if(iteration < 15) {

            final int itr2 = iteration + 1;

            SQGreeter.getInstance().getProxy().getScheduler().schedule(SQGreeter.getInstance(), new Runnable() {

                @Override
                public void run() {
                    callCryoMessage(player, itr2);
                }

            }, 1, TimeUnit.SECONDS);

        }
        else {
            player.sendMessage(createMessage("Took more than 15 seconds to connect to server, active cryopod message failed!"));
        }

    }

    public static void fakeCryopodLogin(final ProxiedPlayer player, final ServerInfo target) {
        sendMessage(player.getName(), target);
    }

    public static void sendMessage(final String message, final ServerInfo server) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try(DataOutputStream out = new DataOutputStream(stream)) {
            out.writeUTF(message);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        server.sendData("cryoBounce", stream.toByteArray());

    }

    private static BaseComponent[] createMessage(final String str) {
        return new ComponentBuilder(str).create();
    }

}
