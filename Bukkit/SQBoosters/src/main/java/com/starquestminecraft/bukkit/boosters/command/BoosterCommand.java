package com.starquestminecraft.bukkit.boosters.command;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.starquestminecraft.bukkit.boosters.Booster;
import com.starquestminecraft.bukkit.boosters.SQBoosters;

public class BoosterCommand implements CommandExecutor {

    private final SQBoosters plugin;

    public BoosterCommand(final SQBoosters plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {

        Booster booster;

        try {
            booster = plugin.getBooster(Booster.Type.byName(label));
        }
        catch(IllegalArgumentException ex) {
            sender.sendMessage(ChatColor.RED + "Invalid booster name: '" + label + "'");
            return true;
        }

        if(!booster.isEnabled()) {
            sender.sendMessage(ChatColor.RED + "That booster is not enabled");
            return true;
        }

        if(args.length == 0) {
            cmdHelp(sender, booster, label);
            return true;
        }

        switch(args[0].toLowerCase()) {

            case "?":
            case "help":
                cmdHelp(sender, booster, label);
                return true;

            case "add":
                cmdAdd(sender, booster, label, args);

            case "view":
                cmdView(sender, booster);
                return true;

            default:
                cmdHelp(sender, booster, label);
                return true;
        }

    }

    private void cmdAdd(final CommandSender sender, final Booster booster, final String command, final String[] args) {

        if(!sender.hasPermission("sqboosters.addmultiplier")) {
            sender.sendMessage(ChatColor.RED + "You do not have premission to add to the " + booster.getType().getMultiplierName() + " multiplier");
            return;
        }

        if(args.length <= 2) {
            sender.sendMessage(ChatColor.RED + "Correct usage: /" + command + " add <multiplier> <duration> [purchaser]");
            return;
        }

        int amount;
        int minutes;
        String purchaser = null;

        try {
            amount = Integer.parseInt(args[1]);
        }
        catch(NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "Amount must be a number");
            return;
        }

        try {
            minutes = Integer.parseInt(args[2]);
        }
        catch(NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "Minutes must be a number");
            return;
        }

        if(args.length == 4) {
            purchaser = args[3];
        }

        plugin.getDB().addMultiplier(booster.getType().getConfigKey(), amount, purchaser, minutes);

        sender.sendMessage(ChatColor.GREEN + "The booster may take up to 30 seconds to register across every server");

    }

    private void cmdHelp(final CommandSender sender, final Booster booster, final String command) {

        boolean permission = sender.hasPermission("sqboosters.viewhelp");
        String multiplier_name = booster.getType().getMultiplierName();
        int multiplier = booster.getMultiplier();

        sender.sendMessage(ChatColor.GOLD + "-----------------------------------------------------");
        sender.sendMessage(ChatColor.GOLD + "Current " + multiplier_name + " multiplier: " + ChatColor.BLUE + multiplier);
        sender.sendMessage(ChatColor.GOLD + "/" + command + " help" + ChatColor.BLUE + " - Shows this");
        if(permission) {
            sender.sendMessage(ChatColor.GOLD + "/" + command + " add <multiplier> <minutes> [purchaser]" + ChatColor.BLUE + " - Adds a booster with optional purchaser");
        }
        sender.sendMessage(ChatColor.GOLD + "/" + command + " view" + ChatColor.BLUE + " - Shows a breakdown of the current booster");
        sender.sendMessage(ChatColor.GOLD + "-----------------------------------------------------");

    }

    private void cmdView(final CommandSender sender, final Booster booster) {

        String multiplier_name = booster.getType().getMultiplierName();
        int multiplier = booster.getMultiplier();

        sender.sendMessage(ChatColor.GOLD + "-----------------------------------------------------");
        sender.sendMessage(ChatColor.GOLD + "Current " + multiplier_name + " multiplier: " + ChatColor.BLUE + multiplier);
        sender.sendMessage(ChatColor.BLUE + "multiplier - purchaser - expires in");

        for(Booster b : plugin.getBoosters().values()) {

            String purchaser = b.getPurchaser();
            String remaining = SQBoosters.getTimeLeft(b.getExpireTime());

            if(purchaser == null) {
                purchaser = "Anonymous";
            }

            sender.sendMessage(ChatColor.BLUE + Integer.toString(multiplier) + " - " + purchaser + " - " + remaining);

        }

        sender.sendMessage(ChatColor.GOLD + "-----------------------------------------------------");

    }

}
