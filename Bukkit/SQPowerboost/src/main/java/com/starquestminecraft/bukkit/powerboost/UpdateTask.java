package com.starquestminecraft.bukkit.powerboost;

import java.util.Calendar;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.integration.Econ;
import com.starquestminecraft.bukkit.StarQuest;
import com.starquestminecraft.bukkit.powerboost.boost.FactionPowerBoost;

public class UpdateTask extends BukkitRunnable {

    private static final boolean COLLECT_COSTS = false;

    private final SQPowerBoost plugin;

    private UpdateTask(final SQPowerBoost plugin) {
        this.plugin = plugin;
    }

    public static void schedule(final SQPowerBoost plugin) {

        UpdateTask task = new UpdateTask(plugin);

        // should be taken at 9:00 PM
        // first try today
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 18);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long millis = cal.getTimeInMillis();

        if(millis < System.currentTimeMillis()) {
            // this time has already passed, schedule for tomorrow
            cal.add(Calendar.DAY_OF_YEAR, 1);
            millis = cal.getTimeInMillis();
        }

        long millisDiff = millis - System.currentTimeMillis();
        int ticksDiff = (int)(millisDiff * 0.02);

        task.runTaskTimerAsynchronously(plugin, ticksDiff, 86400 * 20);

    }

    @Override
    public void run() {

        if(COLLECT_COSTS) {

            SQLDatabase db = plugin.getDB();
            plugin.janeMessage("A new day is here! Powerboost costs and taxes have been collected!");

            for(Faction faction : FactionColl.get().getAll()) {

                int taxes = db.getTaxesOfFaction(faction);
                int total = 0;
                List<MPlayer> players = faction.getMPlayers();

                for(MPlayer mplayer : players) {

                    OfflinePlayer oplayer = plugin.getServer().getOfflinePlayer(mplayer.getUuid());

                    if(oplayer != null) {

                        if(StarQuest.getVaultEconomy().withdrawPlayer(oplayer, taxes).transactionSuccess()) {
                            total += taxes;
                        }
                        else if(!(mplayer.getRole() == Rel.LEADER)) {
                            faction.setInvited(mplayer, false);
                            faction.saveToRemote();
                            mplayer.resetFactionData();
                            mplayer.saveToRemote();
                        }

                    }

                }

                Econ.modifyMoney(faction, total, "daily tax deposit");
                FactionPowerBoost fpb = db.getBoostOfFaction(faction);

                if(fpb == null) {
                    continue;
                }

                int boost = fpb.getAmount();

                if(faction.getPowerBoost() != boost) {
                    faction.setPowerBoost((double)boost);
                }

                if(boost == 0) {

                    if(boost != faction.getPowerBoost()) {
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "f powerboost f " + faction.getName() + " " + boost);
                    }

                    continue;

                }

                if(Econ.hasAtLeast(faction, plugin.getPowerBoostCost() * boost, "powerboost daily charge")) {
                    Econ.modifyMoney(faction, -1 * plugin.getPowerBoostCost() * boost, "powerboost daily charge");
                }
                else {

                    plugin.janeMessage(fpb.getFaction().getName() + " could not afford to maintain their powerboost.");

                    db.setBoostOfFaction(new FactionPowerBoost(fpb.getFaction(), 0));

                    boost = 0;

                }

                if(boost != faction.getPowerBoost()) {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "f powerboost f " + faction.getName() + " " + boost);
                }

            }

        }

    }

}
