package com.starquestminecraft.bungeecord.greeter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MaintenanceMode {

    private static final String DEFAULT_MESSAGE = "See http://starquestminecraft.com for more information.";

    private final List<UUID> allowed_players = new ArrayList<>();

    private String message;
    private boolean active;

    public boolean isEnabled() {
        return active;
    }

    public String getMessage() {
        return message;
    }

    public void toggleEnabled(final String message) {

        if((message == null) || message.isEmpty()) {
            this.message = DEFAULT_MESSAGE;
        }
        else {
            this.message = message;
        }

        active = !active;

        if(active == false) {
            allowed_players.clear();
        }

    }

    public boolean addPlayer(final UUID profile_id) {
        return allowed_players.add(profile_id);
    }

    public boolean isAllowed(final UUID profile_id) {
        return allowed_players.contains(profile_id);
    }

}
