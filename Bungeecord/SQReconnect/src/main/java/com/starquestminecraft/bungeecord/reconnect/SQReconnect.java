package com.starquestminecraft.bungeecord.reconnect;

import java.util.HashSet;
import java.util.Set;

import com.starquestminecraft.bungeecord.SQBungeePlugin;

public class SQReconnect extends SQBungeePlugin {

    private final Database database;
	private final Set<String> main_servers;

    public SQReconnect() {

        this.database = new Database(this);
        this.main_servers = new HashSet<>();

    }

    @Override
	protected void enable() throws Exception {

        loadSettings();

        database.setUp();

		getProxy().setReconnectHandler(new SQLReconnectHandler(this, getProxy().getReconnectHandler()));

	}

    public boolean isSQServer(final String server) {
		return main_servers.contains(server);
	}

    Database getDatabase() {
        return database;
    }

	private void loadSettings() {

        main_servers.clear();

        main_servers.addAll(getConfig().getStringList("mainServers"));

        logInfo("Loaded " + main_servers.size() + " main servers.");

	}

}
