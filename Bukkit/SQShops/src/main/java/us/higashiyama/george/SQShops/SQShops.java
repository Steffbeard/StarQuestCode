
package us.higashiyama.george.SQShops;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.starquestminecraft.bukkit.StarQuest;

public class SQShops extends JavaPlugin implements Listener {

	public static SQShops instance;
	public static HashMap<ItemStack, Double> itemIndex = new HashMap<ItemStack, Double>();
	public static Set<ItemStack> blacklist;
	public static double MULTIPLIER = 1;

	public void onEnable() {

		instance = this;
		saveDefaultConfig();
		Database.setUp();
		LogDatabase.setUp();
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		itemIndex = Database.loadData();
		blacklist = new HashSet<ItemStack>();
		List<String> itemBlackList = getConfig().getStringList("blacklist");
		for (String s : itemBlackList) {
			System.out.println(s);
			ItemStack is = new ItemStack(Material.matchMaterial(s), 1);
			blacklist.add(is);
		}
		MULTIPLIER = instance.getConfig().getInt("multiplier");
		new NotifierTask().runTaskTimer(instance, 12000, 12000);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if ((sender instanceof Player)) {
			if (cmd.getName().equalsIgnoreCase("ecorefresh") && sender.hasPermission("SQShops.refresh")) {
				refresh();
				sender.sendMessage("Economy Multiplier Refreshed");
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("ecomultiplier") && sender.hasPermission("SQShops.multiplier")) {
				MULTIPLIER = Double.parseDouble(args[0]);
				instance.getConfig().set("multiplier", MULTIPLIER);
				saveConfig();
				if (MULTIPLIER != 1) {
					sender.sendMessage("Economy Multiplier set to " + MULTIPLIER + " by " + args[1]);
				}
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("ecoreload") && sender.hasPermission("SQShops.reload")) {
				itemIndex.clear();
				itemIndex = Database.loadData();
				sender.sendMessage(ChatColor.AQUA + "Economy Values Reloaded");
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("value")) {
				ItemStack i = ((Player) sender).getItemInHand();
				ItemStack checkStack = new ItemStack(i);
				checkStack.setAmount(1);
				if (itemIndex.get(checkStack) == null) {
					sender.sendMessage(ChatColor.AQUA + "The item you are holding is not sellable.");
				} else {
					sender.sendMessage(ChatColor.AQUA + "Value of one item you are holding: " + itemIndex.get(checkStack));
				}
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("setvalue") && sender.hasPermission("SQShops.edit")) {
				ItemStack i = ((Player) sender).getItemInHand();
				ItemStack checkStack = new ItemStack(i);
				checkStack.setAmount(1);
				if (itemIndex.get(checkStack) == null) {
					sender.sendMessage(ChatColor.AQUA + "The item you are holding is not sellable.");
					return false;
				}
				if (args.length == 0) {
					sender.sendMessage(ChatColor.AQUA + "/setvalue <price> | Sets the value of the item in hand");
					return false;
				}
				Database.updateMaterial(checkStack, Double.parseDouble(args[0]));
				sender.sendMessage(ChatColor.AQUA + "Price set to: " + args[0]);
				return true;
			}

		}

		return true;
	}

	@EventHandler
	public void login(PlayerLoginEvent e) {

		if (MULTIPLIER != 1) {
			e.getPlayer().sendMessage(ChatColor.GOLD + "Hey " + e.getPlayer().getName() + "! There's a x" + MULTIPLIER + " multiplier on all sales at spawn!");
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e) {

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK
				&& (((e.getClickedBlock().getType() == Material.SIGN) || (e.getClickedBlock().getType() == Material.WALL_SIGN) || (e.getClickedBlock()
						.getType() == Material.SIGN_POST))) && (((Sign) e.getClickedBlock().getState()).getLine(0).equals("[CashRegister]"))
				&& e.getPlayer().hasPermission("SQShop.create")) {
			Sign s = (Sign) e.getClickedBlock().getState();
			s.setLine(0, ChatColor.AQUA + "Cash Register");
			s.update(true);
		}

		if (e.getAction() == Action.LEFT_CLICK_BLOCK
				&& (((e.getClickedBlock().getType() == Material.SIGN) || (e.getClickedBlock().getType() == Material.WALL_SIGN) || (e.getClickedBlock()
						.getType() == Material.SIGN_POST))) && (((Sign) e.getClickedBlock().getState()).getLine(0).equals(ChatColor.AQUA + "Cash Register"))) {
			Sign s = (Sign) e.getClickedBlock().getState();
			s.setLine(0, ChatColor.AQUA + "Cash Register");
			s.setLine(1, ChatColor.GREEN + "" + getWorth(s));
			s.setLine(2, ChatColor.AQUA + "Left Click to");
			s.setLine(3, ChatColor.AQUA + "Update Total");
			s.update(true);
		}

		if (e.getAction() == Action.RIGHT_CLICK_BLOCK
				&& (((e.getClickedBlock().getType() == Material.SIGN) || (e.getClickedBlock().getType() == Material.WALL_SIGN) || (e.getClickedBlock()
						.getType() == Material.SIGN_POST))) && (((Sign) e.getClickedBlock().getState()).getLine(0).equals(ChatColor.AQUA + "Cash Register"))) {

			sellItems(e.getPlayer(), (Sign) e.getClickedBlock().getState());
			Sign s = (Sign) e.getClickedBlock().getState();
			s.setLine(0, ChatColor.AQUA + "Cash Register");
			s.setLine(1, ChatColor.GREEN + "0.0");
			s.setLine(2, ChatColor.AQUA + "Left Click to");
			s.setLine(3, ChatColor.AQUA + "Update Total");
			s.update(true);

		}

	}

	private double getWorth(Sign s) {

		double total = 0;
		Location l = new Location(s.getWorld(), s.getLocation().getX(), (s.getLocation().getY() - 1), s.getLocation().getZ());
		Chest c = (Chest) l.getBlock().getState();
		Inventory i = c.getInventory();

		for (ItemStack finalStack : i.getContents()) {
			if (finalStack == null)
				continue;

			if (inBlacklist(finalStack.getType())) {
				continue;
			}
			double quantity = finalStack.getAmount();
			ItemStack checkStack = new ItemStack(finalStack);
			checkStack.setAmount(1);
			if (itemIndex.get(checkStack) == null)
				continue;
			double price = itemIndex.get(checkStack);
			total = total + (price * quantity);
		}
		return roundTwoDecimals(total);

	}

	public static boolean inBlacklist(Material m) {

		for (ItemStack is : blacklist) {
			if (is.getType() == m) {
				return true;
			}
		}

		return false;
	}

	private void sellItems(Player player, Sign s) {

		ArrayList<ItemStack> leftovers = new ArrayList<ItemStack>();
		double total = 0;
		Location l = new Location(player.getWorld(), s.getLocation().getX(), (s.getLocation().getY() - 1), s.getLocation().getZ());
		Chest c = (Chest) l.getBlock().getState();
		Inventory i = c.getInventory();

		for (ItemStack finalStack : i.getContents()) {
			if (finalStack == null)
				continue;

			if (inBlacklist(finalStack.getType())) {
				leftovers.add(finalStack);
				continue;
			}

			double quantity = finalStack.getAmount();
			ItemStack checkStack = new ItemStack(finalStack);
			checkStack.setAmount(1);
			if (itemIndex.get(checkStack) == null) {
				leftovers.add(finalStack);
				continue;
			}
			double price = itemIndex.get(checkStack);
			Database.updateStats(finalStack, quantity);
			LogDatabase.addPurchase(finalStack, quantity, (price * quantity), player.getName());
			total = total + (price * quantity);
		}
		if (total == 0)
			return;

		StarQuest.getEconomy().depositPlayer(player.getName(), total * MULTIPLIER);
		if (MULTIPLIER == 1) {
			player.sendMessage(ChatColor.AQUA + "You earned " + roundTwoDecimals(total) + " from selling items.");
		} else {
			player.sendMessage(ChatColor.AQUA + "You earned " + roundTwoDecimals(total) + " from selling items with an active economy booster!");
		}

		i.clear();
		for (ItemStack leftover : leftovers) {
			i.addItem(leftover);
			player.sendMessage(ChatColor.AQUA + "Some items could not be sold. Check your chest to recover un-sellable items.");
		}

	}

	double roundTwoDecimals(double d) {

		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));
	}

	public static void refresh() {

		MULTIPLIER = instance.getConfig().getInt("multiplier");
	}

}
