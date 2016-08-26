package com.starquestminecraft.bungeecord.greeter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.starquestminecraft.bungeecord.util.UUIDFetcher;

public class MaintenanceMode {

    private static final List<UUID> ALLOWED_PLAYERS = new ArrayList<>();

    public static String message;

    private static boolean active;

    public static boolean isEnabled() {
        return active;
    }

    public static void toggleEnabled(final String msg) {

        if((msg == null) || msg.equals("")) {
            message = "See http://starquestminecraft.com for more information.";
        }
        else {
            message = msg;
        }

        active = !active;

        if(active == false) {
            ALLOWED_PLAYERS.clear();
        }

    }

    public static boolean addPlayer(final String player) {

        UUID profile_id = uuidFromUsername(player);

        if(profile_id == null) {
            return false;
        }

        ALLOWED_PLAYERS.add(profile_id);

        return true;

    }

    public static boolean isAllowed(final UUID profile_id) {
        return ALLOWED_PLAYERS.contains(profile_id);
    }

    public static UUID uuidFromUsername(final String username) {

        UUIDFetcher.Profile profile = UUIDFetcher.getProfile(username);
        UUID profile_id = profile.getID();
        String name = profile.getName();

        System.out.println(profile_id);
        System.out.println(name);

        return profile_id;

    }

}
