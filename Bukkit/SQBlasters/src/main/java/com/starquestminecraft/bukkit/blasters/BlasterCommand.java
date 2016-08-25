package com.starquestminecraft.bukkit.blasters;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlasterCommand implements CommandExecutor {

    private final SQBlasters plugin;

    public BlasterCommand(final SQBlasters plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {

        Player player = (Player)sender;

        if(args.length == 0) {
            cmdHelp(player);
            return true;
        }

        if(args.length > 1) {
            sender.sendMessage(ChatColor.RED + "Use /blaster help for help on how to use SQBlasters");
            return true;
        }

        switch(args[0].toLowerCase()) {

            case "?":
            case "help":
                cmdHelp(player);
                break;

            case "create":
                cmdCreate(player);
                break;

            case "guide":
                cmdGuide(player);
                break;

            case "recipe":
                plugin.showBlasterRecipe(player);
                break;

            case "spawn":
                cmdSpawn(player);
                break;

            default:
                sender.sendMessage(ChatColor.RED + "Use /blaster help for help on how to use SQBlasters");
                break;

        }

        return true;

    }

    private void cmdCreate(final Player player) {

        ItemStack item = player.getInventory().getItemInMainHand();

        if(!plugin.isNewBlaster(item)) {
            player.sendMessage(ChatColor.RED + "You must be holding a blaster with the type of New Blaster to create a new blaster");
            return;
        }

        plugin.cacheNewBlasterItem(player.getUniqueId(), item);

        //player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        player.getInventory().setItemInMainHand(null);

        plugin.showBlasterSelection(player);

    }

    private void cmdGuide(final Player player) {

        player.sendMessage(ChatColor.GOLD + "-----------------------------------------------------");
        player.sendMessage(ChatColor.BLUE + "To aquire a blaster, the first step that you need to follow is to craft a blaster with the recipe that you can find with the command /blaster recipe");
        player.sendMessage("");
        player.sendMessage(ChatColor.BLUE + "The next step is to type /blaster create and then pick a type of blaster.  Each of the blaster have different stats so pick the one that is best suited for your purpose.");
        player.sendMessage("");
        player.sendMessage(ChatColor.BLUE + "After you select your blaster type, you can fire by right clicking with the blaster in your hand. If the blaster is automatic, you just need to press right click once to toggle on and off automatic shooting. You can left click to zoom in with your blaster.  All of the enchantments except punch work and unbreaking has no use becuase blasters do not loss durability.");
        player.sendMessage(ChatColor.GOLD + "-----------------------------------------------------");

    }

    private void cmdHelp(final Player player) {

        player.sendMessage(ChatColor.GOLD + "-----------------------------------------------------");
        player.sendMessage(ChatColor.GOLD + "/blaster help" + ChatColor.BLUE + " - Shows this");
        player.sendMessage(ChatColor.GOLD + "/blaster create" + ChatColor.BLUE + " - Creates a new blaster ");
        player.sendMessage(ChatColor.GOLD + "/blaster guide" + ChatColor.BLUE + " - Displays a guide for SQBlasters");
        player.sendMessage(ChatColor.GOLD + "/blaster recipe" + ChatColor.BLUE + " - Displays the blaster crafting recipe");
        player.sendMessage(ChatColor.GOLD + "/blaster spawn" + ChatColor.BLUE + " - Spawns in a blaster - Moderator only!");
        player.sendMessage(ChatColor.GOLD + "-----------------------------------------------------");

    }

    private void cmdSpawn(final Player player) {

        if(!player.hasPermission("sqblasters.spawnblaster")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to spawn in a blaster");
            return;
        }

        plugin.showBlasterSelection(player);

    }

    private Inventory createInventory(final Player player, final int blaster_count) {

        int size;

        if(blaster_count <= 9) {
            size = 9;
        }
        else if((blaster_count > 9) && (blaster_count <= 18)) {
            size = 18;
        }
        else {
            size = 27;
        }

        return plugin.getServer().createInventory(player, size, SQBlasters.INVENTORY_TITLE_BLASTER_SELECTION);

    }

}
