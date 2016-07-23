package com.martinjonsson01.sqtechpumps;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.starquestminecraft.sqtechbase.SQTechBase;
import com.starquestminecraft.sqtechbase.objects.Fluid;
import com.starquestminecraft.sqtechbase.objects.Machine;

import net.md_5.bungee.api.ChatColor;


public class Events implements Listener{
	
	@EventHandler
	public void onLiquidFlow(BlockFromToEvent e) {
		
		if (SQTechPumps.waterBlocks != null) {
			
			if (SQTechPumps.waterBlocks.contains(e.getBlock())) {
				
				e.setCancelled(true);
				
			}
			
		}
		
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		
		if (e.getHand() == EquipmentSlot.OFF_HAND) return;
		
		if (e.getClickedBlock().getType() != Material.LAPIS_BLOCK) return;
		
		for (List<Block> list : SQTechPumps.smallTankLapisBlocks.keySet()) {
			
			if (list.contains(e.getClickedBlock())) {
				
				Machine m = SQTechPumps.smallTankLapisBlocks.get(list);
				
				if (m.enabled) {
					//m.getGUI().open(e.getPlayer());
				}
				
			}
			
		}
		
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		
		if (e.getInventory().getName().equals(ChatColor.BLUE + "SQTech - Choose tank type")) {
			
			e.setCancelled(true);
			
			Machine machine = SQTechPumps.inventoryMap.get(e.getInventory());
			
			if (e.getSlot() == 10) {
				//Lava
				for (Fluid f : SQTechBase.fluids) {
					if (f.name.equals("Lava")) {
						machine.maxLiquid.put(f, SQTechPumps.config.getInt("small tank max liquid"));
						e.getWhoClicked().closeInventory();
					}
				}
				
			} else if (e.getSlot() == 16) {
				//Water
				for (Fluid f : SQTechBase.fluids) {
					if (f.name.equals("Water")) {
						machine.maxLiquid.put(f, SQTechPumps.config.getInt("small tank max liquid"));
						e.getWhoClicked().closeInventory();
					}
				}
				
			}
			
		}
		
	}
	
}