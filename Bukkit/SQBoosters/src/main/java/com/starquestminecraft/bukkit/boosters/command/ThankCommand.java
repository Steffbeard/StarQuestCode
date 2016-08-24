package com.starquestminecraft.bukkit.boosters.command;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.starquestminecraft.bukkit.StarQuest;
import com.starquestminecraft.bukkit.boosters.SQBoosters;

public class ThankCommand implements CommandExecutor {

    private final SQBoosters plugin;

    public ThankCommand(final SQBoosters plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {

        if(!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player)sender;

        if(args.length != 2) {
            player.sendMessage(ChatColor.RED + "The correct use is /thank <person> <amount>");
            return true;
        }

        if(!StarQuest.getEconomy().hasAccount(args[0])) {
            player.sendMessage(ChatColor.RED + "That person does not exsist");
            return true;
        }

        int amount;

        try {
            amount = Math.abs(Integer.parseInt(args[1]));
        }
        catch(NumberFormatException ex) {
            player.sendMessage(ChatColor.RED + "You must input a number");
            return true;
        }

        if(!StarQuest.getEconomy().has(player, player.getWorld().getName(), amount)) {
            player.sendMessage(ChatColor.RED + "You do not have enough money");
            return true;
        }

        StarQuest.getEconomy().withdrawPlayer(player, player.getWorld().getName(), amount);
        StarQuest.getEconomy().depositPlayer(args[0], player.getWorld().getName(), amount);

        String currency;
        if(amount != 1) {
            currency = StarQuest.getEconomy().currencyNamePlural();
        }
        else {
            currency = StarQuest.getEconomy().currencyNamePlural();
        }

        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "eb janesudo " + ChatColor.GOLD + player.getName() + " has thanked " + args[0] + " with " + amount + " " + currency);

        return true;

    }

}
