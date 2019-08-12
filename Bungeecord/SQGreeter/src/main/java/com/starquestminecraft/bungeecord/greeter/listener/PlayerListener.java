package com.starquestminecraft.bungeecord.greeter.listener;

import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import com.starquestminecraft.bungeecord.SQBungeeListener;
import com.starquestminecraft.bungeecord.greeter.SQGreeter;

public class PlayerListener extends SQBungeeListener<SQGreeter> {

    public PlayerListener(final SQGreeter plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(final LoginEvent event) {

        if(event.isCancelled()) {
            return;
        }

        if(!plugin.getMaintenanceMode().isEnabled()) {
            return;
        }

        if(!plugin.getMaintenanceMode().isAllowed(event.getConnection().getUniqueId())) {
            event.setCancelled(true);
            event.setCancelReason("StarQuest is in maintenance mode: " + plugin.getMaintenanceMode().getMessage());
        }

    }

    @EventHandler
    public void onPostLogin(final PostLoginEvent event) {

        plugin.getCryoBounce().callCryoMessage(event.getPlayer());

        String ip = event.getPlayer().getAddress().getAddress().getHostAddress();
        String name = event.getPlayer().getName();

        plugin.getDatabase().updateIP(ip, name);

    }

}
