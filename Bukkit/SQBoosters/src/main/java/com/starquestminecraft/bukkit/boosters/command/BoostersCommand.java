package com.starquestminecraft.bukkit.boosters.command;

import java.util.Collection;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.starquestminecraft.bukkit.boosters.Booster;
import com.starquestminecraft.bukkit.boosters.SQBoosters;

public class BoostersCommand implements CommandExecutor {

    private final SQBoosters plugin;

    public BoostersCommand(final SQBoosters plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {

        sender.sendMessage(ChatColor.GOLD + "-----------------------------------------------------");

        for(Booster booster : plugin.getBoosters().values()) {

            if(!booster.isEnabled()) {
                continue;
            }

            sender.sendMessage(ChatColor.GOLD + "/" + booster.getType().getConfigKey() + ChatColor.BLUE + " - " + booster.getMultiplier());

        }

        sender.sendMessage(ChatColor.GOLD + "-----------------------------------------------------");

        return true;

    }

}
