package com.starquestminecraft.bukkit.space;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class SQSpace extends JavaPlugin implements Listener {

    private final Set<UUID> suffocating_players = new HashSet<>();
    private final Set<UUID> space_players = new HashSet<>();
    private final Set<String> space_worlds = new HashSet<>();

    @Override
    public void onEnable() {

        saveDefaultConfig();

        space_worlds.clear();

        for(String world : getConfig().getStringList("systems")) {
            space_worlds.add(world.toLowerCase());
        }

        boolean enabled = false;

        for(World world : getServer().getWorlds()) {
            if(space_worlds.contains(world.getName().toLowerCase())) {
                enabled = true;
            }
        }

        if(enabled) {

            getServer().getPluginManager().registerEvents(this, this);

            getServer().getScheduler().runTaskTimer(this, new SpaceCheckTask(), 10, 10);

            getLogger().info("Events registered!");

        }

    }

    // Adding headshot support
    @EventHandler
    public void arrowDamager(final EntityDamageByEntityEvent event) {

        if(event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }

        if(!(event.getDamager() instanceof Arrow)) {
            return;
        }

        Arrow arrow = (Arrow)event.getDamager();
        Player player = (Player)event.getEntity();
        // Sneaking changes height of the player's head
        Location loc = player.getLocation().add(0, (player.isSneaking() ? 1.46 : 1.62), 0);
        Location arrow_loc = arrow.getLocation();

        if(arrow_loc.getY() < loc.getY()) {
            return;
        }

        ItemStack helmet = player.getInventory().getHelmet();

        if(helmet.getType() != Material.PUMPKIN) {
            return;
        }

        // Replacing their pumpkin with air
        player.getInventory().setHelmet(new ItemStack(Material.AIR));

        Map<Integer,ItemStack> overflow = player.getInventory().addItem(helmet);
        if(!overflow.isEmpty()) {
            for(ItemStack item : overflow.values()) {
                arrow.getWorld().dropItemNaturally(arrow_loc, item);
            }
        }

        if(arrow.getShooter() instanceof Player) {
            ((Player)arrow.getShooter()).sendMessage(ChatColor.RED + "Headshot!");
        }

    }

    boolean canSuffocate(final Player player) {

        if(player.isDead()) {
            return false;
        }

        if((player.getGameMode() == GameMode.CREATIVE) || (player.getGameMode() == GameMode.SPECTATOR)) {
            return false;
        }

        if(player.hasPermission("sqspace.nosuffocate")) {
            return false;
        }

        if(!isInSpace(player)) {
            return false;
        }

        if(!hasSpaceArmor(player)) {
            return true;
        }

        return false;

    }

    boolean doSuffocationCheck(final Player player) {

        // WARNING: This check assumes you have already checked that the player
        // is in a "Space" area.
        // It is only a Respiration / Perm check

        if(!canSuffocate(player)) {
            return false;
        }

        if(!suffocating_players.add(player.getUniqueId())) {

            player.sendMessage(ChatColor.AQUA + "[Space] " + ChatColor.RED + "You are now Suffocating!");

            new SuffocationTask(this, player).runTaskTimer(this, 20, 20);

            return true;

        }

        return false;

    }

    public boolean isSuffocating(final Player player) {
        return suffocating_players.contains(player.getUniqueId());
    }

    public void removeSuffocating(final Player player) {
        suffocating_players.remove(player.getUniqueId());
    }

    public boolean hasSpaceArmor(final Player player) {

        ItemStack[] armor = player.getInventory().getArmorContents();

        if(armor == null) {
            return false;
        }

        for(ItemStack i : armor) {

            if(i == null) {
                return false;
            }

            if(!isArmor(i)) {
                return false;
            }

        }

        return true;

    }

    private boolean isArmor(final ItemStack item) {

        Material type = item.getType();

        switch(type) {

            case CHAINMAIL_BOOTS:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_HELMET:
            case CHAINMAIL_LEGGINGS:
                return true;

        }

        if(!item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        if(!meta.hasLore()) {
            return false;
        }

        return meta.getLore().contains(ChatColor.DARK_PURPLE + "Power Tool");

    }

    private boolean doSpaceCheck(final Entity entity) {

        Location loc = entity.getLocation();
        boolean air1 = true;
        boolean air2 = true;
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        World world = loc.getWorld();
        int height = 40;

        for(int i = 0; i < height; i++) {

            if(world.getBlockAt(x, y + i + 1, z).getType() != Material.AIR) {
                air1 = false;
                if(!air2) {
                    break;
                }
            }

            if(world.getBlockAt(x, y - i, z).getType() != Material.AIR) {
                air2 = false;
                if(!air1) {
                    break;
                }
            }

        }

        if(!air1 && !air2) {
            return false;
        }

        return true;

    }

    public boolean isInSpace(final Player player) {
        return space_players.contains(player.getUniqueId());
    }

    public boolean isSpaceWorld(final String name) {
        return space_worlds.contains(name.toLowerCase());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHelmetChange(final InventoryClickEvent event) {

        if((event.getSlotType() == SlotType.ARMOR) && (event.getSlot() == 103)) {
            doSuffocationCheck((Player)event.getWhoClicked());
        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {

        checkIfInSpace(event.getPlayer(), event.getTo());

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onItemDrop(final ItemSpawnEvent event) {

		String planet = event.getEntity().getWorld().getName().toLowerCase();

		if (isSpaceWorld(planet)) {
			
			event.getEntity().setGravity(false);

		}

	}

    private void checkIfInSpace(final Player player) {
        checkIfInSpace(player, player.getLocation());
    }

    private void checkIfInSpace(final Player player, final Location loc) {

        if(!isSpaceWorld(loc.getWorld().getName())) {
            return;
        }

        if(hasSpaceArmor(player)) {
            player.setRemainingAir(player.getMaximumAir());
        }

        if((player.getGameMode() == GameMode.SURVIVAL) || (player.getGameMode() == GameMode.ADVENTURE)) {

            boolean is_flying = player.isFlying();
            boolean in_space = doSpaceCheck(player);

            if(in_space) {
                space_players.add(player.getUniqueId());
            }
            else {
                space_players.remove(player.getUniqueId());
            }

            if(in_space && !is_flying && (loc.getY() < 256)) {

                player.setAllowFlight(true);
                player.setFlying(true);
                player.setFlySpeed(0.02F);

            }
            else if(is_flying) {

                player.setAllowFlight(false);
                player.setFlying(false);
                player.setFlySpeed(0.1F);
                player.setFallDistance(0.0F);
                player.setSprinting(false);

            }

            if(loc.getY() > 256) {
                player.teleport(loc.add(0, -1, 0));
            }
            else if(loc.getY() < 0) {
                player.teleport(loc.add(0, 1, 0));
            }

        }

        doSuffocationCheck(player);

    }

    private class SpaceCheckTask implements Runnable {

        @Override
        public void run() {

            for(Player player : getServer().getOnlinePlayers()) {
                checkIfInSpace(player);
            }

        }

    }

}
