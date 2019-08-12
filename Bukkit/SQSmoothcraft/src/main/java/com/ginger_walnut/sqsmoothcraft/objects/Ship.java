package com.ginger_walnut.sqsmoothcraft.objects;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_10_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_10_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_10_R1.PlayerConnection;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import com.starquestminecraft.bukkit.space.SQSpace;
import com.ginger_walnut.sqsmoothcraft.SQSmoothCraft;
import com.ginger_walnut.sqsmoothcraft.enums.BlockType;
import com.ginger_walnut.sqsmoothcraft.gui.MainGui;
import com.ginger_walnut.sqsmoothcraft.tasks.CooldownHandler;
import com.ginger_walnut.sqsmoothcraft.tasks.ProjectileSmoother;
import com.ginger_walnut.sqsmoothcraft.tasks.ShipCreator;

public class Ship {
	
	public Player captain = null;
	
	public List<ShipBlock> blockList = null;
	public List<ShipBlock> cannonList = null;	
	public List<ShipBlock> missleList = null;
	public List<ShipBlock> reactorList = null;
	public List<ShipBlock> emFieldGenList = null;
	
	public double shieldHealth = 0;
	
	public ShipBlock mainBlock = null;
	
	public ShipBlock thirdPersonBlock = null;
	public EntityPlayer thirdPersonPlayer = null;
	
	public float speed = 0.0f;
	public float maxSpeed = 0.0f;
	
	public float playerInput;
	public float lastPlayerInput;
	
	public float acceleration = 0.0f;
	
	public float maxYawRate = 0.0f;
	
	public ShipDirection pointingDirection = null;
	public ShipDirection movingDirection = null;
	
	public ShipDirection lastPointingDirection = null;
	public ShipDirection lastMovingDirection = null;
	
	public Location lastLocation = null;
	
	public Location location = null;	
	
	public boolean lockedDirection = true;
	
	public float fuel = 0;
	public float startingFuel = 0;
	
	public int catalysts = 0;
	
	public BossBar speedBar = null;
	public BossBar fuelBar = null;
	
	public boolean alternatingBlockDirection = false;
	
	public boolean explosiveMode = false;
	
	public Ship (List<ShipBlock> shipBlocks, ShipBlock firstMainBlock, Player firstCaptain, float firstMaxSpeed, float firstMaxYawRate, float maxAcceleration, float firstFuel, int firstCatalysts) {
		
		captain = firstCaptain;
		blockList = shipBlocks;
		
		mainBlock = firstMainBlock;
		
		maxSpeed = firstMaxSpeed;
		maxYawRate = firstMaxYawRate;
		
		location = captain.getLocation();
		
		location.add(0, -1, 0);
		
		lastLocation = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
		
		pointingDirection = new ShipDirection(captain.getLocation().getYaw(), captain.getLocation().getPitch());
		movingDirection = new ShipDirection(captain.getLocation().getYaw(), captain.getLocation().getPitch());
		
		lastPointingDirection = new ShipDirection(captain.getLocation().getYaw(), captain.getLocation().getPitch());
		lastMovingDirection = new ShipDirection(captain.getLocation().getYaw(), captain.getLocation().getPitch());

		acceleration = maxAcceleration;
		
		speedBar = Bukkit.createBossBar("Speed", BarColor.BLUE, BarStyle.SEGMENTED_10);
		speedBar.addPlayer(captain);
		speedBar.setProgress(0.0);
		speedBar.setVisible(true);
		
		fuelBar = Bukkit.createBossBar("Fuel", BarColor.RED, BarStyle.SEGMENTED_10);
		fuelBar.addPlayer(captain);
		fuelBar.setProgress(0.0);
		fuelBar.setVisible(true);
		
		fuel = firstFuel;
		startingFuel = firstFuel;
		
		catalysts = firstCatalysts;
		
		(new ShipCreator()).run(this, captain);
		
	}
	
	public Player getCaptain() {
		
		return captain;
		
	}
	
	public List<ShipBlock> getShipBlocks() {
		
		return blockList;		
		
	}
	
	public void setShipBlocks(List<ShipBlock> shipBlocks) {
		
		blockList = shipBlocks;
		
	}
	
	public List<ShipBlock> getCannons() {
		
		return cannonList;
		
	}
	
	public void setCannons(List<ShipBlock> cannons) {
		
		cannonList = cannons;
		
	}
	
	public ShipBlock getMainBlock() {
		
		return mainBlock;
		
	}
	
	public float getSpeed() {
		
		return speed;
		
	}
	
	public void setSpeed(float newSpeed) {
		
		speed = newSpeed;
		
	}
	
	public float getMaxSpeed() {
		
		return maxSpeed;
		
	}
	
	public void setMaxSpeed(float newMaxSpeed) {
		
		maxSpeed = newMaxSpeed;
		
	}
	
	public float getMaxYawRate() {
		
		return maxYawRate;
		
	}
	
	public void setMaxYawRate(float newMaxYawRate) {
		
		maxYawRate = newMaxYawRate;
		
	}
	
	public Location getLocation() {
		
		return location;
		
	}
	
	public void setLocation(Location newLocation) {
		
		location = newLocation;
		
	}
	
	public float getAcceleration() {
		
		return acceleration;
		
	}
	
	public void setAcceleration(float newAcceleration) {
		
		acceleration = newAcceleration;
		
	}
	
	public void setLastDirections() {
		
		lastPointingDirection.adjustedPitchCos = pointingDirection.adjustedPitchCos;
		lastPointingDirection.adjustedPitchSin = pointingDirection.adjustedPitchSin;
		lastPointingDirection.adjustedYawCos = pointingDirection.adjustedYawCos;
		lastPointingDirection.adjustedYawSin = pointingDirection.adjustedYawSin;
		lastPointingDirection.pitchCos = pointingDirection.pitchCos;
		lastPointingDirection.pitchSin = pointingDirection.pitchSin;
		lastPointingDirection.yawCos = pointingDirection.yawCos;
		lastPointingDirection.yawSin = pointingDirection.yawSin;
		lastPointingDirection.yaw = pointingDirection.yaw;
		lastPointingDirection.pitch = pointingDirection.pitch;	
		
		lastMovingDirection.adjustedPitchCos = movingDirection.adjustedPitchCos;
		lastMovingDirection.adjustedPitchSin = movingDirection.adjustedPitchSin;
		lastMovingDirection.adjustedYawCos = movingDirection.adjustedYawCos;
		lastMovingDirection.adjustedYawSin = movingDirection.adjustedYawSin;
		lastMovingDirection.pitchCos = movingDirection.pitchCos;
		lastMovingDirection.pitchSin = movingDirection.pitchSin;
		lastMovingDirection.yawCos = movingDirection.yawCos;
		lastMovingDirection.yawSin = movingDirection.yawSin;
		lastMovingDirection.yaw = movingDirection.yaw;
		lastMovingDirection.pitch = movingDirection.pitch;
		
	}
	
	public void revetDirections() {
		
		pointingDirection.adjustedPitchCos = lastPointingDirection.adjustedPitchCos;
		pointingDirection.adjustedPitchSin = lastPointingDirection.adjustedPitchSin;
		pointingDirection.adjustedYawCos = lastPointingDirection.adjustedYawCos;
		pointingDirection.adjustedYawSin = lastPointingDirection.adjustedYawSin;
		pointingDirection.pitchCos = lastPointingDirection.pitchCos;
		pointingDirection.pitchSin = lastPointingDirection.pitchSin;
		pointingDirection.yawCos = lastPointingDirection.yawCos;
		pointingDirection.yawSin = lastPointingDirection.yawSin;
		pointingDirection.yaw = lastPointingDirection.yaw;
		pointingDirection.pitch = lastPointingDirection.pitch;	
		
		movingDirection.adjustedPitchCos = lastMovingDirection.adjustedPitchCos;
		movingDirection.adjustedPitchSin = lastMovingDirection.adjustedPitchSin;
		movingDirection.adjustedYawCos = lastMovingDirection.adjustedYawCos;
		movingDirection.adjustedYawSin = lastMovingDirection.adjustedYawSin;
		movingDirection.pitchCos = lastMovingDirection.pitchCos;
		movingDirection.pitchSin = lastMovingDirection.pitchSin;
		movingDirection.yawCos = lastMovingDirection.yawCos;
		movingDirection.yawSin = lastMovingDirection.yawSin;
		movingDirection.yaw = lastMovingDirection.yaw;
		movingDirection.pitch = lastMovingDirection.pitch;
		
	}
	
	public void damage(ShipBlock shipBlock, double damage, boolean carryOver, Location shieldParticleLoc) {
		
		if(shipBlock.ship.shieldHealth <= SQSmoothCraft.config.getInt("utilites.emFieldGenerator.fieldPower") && !(shipBlock.ship.shieldHealth <= 0)) {
			
			shipBlock.ship.shieldHealth = shipBlock.ship.shieldHealth - damage;
			
			if(shieldParticleLoc != null) {

				shieldParticleLoc.getWorld().playEffect(shieldParticleLoc.add(0, 0, 0), Effect.MAGIC_CRIT, 100);
				shieldParticleLoc.getWorld().playEffect(shieldParticleLoc.add(0, 1, 0), Effect.MAGIC_CRIT, 100);
				shieldParticleLoc.getWorld().playEffect(shieldParticleLoc.add(0, -1, 0), Effect.MAGIC_CRIT, 100);
				shieldParticleLoc.getWorld().playEffect(shieldParticleLoc.add(1, 0, 0), Effect.MAGIC_CRIT, 100);
				shieldParticleLoc.getWorld().playEffect(shieldParticleLoc.add(-1, 0, 0), Effect.MAGIC_CRIT, 100);
				shieldParticleLoc.getWorld().playEffect(shieldParticleLoc.add(0, 0, 1), Effect.MAGIC_CRIT, 100);
				shieldParticleLoc.getWorld().playEffect(shieldParticleLoc.add(0, 0, -1), Effect.MAGIC_CRIT, 100);
			//	shieldParticleLoc.getWorld().playSound(shieldParticleLoc, Sound.BLOCK_ANVIL_PLACE, 1.0f, 3.0f);
				shieldParticleLoc.getWorld().playSound(shieldParticleLoc, Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 0.5f);
				
			}
			
			shipBlock.ship.captain.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[EM Field] " + "Field strength at: " + shipBlock.ship.shieldHealth);
			return;
			
		}
		shipBlock.health = shipBlock.health - damage;
		
		if (shipBlock.health <= 0) {
			
			if (shipBlock.stand.getHelmet().getType().equals(Material.getMaterial(SQSmoothCraft.config.getString("weapons.cannon.material")))) {
				
				shipBlock.ship.cannonList.remove(shipBlock);
				
			}
			
			if (shipBlock.stand.getHelmet().getType().equals(Material.DROPPER)) {
				
				shipBlock.ship.reactorList.remove(shipBlock);
				
			}
			
			if (shipBlock.stand.getHelmet().getType().equals(Material.DISPENSER)) {
				
				shipBlock.ship.missleList.remove(shipBlock);
				
			}
			
			if(shipBlock.stand.getHelmet().getType().equals(Material.GOLD_BLOCK)) {
				
				shipBlock.ship.emFieldGenList.remove(shipBlock);
				
			}
			
			shipBlock.ship.blockList.remove(shipBlock);
			
			double averageWeight = 0;
			
			for (ShipBlock block : blockList) {
				
				averageWeight = averageWeight + block.weight;
				
			}
			
			averageWeight = averageWeight / blockList.size();
			
			maxSpeed = (float) (1 / averageWeight) + ((float) reactorList.size() / 10.0f);
			
			if (maxSpeed > 1) {
				
				maxSpeed = 1;
				
			}
			
			maxYawRate = maxSpeed * 5;
			
			acceleration = maxSpeed / 20;
			
			shipBlock.stand.remove();
			shipBlock.stand = null;
			shipBlock = null;
			
		}
		
		if (carryOver) {
			
			if (shipBlock.health <= 0) {
				
				double remainingDamage = shipBlock.health * -1;
				
				List<ShipBlock> surroundingBlocks = new ArrayList<ShipBlock>();
				
				for (ShipBlock block : shipBlock.ship.blockList) {
					
					if (shipBlock.loc.x == block.loc.x + 1 && shipBlock.loc.y == block.loc.y && shipBlock.loc.z == block.loc.z) {
						
						surroundingBlocks.add(block);
						
					} else if (shipBlock.loc.x == block.loc.x - 1 && shipBlock.loc.y == block.loc.y && shipBlock.loc.z == block.loc.z) {
						
						surroundingBlocks.add(block);
						
					} else if (shipBlock.loc.x == block.loc.x && shipBlock.loc.y == block.loc.y + 1 && shipBlock.loc.z == block.loc.z) {
						
						surroundingBlocks.add(block);
						
					} else if (shipBlock.loc.x == block.loc.x && shipBlock.loc.y == block.loc.y - 1 && shipBlock.loc.z == block.loc.z) {
						
						surroundingBlocks.add(block);
						
					} else if (shipBlock.loc.x == block.loc.x && shipBlock.loc.y == block.loc.y && shipBlock.loc.z == block.loc.z + 1) {
						
						surroundingBlocks.add(block);
						
					} else if (shipBlock.loc.x == block.loc.x && shipBlock.loc.y == block.loc.y && shipBlock.loc.z == block.loc.z - 1) {
						
						surroundingBlocks.add(block);
						
					}
					
				}
				
				for (ShipBlock block : surroundingBlocks) {
					
					block.ship.damage(block, remainingDamage / surroundingBlocks.size(), true, null);
					
				}
				
			}
			
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public boolean blockify(boolean remove) {
		
		float yaw = (float) pointingDirection.yaw;
		
		if (yaw < 0) {
			
			yaw = yaw * -1;
			
			yaw = 360 - yaw;
			
		}
		
		List<Block> blocks = new ArrayList<Block>();
		List<Material> materials = new ArrayList<Material>();
		List<Short> durabilitys = new ArrayList<Short>();
		
		if (yaw >= 315 || yaw < 45) {
			
			for (ShipBlock block : blockList) {
				
				Location blockLocation = new Location(location.getWorld(), mainBlock.getLocation().getBlockX() + block.loc.x, mainBlock.getLocation().getBlockY() + block.loc.y, mainBlock.getLocation().getBlockZ() + block.loc.z);
				
				blocks.add(location.getWorld().getBlockAt(blockLocation));		
				materials.add(block.stand.getHelmet().getType());	
				
				if (block.type.equals(BlockType.NORMAL)) {
				
					durabilitys.add(block.stand.getHelmet().getDurability());
				
				} else if (block.type.equals(BlockType.SLAB) || block.type.equals(BlockType.DIRECTIONAL)) {
					
					durabilitys.add((Short) block.data);
					
				}
				
			}
			
		} else if (yaw >= 225 && yaw < 315) {
			
			for (ShipBlock block : blockList) {
				
				Location blockLocation = new Location(location.getWorld(), mainBlock.getLocation().getBlockX() + block.loc.z, mainBlock.getLocation().getBlockY() + block.loc.y, mainBlock.getLocation().getBlockZ() + block.loc.x);
				
				blocks.add(location.getWorld().getBlockAt(blockLocation));		
				materials.add(block.stand.getHelmet().getType());	

				if (block.type.equals(BlockType.NORMAL)) {
					
					durabilitys.add(block.stand.getHelmet().getDurability());
				
				} else if (block.type.equals(BlockType.SLAB) || block.type.equals(BlockType.DIRECTIONAL)) {
					
					durabilitys.add((Short) block.data);
					
				}
				
			}
			
		} else if (yaw >= 135 && yaw < 225) {
			
			for (ShipBlock block : blockList) {
				
				Location blockLocation = new Location(location.getWorld(), mainBlock.getLocation().getBlockX() - block.loc.x, mainBlock.getLocation().getBlockY() + block.loc.y, mainBlock.getLocation().getBlockZ() - block.loc.z);
				
				blocks.add(location.getWorld().getBlockAt(blockLocation));		
				materials.add(block.stand.getHelmet().getType());	

				if (block.type.equals(BlockType.NORMAL)) {
					
					durabilitys.add(block.stand.getHelmet().getDurability());
				
				} else if (block.type.equals(BlockType.SLAB) || block.type.equals(BlockType.DIRECTIONAL)) {
					
					durabilitys.add((Short) block.data);
					
				}
				
			}
			
		} else if (yaw >= 45 && yaw < 135) {
			
			for (ShipBlock block : blockList) {
				
				Location blockLocation = new Location(location.getWorld(), mainBlock.getLocation().getBlockX() - block.loc.z, mainBlock.getLocation().getBlockY() + block.loc.y, mainBlock.getLocation().getBlockZ() - block.loc.x);
				
				blocks.add(location.getWorld().getBlockAt(blockLocation));		
				materials.add(block.stand.getHelmet().getType());	

				if (block.type.equals(BlockType.NORMAL)) {
					
					durabilitys.add(block.stand.getHelmet().getDurability());
				
				} else if (block.type.equals(BlockType.SLAB) || block.type.equals(BlockType.DIRECTIONAL)) {
					
					durabilitys.add((Short) block.data);
					
				}
				
			}
			
		}
		
		boolean canDecompile = true;
		
		for (Block block : blocks) {
			
			if (!block.getType().equals(Material.AIR)) {
				
				canDecompile = false;
				
			}
			
		}
		
		if (canDecompile) {
			
			boolean firstReactor = true;
			
			for (int i = 0; i < blocks.size(); i ++) {
				
				blocks.get(i).setType(materials.get(i));
				blocks.get(i).setData((byte) (int) durabilitys.get(i));
				
				if (materials.get(i).equals(Material.getMaterial(SQSmoothCraft.config.getString("utilites.reactor.material")))) {
					
					Dropper dropper = (Dropper) blocks.get(i).getState();
					
					ItemStack coal = new ItemStack(Material.COAL);
					coal.setAmount((int) ((fuel / reactorList.size()) / SQSmoothCraft.config.getInt("utilites.reactor.fuel per coal")));
					
					if (coal.getAmount() > 0) {
						
						dropper.getInventory().addItem(coal);
						
					}
					
					if (firstReactor) {
						
						firstReactor = false;
						
						ItemStack catalyst = new ItemStack(Material.getMaterial(SQSmoothCraft.config.getString("utilites.reactor.catalyst")));
						
						catalyst.setAmount(catalysts);
						
						if (catalysts > 0) {
							
							dropper.getInventory().addItem(catalyst);
							
						}
						
					}
					
				}
				
			}
			
			for (ShipBlock shipBlock : blockList) {
				
				shipBlock.stand.remove();
				shipBlock.stand = null;
				shipBlock = null;
				
			}
			
			if (remove) {
				
				if (SQSmoothCraft.shipMap.containsKey(captain.getUniqueId())) {
					
					Ship ship = SQSmoothCraft.shipMap.get(captain.getUniqueId());
					ship = null;
					
					SQSmoothCraft.shipMap.remove(captain.getUniqueId());
					
					captain.teleport(location.getWorld().getBlockAt(new Location (location.getWorld(), mainBlock.getLocation().getBlockX() + .5, mainBlock.getLocation().getBlockY(), mainBlock.getLocation().getBlockZ() + .5)).getRelative(0, 1, 0).getLocation());
					
				} else {
					
					SQSmoothCraft.stoppedShipMap.remove(this);
					
				}
				
			}
			
			return true;			
			
		} else {
			
			captain.sendMessage(ChatColor.RED + "Ship cannot undetect becuase of objects in the way");
			
			return false;
			
		}
		
	}
	
	public void exit(boolean remove) {
		
		SQSpace.noSuffacatePlayers.remove(captain);
		
		Ship ship = SQSmoothCraft.shipMap.get(captain.getUniqueId());
		
		if (thirdPersonPlayer != null) {
			
			for (Player onlinePlayer : SQSmoothCraft.getPluginMain().getServer().getOnlinePlayers()) {
				
				PlayerConnection connection = ((CraftPlayer) onlinePlayer).getHandle().playerConnection;
				
				connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, thirdPersonPlayer));
				connection.sendPacket(new PacketPlayOutEntityDestroy(thirdPersonPlayer.getId()));
				
				//			onlinePlayer.showPlayer(event.getPlayer());
				
			}
			
		}
		
		thirdPersonPlayer = null;
		
		if (SQSmoothCraft.shipMap.containsKey(captain.getUniqueId()) && remove) {
			
			SQSmoothCraft.stoppedShipMap.add(ship);
			
			SQSmoothCraft.shipMap.remove(captain.getUniqueId());
			
		}
		
		captain.getInventory().clear();
		captain.getInventory().setArmorContents(null);
		
		speedBar.removePlayer(captain);
		speedBar.setVisible(false);
		
		fuelBar.removePlayer(captain);
		fuelBar.setVisible(false);
		
		SQSmoothCraft.knapsackMap.get(captain.getUniqueId()).unpack(captain);
		
		if (mainBlock != null) {
			
			if (mainBlock.stand != null) {
				
				mainBlock.stand.eject();
				
			}
			
		}

	}
	
	public void fireMissles() {
		
		if (captain.hasMetadata("missile_cooldown")) {
			
			captain.sendMessage(ChatColor.RED + "The missle launchers are on cooldown");
			
		} else {
			
			new CooldownHandler(captain, "missile_cooldown", SQSmoothCraft.config.getInt("weapons.missile launcher.cooldown")).runTaskTimer(SQSmoothCraft.getPluginMain(), 0, 1);
			
			World world = captain.getWorld();
			
			Location captainLocation = captain.getLocation();
			
			if (fuel > 0.0f) {
				
				for (int i = 0; i < missleList.size(); i ++) {
					
					Location blockLocation = missleList.get(i).getLocation();
					Location location = blockLocation.toVector().add(captainLocation.getDirection().multiply(4)).toLocation(world, captainLocation.getYaw(), captainLocation.getPitch());
					
					Fireball fireball = (Fireball) world.spawnEntity(location, EntityType.FIREBALL);
					
					fireball.setYield((float) SQSmoothCraft.config.getDouble("weapons.missile launcher.explosion power"));
					
					Vector newVelocity = captain.getLocation().getDirection();
					
					newVelocity.multiply(4);
					
					fireball.setVelocity(newVelocity);
					
					fireball.setMetadata("damage", new FixedMetadataValue(SQSmoothCraft.getPluginMain(), SQSmoothCraft.config.getInt("weapons.missile launcher.damage")));
					fireball.setMetadata("no_pickup", new FixedMetadataValue(SQSmoothCraft.getPluginMain(), true));
					fireball.setMetadata("carry_over", new FixedMetadataValue(SQSmoothCraft.getPluginMain(), SQSmoothCraft.config.getBoolean("weapons.missile launcher.carry over")));
					
					new ProjectileSmoother(fireball, newVelocity).runTaskTimer(SQSmoothCraft.getPluginMain(), 0, 1);
					
					world.playSound(blockLocation, Sound.ENTITY_GHAST_SHOOT, 2, 1);
					
				}
				
			}
			
		}
		
	}
	
	public void fireCannons() {
		
		if (!explosiveMode) {
			
			if (captain.hasMetadata("cannon_cooldown")) {
				
				captain.sendMessage(ChatColor.RED + "The cannons are on cooldown");
				
			} else {
				
				new CooldownHandler(captain, "cannon_cooldown", SQSmoothCraft.config.getInt("weapons.cannon.cooldown")).runTaskTimer(SQSmoothCraft.getPluginMain(), 0, 1);
				
				World world = captain.getWorld();
				
				Location captainLocation = captain.getLocation();
				
				if (fuel > 0.0f) {
					
					for (int i = 0; i < cannonList.size(); i ++) {
						
						Location blockLocation = cannonList.get(i).getLocation();
						Location location = blockLocation.toVector().add(captainLocation.getDirection().multiply(4)).toLocation(world, captainLocation.getYaw(), captainLocation.getPitch());
						
						Arrow arrow = (Arrow) world.spawnEntity(location, EntityType.ARROW);
						
						Vector newVelocity = captainLocation.getDirection();
						
						newVelocity.multiply(4);
						
						arrow.setVelocity(newVelocity);
						
						arrow.setMetadata("damage", new FixedMetadataValue(SQSmoothCraft.getPluginMain(), SQSmoothCraft.config.getInt("weapons.cannon.damage")));
						arrow.setMetadata("no_pickup", new FixedMetadataValue(SQSmoothCraft.getPluginMain(), true));
						arrow.setMetadata("carry_over", new FixedMetadataValue(SQSmoothCraft.getPluginMain(), SQSmoothCraft.config.getBoolean("weapons.cannon.carry over")));
						
						new ProjectileSmoother(arrow, newVelocity).runTaskTimer(SQSmoothCraft.getPluginMain(), 0, 1);
						
						world.playSound(blockLocation, Sound.ENTITY_ARROW_SHOOT, 2, 1);
						
					}
					
				}
				
			}
			
		} else {
			
			if (captain.hasMetadata("cannon_explosive_cooldown")) {
				
				captain.sendMessage(ChatColor.RED + "The cannons are on cooldown");
				
			} else {
				
				new CooldownHandler(captain, "cannon_explosive_cooldown", SQSmoothCraft.config.getInt("weapons.cannon.explosive mode cooldown")).runTaskTimer(SQSmoothCraft.getPluginMain(), 0, 1);
				
				World world = captain.getWorld();
				
				Location captainLocation = captain.getLocation();
				
				if (fuel > 0.0f) {
					
					for (int i = 0; i < cannonList.size(); i ++) {
						
						Location blockLocation = cannonList.get(i).getLocation();
						Location location = blockLocation.toVector().add(captainLocation.getDirection().multiply(4)).toLocation(world, captainLocation.getYaw(), captainLocation.getPitch());
						
						Arrow arrow = (Arrow) world.spawnEntity(location, EntityType.ARROW);
						
						Vector newVelocity = captainLocation.getDirection();
						
						newVelocity.multiply(4);
						
						arrow.setVelocity(newVelocity);
						
						arrow.setMetadata("damage", new FixedMetadataValue(SQSmoothCraft.getPluginMain(), SQSmoothCraft.config.getInt("weapons.cannon.damage")));
						arrow.setMetadata("no_pickup", new FixedMetadataValue(SQSmoothCraft.getPluginMain(), true));
						arrow.setMetadata("carry_over", new FixedMetadataValue(SQSmoothCraft.getPluginMain(), SQSmoothCraft.config.getBoolean("weapons.cannon.carry over")));
						
						arrow.setMetadata("explosive", new FixedMetadataValue(SQSmoothCraft.getPluginMain(), true));
						
						new ProjectileSmoother(arrow, newVelocity).runTaskTimer(SQSmoothCraft.getPluginMain(), 0, 1);
						
						world.playSound(blockLocation, Sound.ENTITY_ARROW_SHOOT, 2, 1);
						
					}
					
				}
				
			}
			
		}
		
	}	
	
	public void decelerate(float multiplier) {
			
		if (speed <= 0){
				
			speed = speed + (acceleration * multiplier);
			
			if (speed >= 0) {
				
				speed = 0;
				
			}
			
		} else {
				
			speed = speed - (acceleration * multiplier);
			
			if (speed <= 0) {
				
				speed = 0;
				
			}
				
		}

	}
	
	public void toggleLock() {
		
		ItemStack itemInHand = captain.getInventory().getItemInMainHand();
		
		if (itemInHand.getItemMeta().getDisplayName().equals("Direction Locker")) {
			
			if (lockedDirection) {
				
				lockedDirection = false;
				
				captain.sendMessage(ChatColor.GREEN + "The direction has been unlocked");
				
			} else {
				
				lockedDirection = true;
				
				captain.sendMessage(ChatColor.RED + "The direction has been locked");
				
			}
			
		}
		
	}
	
	public void toggleExplosive() {
		
		ItemStack itemInHand = captain.getInventory().getItemInMainHand();
		
		if (itemInHand.getItemMeta().getDisplayName().equals("Cannon Explosive Mode")) {
			
			if (explosiveMode) {
				
				explosiveMode = false;
				
				captain.sendMessage(ChatColor.RED + "Cannon explosive mode has been disabled");
				
			} else {
				
				explosiveMode = true;
				
				captain.sendMessage(ChatColor.GREEN + "Cannon explosive mode has been enabled");
				
			}
			
		}
		
	}
	
	public boolean rightClickControls(Player p) {
		
		ItemStack itemInHand = captain.getInventory().getItemInMainHand();
		
		if (itemInHand.getType().equals(Material.WATCH)) {
			
			fireMissles();

		} else if (itemInHand.getType().equals(Material.COMPASS)) {
			
			toggleLock();
			
		} else if (itemInHand.getType().equals(Material.REDSTONE)) {
			
			MainGui gui = new MainGui(captain);
			gui.open();
			
		} else if (itemInHand.getType().equals(Material.SULPHUR)) {
			
			toggleExplosive();
			
		} else if (itemInHand.getType().equals(Material.STAINED_GLASS)) {

			p.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "[EM Field] " + "Field strength currently at: " + SQSmoothCraft.shipMap.get(p.getUniqueId()).shieldHealth);
			
		} else {

			return true;
			
		}
		
		return false;
		
	}
	
	public void leftClickControls() {
		
		ItemStack itemInHand = captain.getInventory().getItemInMainHand();
		
		if (itemInHand.getType().equals(Material.WATCH)) {
			
			String name = itemInHand.getItemMeta().getDisplayName();
			
			if (name.equals("Main Control Device")) {
				
				fireCannons();
				
			}
			
		}
		
	}
	
}
