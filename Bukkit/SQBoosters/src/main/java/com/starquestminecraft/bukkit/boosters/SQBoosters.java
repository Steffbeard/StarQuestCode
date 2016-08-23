package com.starquestminecraft.bukkit.boosters;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import net.milkbowl.vault.economy.Economy;

import com.starquestminecraft.bukkit.boosters.command.BoosterCommand;
import com.starquestminecraft.bukkit.boosters.command.BoostersCommand;
import com.starquestminecraft.bukkit.boosters.command.ThankCommand;
import com.starquestminecraft.bukkit.boosters.database.SQLDatabase;
import com.starquestminecraft.bukkit.boosters.listener.EntityListener;
import com.starquestminecraft.bukkit.boosters.listener.McMMOListener;
import com.starquestminecraft.bukkit.boosters.listener.PlayerListener;
import com.starquestminecraft.bukkit.boosters.task.DatabaseCheckTask;

public class SQBoosters extends JavaPlugin {

    public static int[] multipliers = new int[] {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
    public static boolean[] enabledBoosters = new boolean[] {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    public static String[] configNames = new String[] {"expbooster", "mobdropbooster", "sheepshearingbooster", "shopbooster", "speedbooster", "mcmmo-miningbooster", "mcmmo-woodcuttingbooster", "mcmmo-herbalismbooster", "mcmmo-fishingbooster", "mcmmo-excavationbooster", "mcmmo-unarmedbooster", "mcmmo-archerybooster", "mcmmo-swordsbooster", "mcmmo-axesbooster", "mcmmo-repairbooster", "mcmmo-acrobaticsbooster", "mcmmo-alchemybooster", "mcmmo-pilotingbooster", "mcmmo-salvagebooster", "mcmmo-smeltingbooster", "mcmmo-tamingbooster"};
    public static String[] permissionName = new String[] {"SQExpBoost", "SQMobDropBoost", "SQSheepShearBoost", "SQShopBooster", "SQSpeedBooster", "SQMMO-MiningBooster", "SQMMO-WoodcuttingBooster", "SQMMO-HerbalismBooster", "SQMMO-FishingBooster", "SQMMO-ExcavationBooster", "SQMMO-UnarmedBooster", "SQMMO-ArcheryBooster", "SQMMO-SwordsBooster", "SQMMO-AxesBooster", "SQMMO-RepairBooster", "SQMMO-AcrobaticsBooster", "SQMMO-AlchemyBooster", "SQMMO-PilotingBooster", "SQMMO-SalvageBooster", "SQMMO-SmeltingBooster", "SQMMO-TamingBooster"};
    public static String[] multiplierName = new String[] {"exp", "mob drop", "sheep shearing", "shop", "speed", "MCMMO-mining", "MCMMO-woodcutting", "MCMMO-herbalism", "MCMMO-fishing", "MCMMO-excavation", "MCMMO-unarmed", "MCMMO-archery", "MCMMO-swords", "MCMMO-axes", "MCMMO-repair", "MCMMO-acrobatics", "MCMMO-alchemy", "MCMMO-piloting", "MCMMO-salvage", "MCMMO-smelting", "MCMMO-taming"};

    public static List<Integer> databaseIDs = new ArrayList<>();
    public static List<String> databaseBoosters = new ArrayList<>();
    public static List<Integer> databaseMultipliers = new ArrayList<>();
    public static List<String> databasePurchasers = new ArrayList<>();
    public static List<Long> databaseExpirationTimes = new ArrayList<>();

    

    private final Map<Booster.Type, Booster> boosters = new EnumMap<>(Booster.Type.class);
    private final Set<EntityType> allowed_drop_entities = EnumSet.noneOf(EntityType.class);

    private BukkitTask task_boostersync;
    private Economy economy;
    private SQLDatabase database;

    @Override
    public void onLoad() {

        this.database = new SQLDatabase(this);

    }

    @Override
    public void onEnable() {

        saveDefaultConfig();

        registerEconomy();

        for(String str : getConfig().getStringList("entities")) {

            EntityType type;

            try {
                type = EntityType.valueOf(str.toUpperCase().replace(' ', '_'));
            }
            catch(Exception ex) {
                getLogger().warning("Invalid entity type: '" + str + "'");
                continue;
            }

            allowed_drop_entities.add(type);

        }

        refreshBoosters();

        scheduleBoosterSyncTask();

        BoosterCommand command = new BoosterCommand(this);
        for(String cmd : getDescription().getCommands().keySet()) {
            getCommand(cmd).setExecutor(command);
        }

        getCommand("boosters").setExecutor(new BoostersCommand(this));
        getCommand("thank").setExecutor(new ThankCommand(this));

        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        getServer().getPluginManager().registerEvents(new McMMOListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        getLogger().info(getDescription().getName() + " has been enabled!");

    }

    @Override
    public void onDisable() {

        getLogger().info(getDescription().getName() + " has been disabled!");

    }

    public SQLDatabase getDB() {
        return database;
    }

    public Economy getEconomy() {
        return economy;
    }

    public Booster getBooster(final Booster.Type type) {

        synchronized(boosters) {
            return boosters.get(type);
        }

    }

    public Map<Booster.Type, Booster> getBoosters() {

        synchronized(boosters) {
            return new EnumMap<>(boosters);
        }

    }

    public boolean isAllowedMobDropType(final EntityType type) {
        return allowed_drop_entities.contains(type);
    }

    public void sendActiveBoosters(final Player player) {

        getServer().getScheduler().runTask(this, new Runnable() {

            @Override
            public void run() {

                synchronized(boosters) {

                    for(Booster booster : boosters.values()) {

                        if(!booster.isActive()) {
                            continue;
                        }

                        player.sendMessage(ChatColor.GOLD + booster.getType().getName() + ChatColor.BLUE + ": The " + booster.getType().getMultiplierName() + " multiplier is currently " + booster.getMultiplier());

                    }

                }

            }

        });

    }

    public double applyExponentialMultiplier(final double multiplier) {

        if(multiplier == 1) {
            return 1;
        }
        else {
            return Math.abs(Math.pow(.5, (multiplier - 1)) - 2);
        }

    }

    public void refreshBoosters() {

        Map<Booster.Type, Booster> newboosters = database.getBoosters();

        synchronized(boosters) {
            boosters.clear();
            boosters.putAll(newboosters);
        }

    }

    private void registerEconomy() {

        RegisteredServiceProvider<Economy> provider = getServer().getServicesManager().getRegistration(Economy.class);

        if(provider != null) {
            economy = provider.getProvider();
        }

        if(economy == null) {
            throw new IllegalStateException("Economy plugin required!");
        }

    }

    private void scheduleBoosterSyncTask() {

        if(task_boostersync != null) {
            task_boostersync.cancel();
        }

        task_boostersync = getServer().getScheduler().runTaskTimerAsynchronously(this, new DatabaseCheckTask(this), 600, 600);

    }

    public static String getTimeLeft(final long timestamp) {

        long diff = ((timestamp / 60000) - (System.currentTimeMillis() / 60000));
        long hours = (diff / 60);
        long minutes = (diff % 60);

        StringBuilder sb = new StringBuilder(24);

        sb.append(hours);
        sb.append(" hour");

        if(hours != 1) {
            sb.append('s');
        }

        if((hours != 0) && (minutes != 0)) {
            sb.append(" and ");
        }

        sb.append(minutes);
        sb.append(" minute");

        if(minutes != 1) {
            sb.append('s');
        }

        return sb.toString();

    }

}
