package com.starquestminecraft.sqtechbase.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import com.starquestminecraft.sqtechbase.SQTechBase;
import com.starquestminecraft.sqtechbase.gui.GUI;

public class Machine {

	int energy;
	
	GUIBlock guiBlock;
	
	MachineType machineType;
	
	public boolean exportsEnergy;
	public boolean importsEnergy;
	
	public HashMap<String, Object> data = new HashMap<String, Object>();
	
	public boolean enabled = true;
	
	public Machine(int energy, GUIBlock guiBlock, MachineType machineType) {
		
		this.energy = energy;
		
		this.guiBlock = guiBlock;
		
		this.machineType = machineType;
		
		List<Machine> removeMachines = new ArrayList<Machine>();
		
		for (Machine machine : SQTechBase.machines) {
			
			if (machine.getGUIBlock() == guiBlock) {
				
				removeMachines.add(machine);
				
			}
			
		}
		
		SQTechBase.machines.removeAll(removeMachines);
		
	}
	
	public GUIBlock getGUIBlock() {
		
		return guiBlock;
		
	}
	
	public GUI getGUI(Player player) {
		
		return machineType.getGUI(player, guiBlock.id);
		
	}
	
	public MachineType getMachineType() {
		
		return machineType;
		
	}
	
	public boolean detectStructure() {
		
		return machineType.detectStructure(guiBlock);
		
	}
	
	public int getEnergy() {
		
		return energy;
		
	}
	
	public void setEnergy(int energy) {
		
		this.energy = energy;
		
	}
	
	
	public void changeMachineType(MachineType newMachineType) {
		
		machineType = newMachineType;
		
	}
	
}