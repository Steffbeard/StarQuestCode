package com.starquestminecraft.bukkit.blasters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.starquestminecraft.bukkit.blasters.listener.InventoryListener;
import com.starquestminecraft.bukkit.blasters.listener.PlayerListener;
import com.starquestminecraft.bukkit.blasters.listener.ProjectileListener;
import com.starquestminecraft.bukkit.blasters.task.BlasterBoltCleanupTask;
import com.starquestminecraft.bukkit.blasters.task.RepeatingTask;
import com.starquestminecraft.bukkit.blasters.task.TimedMetadataExpireTask;
import com.starquestminecraft.bukkit.blasters.util.ItemUtil;
import com.starquestminecraft.bukkit.blasters.util.NMSUtil;
import com.starquestminecraft.bukkit.blasters.util.NumberUtil;

public class SQBlasters extends JavaPlugin {

    public static final String INVENTORY_TITLE_BLASTER_RECIPE = ChatColor.GOLD + "Blaster Recipe";
    public static final String INVENTORY_TITLE_BLASTER_SELECTION = ChatColor.GOLD + "Blaster Selection";

    private final BlasterBoltCleanupTask bolt_cleanup;
    private final FixedMetadataValue metadatavalue_true;
    private final Map<String, BlasterBase> blasters;
    private final Map<UUID, ItemStack> new_blaster_cache;
    private final RepeatingTask auto_scope_check;

    private BukkitTask task_boltcleanup;
    private BukkitTask task_repeating;
    private Inventory inventory_recipe;
    private Inventory inventory_selection;

    public SQBlasters() {

        this.auto_scope_check = new RepeatingTask(this);
        this.blasters = new LinkedHashMap<>();
        this.bolt_cleanup = new BlasterBoltCleanupTask(this);
        this.metadatavalue_true = new FixedMetadataValue(this, true);
        this.new_blaster_cache = new HashMap<>();

    }

    @Override
    public void onEnable() {

        saveDefaultConfig();

        loadBlasters();

        registerBlasterRecipe();

        inventory_recipe = createRecipeInventory();
        inventory_selection = createSelectionInventory();

        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileListener(this), this);

        getCommand("blaster").setExecutor(new BlasterCommand(this));

        registerArrowCleanupTask();
        registerRepeatingTask();

        getLogger().info(getDescription().getName() + " has been enabled!");

    }

    @Override
    public void onDisable() {

        getLogger().info(getDescription().getName() + " has been disabled!");

    }

    public void toggleAutomatic(final Player player) {
        auto_scope_check.toggleAutomatic(player);
    }

    public void toggleScope(final Player player, final int scope) {
        auto_scope_check.toggleScope(player, scope);
    }

    public boolean canFireOrReloadBlaster(final Player player) {
        return !(player.hasMetadata("reload_timer") || player.hasMetadata("fire_timer"));
    }

    private void loadBlasters() {

        blasters.clear();

        char[] word_delim = {' ', '-', '_'};

        for(String key : getConfig().getKeys(false)) {

            Blaster.Type type;
            String str_type = getConfig().getString(key + ".type", "STANDARD");

            try {
                type = Blaster.Type.valueOf(str_type.toUpperCase());
            }
            catch(Exception ex) {
                getLogger().warning("Error parsing blaster '" + key + "': Invalid type '" + str_type);
                continue;
            }

            String name = getConfig().getString(key + ".name", WordUtils.capitalize(key, word_delim));
            int mag_size = getConfig().getInt(key + ".ammo per pack");
            double bolt_damage = getConfig().getDouble(key + ".damage");
            int reload_delay = getConfig().getInt(key + ".reload time");
            int fire_delay = getConfig().getInt(key + ".fire time");
            int bolt_lifetime = getConfig().getInt(key + ".arrow life");
            int bolt_fire_ticks = getConfig().getInt(key + ".flame ticks");
            int scope = Math.min(getConfig().getInt(key + ".scope"), 6);

            blasters.put(name.toLowerCase(), new BlasterBase(name, type, mag_size, fire_delay, reload_delay, scope, bolt_damage, bolt_lifetime, bolt_fire_ticks));

        }

        getLogger().info("Blasters loaded: " + blasters.size());

    }

    private void registerBlasterRecipe() {

        ItemStack blaster = new ItemStack(Blaster.MATERIAL);
        ItemMeta meta = blaster.getItemMeta();
        meta.setLore(Arrays.asList(Blaster.LORE_TYPE_NEW));

        blaster.setItemMeta(meta);

        ShapedRecipe recipe = new ShapedRecipe(blaster);

        recipe.shape(" g ", "iir", "ib ");

        recipe.setIngredient('g', Material.GLASS);
        recipe.setIngredient('i', Material.IRON_INGOT);
        recipe.setIngredient('b', Material.STONE_BUTTON);
        recipe.setIngredient('r', Material.EMERALD);

        getServer().addRecipe(recipe);

    }

    public void cacheNewBlasterItem(final UUID profile_id, final ItemStack item) {
        new_blaster_cache.put(profile_id, item);
    }

    public ItemStack getCachedNewBlasterItem(final UUID profile_id) {
        return new_blaster_cache.get(profile_id);
    }

    public void clearCachedNewBlasterItem(final UUID profile_id) {
        new_blaster_cache.remove(profile_id);
    }

    public ItemStack createNewBlaster(final String type, final ItemMeta metaData) {
        return createNewBlaster(getBlasterBase(type), metaData);
    }

    public ItemStack createNewBlaster(final BlasterBase base, final ItemMeta metaData) {

        if(base == null) {
            return null;
        }

        ItemStack item = new ItemStack(Blaster.MATERIAL);
        ItemMeta meta = metaData;

        if(meta == null) {
            meta = item.getItemMeta();
        }

        List<String> lore;

        if(meta.hasLore()) {
            lore = meta.getLore();
        }
        else {
            lore = new ArrayList<>();
        }

        Blaster blaster = new Blaster(base, meta);

        blaster.resetLore(lore);

        meta.setLore(lore);
        meta.setDisplayName(blaster.formatDisplayName());

        item.setItemMeta(meta);

        return item;

    }

    public void useBlaster(final Player player, final Blaster blaster, final ItemStack item, final ItemMeta meta) {

        if(!canFireOrReloadBlaster(player)) {
            return;
        }

        if(blaster.isMagazineEmpty()) {
            reloadBlaster(player, blaster, item, meta);
        }
        else {
            fireBlaster(player, blaster, item, meta);
        }

    }

    private void fireBlaster(final Player player, final Blaster blaster, final ItemStack item, final ItemMeta meta) {

        if(player.hasMetadata("fire_timer")) {
            return;
        }

        launchBlasterBolt(player, blaster);

        blaster.useAmmo(item, meta);

        if(player.getGameMode() != GameMode.CREATIVE) {
            item.setDurability((short)(item.getDurability() + 1));
        }

        player.updateInventory();

        setTimedMetadata(player, "fire_timer", blaster.getMagazineRemaining(), blaster.getMagazineCapacity(), blaster.getFireDelay());

    }

    public BlasterBase getBlasterBase(final String type) {
        return blasters.get(type.toLowerCase());
    }

    public Blaster getBlaster(final ItemStack item) {

        if(!ItemUtil.isTypeWithMeta(item, Blaster.MATERIAL)) {
            return null;
        }

        return getBlaster(item, item.getItemMeta());

    }

    public Blaster getBlaster(final ItemStack item, final ItemMeta meta) {

        if(item.getType() != Blaster.MATERIAL) {
            return null;
        }

        if(!meta.hasLore()) {
            return null;
        }

        List<String> lore = meta.getLore();
        String lore0 = lore.get(0);

        if(!lore0.startsWith(Blaster.LORE_PREFIX_TYPE)) {
            return null;
        }

        BlasterBase base = getBlasterBase(lore0.substring(Blaster.LORE_PREFIX_TYPE.length()).toLowerCase());

        if(base == null) {
            return null;
        }

        int ammo = 0;
        int ammo_max = base.getMagazineCapacity();
        int fire_delay = base.getFireDelay();
        int reload_delay = base.getReloadDelay();
        int scope = base.getScope();
        double bolt_damage = base.getBoltBaseDamage();
        int bolt_lifetime = base.getBoltLifetime();
        int bolt_fire_ticks = base.getBoltFireTicks();
        boolean flaming = Blaster.isFlaming(meta);
        double damage_multiplier = Blaster.calcDamageMultiplier(meta);

        for(String line : lore) {

            if(line.startsWith(Blaster.LORE_PREFIX_AMMO)) {
                String split[] = line.substring(Blaster.LORE_PREFIX_AMMO.length()).split("/");
                ammo = NumberUtil.parseInt(split[0], ammo);
                ammo_max = NumberUtil.parseInt(split[1], ammo_max);
            }
            else if(line.startsWith(Blaster.LORE_PREFIX_DAMAGE)) {
                bolt_damage = NumberUtil.parseDouble(line.substring(Blaster.LORE_PREFIX_DAMAGE.length()), bolt_damage);
            }
            else if(line.startsWith(Blaster.LORE_PREFIX_FIRE_DELAY)) {
                fire_delay = NumberUtil.parseInt(line.substring(Blaster.LORE_PREFIX_FIRE_DELAY.length()), ammo_max);
            }
            else if(line.startsWith(Blaster.LORE_PREFIX_RELOAD_DELAY)) {
                reload_delay = NumberUtil.parseInt(line.substring(Blaster.LORE_PREFIX_RELOAD_DELAY.length()), ammo_max);
            }

        }

        Blaster blaster = new Blaster(base.getName(), base.getType(), ammo, ammo_max, fire_delay, reload_delay, scope, bolt_damage, bolt_lifetime, bolt_fire_ticks, damage_multiplier, flaming);

        return blaster;

    }

    public void reloadBlaster(final Player player, final Blaster blaster, final ItemStack item, final ItemMeta meta) {

        if(player.hasMetadata("reload_timer")) {
            return;
        }

        boolean infinity = item.containsEnchantment(Enchantment.ARROW_INFINITE);

        if(infinity || (player.getGameMode() == GameMode.CREATIVE) || player.getInventory().contains(Material.ARROW)) {

            int amount = blaster.getMagazineCapacity();

            if(infinity || (player.getGameMode() != GameMode.CREATIVE)) {

                ItemStack arrows = new ItemStack(Material.ARROW, 1);

                if(player.getInventory().containsAtLeast(arrows, amount)) {

                    arrows.setAmount(amount);

                    player.getInventory().removeItem(arrows);

                }
                else {

                    arrows.setAmount(amount);

                    Map<Integer,ItemStack> remaining = player.getInventory().removeItem(arrows);

                    for(ItemStack stack : remaining.values()) {
                        amount -= stack.getAmount();
                    }

                }

            }

            blaster.reload(item, meta, amount);

            player.updateInventory();

            //Creating a reload timer for the player
            setTimedMetadata(player, "reload_timer", blaster.getMagazineRemaining(), blaster.getMagazineCapacity(), blaster.getReloadDelay());

        }

    }

    public void showBlasterRecipe(final Player player) {

        player.closeInventory();
        player.openInventory(inventory_recipe);

    }

    public void showBlasterSelection(final Player player) {

        player.closeInventory();
        player.openInventory(inventory_selection);

    }

    public void launchBlasterBolt(final Player player, final Blaster blaster) {

        Arrow arrow = player.launchProjectile(Arrow.class);

        arrow.getWorld().playSound(arrow.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.0f);

        arrow.spigot().setDamage(blaster.getBoltDamage());
        arrow.setGravity(false);

        NMSUtil.setArrowPickup(arrow, false);

        if(blaster.isFlaming()) {
            arrow.setFireTicks(blaster.getBoltFireTicks());
        }

        arrow.setCustomName("Blaster Bolt");

        arrow.setMetadata("blaster_bolt", metadatavalue_true);
        arrow.setMetadata("max_age", new FixedMetadataValue(this, blaster.getBoltLifetime()));

        bolt_cleanup.addArrow(arrow);

    }

    public boolean isAutomaticBlaster(final ItemStack item) {

        if(!ItemUtil.isTypeWithMeta(item, Blaster.MATERIAL)) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        if(!meta.hasLore()) {
            return false;
        }

        return meta.getLore().get(0).equals(Blaster.LORE_PREFIX_TYPE + "Automatic");

    }

    public boolean isNewBlaster(final ItemStack item) {

        if(!ItemUtil.isTypeWithMeta(item, Blaster.MATERIAL)) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        if(!meta.hasLore()) {
            return false;
        }

        return meta.getLore().get(0).equals(Blaster.LORE_TYPE_NEW);

    }

    private Inventory createRecipeInventory() {

        Inventory inventory = getServer().createInventory(null, InventoryType.WORKBENCH, INVENTORY_TITLE_BLASTER_RECIPE);

        ItemStack item = new ItemStack(Blaster.MATERIAL);

        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(Blaster.LORE_TYPE_NEW));
        item.setItemMeta(meta);

        ItemStack glass = new ItemStack(Material.GLASS);
        ItemStack iron = new ItemStack(Material.IRON_INGOT);
        ItemStack emerald = new ItemStack(Material.EMERALD);
        ItemStack button = new ItemStack(Material.STONE_BUTTON);

        inventory.setItem(2, glass);
        inventory.setItem(4, iron);
        inventory.setItem(5, iron);
        inventory.setItem(6, emerald);
        inventory.setItem(7, iron);
        inventory.setItem(8, button);
        inventory.setItem(0, item);

        return inventory;

    }

    private Inventory createSelectionInventory() {

        int size = blasters.size();

        if(size < 9) {
            size = 9;
        }
        else if(size < 18) {
            size = 18;
        }
        else {
            size = 27;
        }

        Inventory inventory = getServer().createInventory(null, size, INVENTORY_TITLE_BLASTER_SELECTION);

        int i = 0;
        for(BlasterBase blaster : blasters.values()) {
            inventory.setItem(i++, createNewBlaster(blaster, null));
        }

        return inventory;

    }

    private void registerArrowCleanupTask() {

        if(task_boltcleanup != null) {
            task_boltcleanup.cancel();
        }

        // this can be async as the only Bukkit/NMS code called is variable accessors
        // *technically* the variables accessed could be stale but it's not critical that they not be
        task_boltcleanup = getServer().getScheduler().runTaskTimerAsynchronously(this, bolt_cleanup, 20, 20);

    }

    private void registerRepeatingTask() {

        if(task_repeating != null) {
            task_repeating.cancel();
        }

        task_repeating = getServer().getScheduler().runTaskTimer(this, auto_scope_check, 0, 1);

    }

    private void setTimedMetadata(final Player player, final String metadata_key, final int ammo, final int ammo_max, final int ticks) {

        //Setting timer Metadata
        player.setMetadata(metadata_key, metadatavalue_true);

        //Scheduling a delayed task
        getServer().getScheduler().scheduleSyncDelayedTask(this, new TimedMetadataExpireTask(this, player, metadata_key, ammo, ammo_max), ticks);

    }

}
