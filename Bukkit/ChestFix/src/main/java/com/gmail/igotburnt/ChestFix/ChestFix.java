package com.gmail.igotburnt.ChestFix;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

public class ChestFix extends org.bukkit.plugin.java.JavaPlugin
{
	public Logger log;

	private File configFile;

	private YamlConfiguration config;
	private boolean lenient = false;

	private HashSet<Material> transparent = new HashSet<Material>(55);
	private HashSet<Material> interact = new HashSet<Material>(30);
	private HashSet<Material> rightClickOnly = new HashSet<Material>(30);

	private ContainerListener containerListener = new ContainerListener(this);
	
	private String pluginName = null;

	public void onEnable()
	{
		this.log = getLogger();

		this.pluginName = "[" + this.getDescription().getName() +"]";
		
		Bukkit.getServer().getPluginManager().registerEvents(this.containerListener, this);

		this.log.info(getDescription().getName() + getDescription().getVersion() + " Enabled ");

		this.configFile = new File(getDataFolder(), "config.yml");
		this.log.info("Loading Config");

		if ((this.configFile == null) || (!this.configFile.exists()))
		{
			this.log.info("Creating config");
			saveDefaultConfig();
			getConfig().options().copyDefaults(true);
		}

		this.config = YamlConfiguration.loadConfiguration(this.configFile);
		this.lenient = this.config.getBoolean("lenient");

		this.log.info("Loading Transparent Blocks...");
		loadTransparentBlocks();
		this.log.info("Loading Interactable Blocks...");
		loadInteractBlocks();
		this.log.info("Loading right-click-only blocks...");
		loadRightClick();
	}

	public void onDisable()
	{
		this.log.info(getDescription().getName() + " disabled");
	}

	public boolean isLenient()
	{
		return this.lenient;
	}

	public HashSet<Material> getTransparentBlocks()
	{
		return this.transparent;
	}

	public void loadRightClick()
	{
		this.rightClickOnly.clear();
		this.rightClickOnly.add(Material.ENCHANTMENT_TABLE);
		this.rightClickOnly.add(Material.WORKBENCH);
		this.rightClickOnly.add(Material.CHEST);
		this.rightClickOnly.add(Material.FURNACE);
		this.rightClickOnly.add(Material.DISPENSER);
		this.rightClickOnly.add(Material.JUKEBOX);
	}

	public HashSet<Material> getRightClickOnly()
	{
		return this.rightClickOnly;
	}

	public void loadTransparentBlocks()
	{
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

		List<String> confIds = this.config.getStringList("transparent");
		for (int i = 0; i < confIds.size(); i++)
		{	
			if(confIds.get(i) != null && Material.getMaterial(confIds.get(i)) == null)
			{
				this.getServer().getConsoleSender().sendMessage(ChatColor.RED + this.pluginName + "Config value: \"transparent: " + confIds.get(i) + "\" is not a valid material name.");
			}
			else if(confIds.get(i) != null)
				addTransparentBlock(Material.getMaterial(confIds.get(i)));
		}
	}

	public void loadInteractBlocks()
	{
		this.interact.clear();
		addInteractBlock(Material.CHEST);
		addInteractBlock(Material.FURNACE);
		addInteractBlock(Material.BREWING_STAND);
		addInteractBlock(Material.DISPENSER);
		addInteractBlock(Material.BURNING_FURNACE);
		addInteractBlock(Material.JUKEBOX);
		
		List<String> confIds = this.config.getStringList("interact");
		for (int i = 0; i < confIds.size(); i++)
		{
			if(confIds.get(i) != null && Material.getMaterial(confIds.get(i)) == null)
			{
				this.getServer().getConsoleSender().sendMessage(ChatColor.RED + this.pluginName + "Config value: \"interact: " + confIds.get(i) + "\" is not a valid material name.");
			}
			else if(confIds.get(i) != null)
				addInteractBlock(Material.getMaterial(confIds.get(i)));
		}
	}

	public void addTransparentBlock(Material mat)
	{
		this.transparent.add(mat);
	}

	public void addInteractBlock(Material mat)
	{
		getInteractBlocks().add(mat);
	}

	public HashSet<Material> getInteractBlocks()
	{
		return this.interact;
	}
}