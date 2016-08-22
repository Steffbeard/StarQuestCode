package com.starquestminecraft.bukkit.powerboost;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.integration.Econ;
import com.starquestminecraft.bukkit.powerboost.boost.FactionPowerBoost;
import com.starquestminecraft.bukkit.powerboost.boost.PersonalPowerBoost;

public class SQPowerBoost extends JavaPlugin {

    private SQLDatabase database;

    @Override
    public void onEnable() {

        database = new SQLDatabase();

        EconomyHandler.setupEconomy();

        if(getServer().getServerName().equals("Trinitos_Alpha")) {
            UpdateTask.schedule(this);
        }

    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        if(!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player)sender;

        if(cmd.getName().equalsIgnoreCase("powerboost")) {
            return cmdPowerBoost(player, args);
        }
        else if(cmd.getName().equalsIgnoreCase("taxes")) {
            return cmdTaxes(player, args);
        }

        return false;

    }

    public boolean cmdPowerBoost(final Player player, final String[] args) {

        if(args.length < 1) {
            player.sendMessage("Powerboosts cost " + EconomyHandler.getCost() + " per power per day.");
            return true;
        }

        String fp = args[0].toLowerCase();

        if(fp.equals("faction")) {

            Faction faction = getFaction(player);

            if(faction == null || faction == FactionColl.get().getNone()) {
                player.sendMessage("You are not in a faction.");
                return true;
            }

            if(args.length < 2) {
                viewFactionBoost(faction, player);
                return true;
            }

            if(!checkFactionPermissions(faction, player)) {
                player.sendMessage("You do not have authority to purchase or cancel boosts for your faction.");
                return true;
            }

            String subcommand = args[1].toLowerCase();

            if(subcommand.equals("purchase")) {

                try {

                    int power = Integer.parseInt(args[2]);

                    purchaseFactionBoost(player, faction, power);

                    return true;

                }
                catch(NumberFormatException ex) {
                    player.sendMessage("Invalid amount of power.");
                    return true;
                }

            }

            if(subcommand.equals("cancel")) {
                cancelFactionBoost(player, faction);
                return true;
            }

        }
        else if(fp.equals("personal")) {

            if(args.length < 2) {
                viewPersonalBoost(player);
                return true;
            }

            String subcommand = args[1].toLowerCase();

            if(subcommand.equals("purchase")) {

                try {
                    int power = Integer.parseInt(args[2]);
                    purchasePersonalBoost(player, power);
                    return true;
                }
                catch(Exception e) {
                    player.sendMessage("Invalid amount of power.");
                    return true;
                }

            }

            if(subcommand.equals("cancel")) {
                cancelPersonalBoost(player);
                return true;
            }

        }

        return false;

    }

    public boolean cmdTaxes(final Player player, final String[] args) {

        Faction faction = getFaction(player);

        if(args.length < 1) {
            displayTaxes(faction, player);
            return true;
        }

        if((args.length == 2) && args[0].equals("set")) {

            if(checkFactionPermissions(faction, player)) {

                try {
                    int i = Integer.parseInt(args[1]);
                    setTaxes(faction, player, i);
                    return true;
                }
                catch(NumberFormatException e) {
                    player.sendMessage("Invalid tax number.");
                    return true;
                }

            }
            else {
                player.sendMessage("You cannot set your faction's taxes.");
                return true;
            }

        }

        return false;

    }

    private void setTaxes(final Faction faction, final Player player, final int rate) {

        database.setTaxesOfFaction(faction, rate);

        player.sendMessage("Your faction's tax rate has been set to " + rate + ". Taxes are collected once per day and put in the faction bank.");

    }

    private void displayTaxes(final Faction faction, final Player player) {

        int taxes = database.getTaxesOfFaction(faction);

        player.sendMessage("Your faction's tax rate is " + taxes + " per day. This is taken from your account once a day and put in the faction bank. If you cannot pay it you will be kicked.");

    }

    private void cancelPersonalBoost(final Player player) {

        database.setBoostOfPlayer(new PersonalPowerBoost(player.getUniqueId(), 0));

        player.sendMessage("Your powerboost has been cancelled. It will be removed the next time costs are collected.");

    }

    private void purchasePersonalBoost(final Player player, final int power) {

        player.sendMessage("Personal boosts are currently disabled.");

        /*
        PersonalPowerboost boost = new PersonalPowerboost(player.getUniqueId(), power);
        Economy eco = EcoHandler.getEconomy();
        EconomyResponse r = eco.withdrawPlayer(player, boost.getBoost() * EcoHandler.getCost());

        if(r.transactionSuccess()) {
            player.sendMessage("Your powerboost has been set and you have been charged for the first day.");
            database.setBoostOfPlayer(boost);
        }
        else {
            player.sendMessage("You cannot afford this powerboost.");
        }
         */
    }

    private void viewPersonalBoost(final Player player) {

        PersonalPowerBoost boost = database.getBoostOfPlayer(player);

        if(boost == null) {
            player.sendMessage("You have no currently active boost.");
        }
        else {
            player.sendMessage("Your currently active boost is " + boost.getAmount() + ".");
        }

    }

    private void cancelFactionBoost(final Player player, final Faction faction) {

        database.setBoostOfFaction(new FactionPowerBoost(faction, 0));

        getServer().dispatchCommand(getServer().getConsoleSender(), "f powerboost f " + faction.getName() + " " + 0);

        player.sendMessage("Your faction powerboost has been cancelled. It will be removed the next time costs are collected.");

    }

    private void purchaseFactionBoost(final Player player, final Faction faction, final int amount) {

        FactionPowerBoost boost = new FactionPowerBoost(faction, amount);

        if(Econ.hasAtLeast(faction, boost.getAmount() * EconomyHandler.getCost(), "to purchase powerboost")) {

            Econ.modifyMoney(faction, -1 * boost.getAmount() * EconomyHandler.getCost(), "purchasing powerboost");

            player.sendMessage("Your faction powerboost has been set and you have been charged for the first day.");

            getServer().dispatchCommand(getServer().getConsoleSender(), "f powerboost f " + faction.getName() + " " + amount);

            database.setBoostOfFaction(boost);

        }
        else {
            player.sendMessage("You cannot afford this powerboost.");
        }

    }

    private boolean checkFactionPermissions(final Faction faction, final Player player) {
        return faction.getLeader().getUuid().equals(player.getUniqueId());
    }

    private void viewFactionBoost(final Faction faction, final Player player) {

        FactionPowerBoost boost = database.getBoostOfFaction(faction);

        if(boost == null) {
            player.sendMessage("You have no currently active boost in your faction.");
        }
        else {
            player.sendMessage("The currently active boost on faction " + faction.getName() + ChatColor.WHITE + " is " + boost.getAmount() + ".");
        }

    }

    public SQLDatabase getDB() {
        return database;
    }

    private Faction getFaction(final Player player) {
        return MPlayer.get(player).getFaction();
    }

    public void janeMessage(final String message) {
        getServer().dispatchCommand(getServer().getConsoleSender(), "eb janesudo " + message);
    }

}
