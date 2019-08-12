package com.starquestminecraft.bukkit.chestfix;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import com.starquestminecraft.bukkit.chestfix.listener.PlayerListener;

public class ChestFix extends JavaPlugin {

    private boolean lenient = false;

    private final Set<Material> transparent = new HashSet<>(55);
    private final Set<Material> interact = new HashSet<>(30);
    private final Set<Material> right_click_only = new HashSet<>(30);

    private final PlayerListener listener = new PlayerListener(this);

    private String message_prefix = null;

    @Override
    public void onEnable() {

        this.message_prefix = "[" + getDescription().getName() + "]";

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(this.listener, this);

        this.lenient = getConfig().getBoolean("lenient");

        getLogger().info("Loading Transparent Blocks...");
        loadTransparentBlocks();

        getLogger().info("Loading Interactable Blocks...");
        loadInteractBlocks();

        getLogger().info("Loading right-click-only blocks...");
        loadRightClick();

    }

    @Override
    public void onDisable() {

    }

    public boolean isLenient() {
        return this.lenient;
    }

    public Set<Material> getTransparentBlocks() {
        return this.transparent;
    }

    public void loadRightClick() {

        this.right_click_only.clear();
        this.right_click_only.add(Material.ENCHANTMENT_TABLE);
        this.right_click_only.add(Material.WORKBENCH);
        this.right_click_only.add(Material.CHEST);
        this.right_click_only.add(Material.FURNACE);
        this.right_click_only.add(Material.DISPENSER);
        this.right_click_only.add(Material.JUKEBOX);

    }

    public Set<Material> getRightClickOnly() {
        return this.right_click_only;
    }

    public void loadTransparentBlocks() {

        this.transparent.clear();

        addTransparentBlock(Material.AIR);

        addTransparentBlock(Material.CAKE_BLOCK);

        addTransparentBlock(Material.REDSTONE);
        addTransparentBlock(Material.REDSTONE_WIRE);

        addTransparentBlock(Material.REDSTONE_TORCH_OFF);
        addTransparentBlock(Material.REDSTONE_TORCH_ON);

        addTransparentBlock(Material.DIODE_BLOCK_OFF);
        addTransparentBlock(Material.DIODE_BLOCK_ON);

        addTransparentBlock(Material.DETECTOR_RAIL);
        addTransparentBlock(Material.LEVER);
        addTransparentBlock(Material.STONE_BUTTON);
        addTransparentBlock(Material.STONE_PLATE);
        addTransparentBlock(Material.WOOD_PLATE);

        addTransparentBlock(Material.RED_MUSHROOM);
        addTransparentBlock(Material.BROWN_MUSHROOM);

        addTransparentBlock(Material.RED_ROSE);
        addTransparentBlock(Material.YELLOW_FLOWER);

        addTransparentBlock(Material.LONG_GRASS);
        addTransparentBlock(Material.VINE);
        addTransparentBlock(Material.WATER_LILY);

        addTransparentBlock(Material.MELON_STEM);
        addTransparentBlock(Material.PUMPKIN_STEM);
        addTransparentBlock(Material.CROPS);
        addTransparentBlock(Material.NETHER_WARTS);

        addTransparentBlock(Material.SNOW);
        addTransparentBlock(Material.FIRE);
        addTransparentBlock(Material.WEB);
        addTransparentBlock(Material.TRIPWIRE);
        addTransparentBlock(Material.TRIPWIRE_HOOK);

        addTransparentBlock(Material.COBBLESTONE_STAIRS);
        addTransparentBlock(Material.BRICK_STAIRS);
        addTransparentBlock(Material.SANDSTONE_STAIRS);
        addTransparentBlock(Material.NETHER_BRICK_STAIRS);
        addTransparentBlock(Material.SMOOTH_STAIRS);

        addTransparentBlock(Material.BIRCH_WOOD_STAIRS);
        addTransparentBlock(Material.WOOD_STAIRS);
        addTransparentBlock(Material.JUNGLE_WOOD_STAIRS);
        addTransparentBlock(Material.SPRUCE_WOOD_STAIRS);

        addTransparentBlock(Material.LAVA);
        addTransparentBlock(Material.STATIONARY_LAVA);
        addTransparentBlock(Material.WATER);
        addTransparentBlock(Material.STATIONARY_WATER);

        addTransparentBlock(Material.SAPLING);
        addTransparentBlock(Material.DEAD_BUSH);

        addTransparentBlock(Material.FENCE);
        addTransparentBlock(Material.FENCE_GATE);
        addTransparentBlock(Material.IRON_FENCE);
        addTransparentBlock(Material.NETHER_FENCE);

        addTransparentBlock(Material.LADDER);
        addTransparentBlock(Material.SIGN);
        addTransparentBlock(Material.SIGN_POST);
        addTransparentBlock(Material.WALL_SIGN);

        addTransparentBlock(Material.BED_BLOCK);
        addTransparentBlock(Material.BED);

        addTransparentBlock(Material.PISTON_EXTENSION);
        addTransparentBlock(Material.PISTON_MOVING_PIECE);
        addTransparentBlock(Material.RAILS);

        addTransparentBlock(Material.TORCH);
        addTransparentBlock(Material.TRAP_DOOR);

        List<String> confIds = getConfig().getStringList("transparent");

        for(String id : confIds) {

            if(id == null) {
                continue;
            }

            Material material = Material.getMaterial(id);

            if(material != null) {
                addTransparentBlock(material);
            }
            else {
                getServer().getConsoleSender().sendMessage(ChatColor.RED + this.message_prefix + "Config value: \"transparent: " + id + "\" is not a valid material name.");
            }

        }

    }

    public void loadInteractBlocks() {

        this.interact.clear();

        addInteractBlock(Material.CHEST);
        addInteractBlock(Material.FURNACE);
        addInteractBlock(Material.BREWING_STAND);
        addInteractBlock(Material.DISPENSER);
        addInteractBlock(Material.BURNING_FURNACE);
        addInteractBlock(Material.JUKEBOX);

        List<String> confIds = getConfig().getStringList("interact");

        for(String id : confIds) {

            if(id == null) {
                continue;
            }

            Material material = Material.getMaterial(id);

            if(material != null) {
                addInteractBlock(material);
            }
            else {
                getServer().getConsoleSender().sendMessage(ChatColor.RED + this.message_prefix + "Config value: \"interact: " + id + "\" is not a valid material name.");
            }

        }

    }

    public void addTransparentBlock(Material mat) {
        this.transparent.add(mat);
    }

    public void addInteractBlock(Material mat) {
        getInteractBlocks().add(mat);
    }

    public Set<Material> getInteractBlocks() {
        return this.interact;
    }

}
