package com.starquestminecraft.bukkit.apocalypse;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class RecentKillHandler {

    private static final Set<RecentKill> RECENT_KILLS = new HashSet<>();
    private static final long HOUR = 1000 * 60 * 60;

    public static boolean hasKilledWithinHour(final Player killer, final Player killed) {

        for(RecentKill k : RECENT_KILLS) {

            if(k.killer != killer.getUniqueId()) {
                continue;
            }

            if(k.killed != killed.getUniqueId()) {
                continue;
            }

            long time = k.time;
            long diff = System.currentTimeMillis() - time;

            if(diff > HOUR) {
                continue;
            }

            return true;

        }

        return false;

    }

    public static void addRecentKill(final Player killer, final Player killed) {

        final RecentKill kill = new RecentKill(killer, killed);

        RECENT_KILLS.add(kill);

        Bukkit.getScheduler().scheduleSyncDelayedTask(SQApocalypse.getInstance(), new Runnable() {

            @Override
            public void run() {

                if(RECENT_KILLS.contains(kill)) {

                    RECENT_KILLS.remove(kill);

                    Player p = Bukkit.getPlayer(kill.killer);

                    if(p != null) {
                        OfflinePlayer plrk = Bukkit.getOfflinePlayer(kill.killed);
                        p.sendMessage("It has been an hour since you killed " + plrk.getName() + ", you can now kill them again for points.");
                    }

                }

            }

        }, 20 * 60 * 60);

    }

    private static class RecentKill {

        private final UUID killer;
        private final UUID killed;
        private final long time;

        public RecentKill(final Player killer, final Player killed) {

            this.killer = killer.getUniqueId();
            this.killed = killed.getUniqueId();
            this.time = System.currentTimeMillis();

        }

    }

}
