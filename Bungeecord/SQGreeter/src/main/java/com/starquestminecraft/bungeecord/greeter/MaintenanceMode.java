package com.starquestminecraft.bungeecord.greeter;

import java.util.ArrayList;
import java.util.UUID;

import com.starquestminecraft.bungeecord.util.UUIDFetcher;

public class MaintenanceMode {
	
	private static boolean active = false;
	private static ArrayList<UUID> allowedPlayers = new ArrayList<UUID>();

	public static String message;
	public static boolean isEnabled(){
		return active;
	}
	
	public static void toggleEnabled(String m){
		if(m == null || m.equals("")){
			message = "See http://starquestminecraft.com for more information.";
		} else {
			message = m;
		}
		active = !active;
		if(active == false){
			allowedPlayers.clear();
		}
	}
	
	public static boolean addPlayer(String player){
		UUID u = uuidFromUsername(player);
		if(u == null) return false;
		allowedPlayers.add(u);
		return true;
	}
	
	public static boolean isAllowed(UUID u){
		return allowedPlayers.contains(u);
	}
	
	public static UUID uuidFromUsername(String username) {
        UUIDFetcher.Profile profile = UUIDFetcher.getProfile(username);
		UUID u = profile.getID();
		String s2 = profile.getName();
		System.out.println(u);
		System.out.println(s2);
		return u;
	}

}
