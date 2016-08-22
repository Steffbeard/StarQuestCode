package com.starquestminecraft.bukkit.automators;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.starquestminecraft.sqtechbase.SQTechBase;
import com.starquestminecraft.sqtechbase.gui.GUI;
import com.starquestminecraft.sqtechbase.objects.GUIBlock;
import com.starquestminecraft.sqtechbase.objects.Machine;
import com.starquestminecraft.sqtechbase.objects.MachineType;
import com.starquestminecraft.sqtechbase.util.InventoryUtils;

public class AutoCrafter extends MachineType {

    public static final String INVENTORY_TITLE = ChatColor.GRAY + "AutoCrafter";

    private static final String ITEM_LORE_CONTRABAND = ChatColor.RED + "" + ChatColor.MAGIC + "Contraband";
    private static final String ITEM_NAME_CURRENT_ENERGY = ChatColor.RESET + "Current Energy";

    public AutoCrafter(final int max_energy) {

        super(max_energy);

        this.name = "AutoCrafter";

    }

    @Override
    public boolean detectStructure(final GUIBlock gui_block) {

        Block block = gui_block.getLocation().getBlock();
        Block middle = block.getRelative(BlockFace.DOWN);
        Block bottom = middle.getRelative(BlockFace.DOWN);

        if(!block.getType().equals(Material.LAPIS_BLOCK)) {
            return false;
        }

        if(!middle.getType().equals(Material.SPONGE)) {
            return false;
        }

        if(!bottom.getType().equals(Material.LAPIS_BLOCK)) {
            return false;
        }

        if(checkDirection(BlockFace.NORTH, block, middle, bottom)) {
            return true;
        }

        if(checkDirection(BlockFace.EAST, block, middle, bottom)) {
            return true;
        }

        if(checkDirection(BlockFace.WEST, block, middle, bottom)) {
            return true;
        }

        if(checkDirection(BlockFace.SOUTH, block, middle, bottom)) {
            return true;
        }

        return false;

    }

    @Override
    public GUI getGUI(final Player player, final int id) {
        return new AutomatorGUI(player, id);
    }

    @Override
    public int getSpaceLeft(final Machine machine, final ItemStack itemstack) {
        return 0;
    }

    @Override
    public void sendItems(final Machine machine, final ItemStack itemstack) {

    }

    @Override
    public void updateEnergy(final Machine machine) {

        for(Player player : SQTechBase.currentGui.keySet()) {

            if(SQTechBase.currentGui.get(player).id != machine.getGUIBlock().id) {
                continue;
            }

            if(player.getOpenInventory() == null) {
                continue;
            }

            if(player.getOpenInventory().getTitle().equals(INVENTORY_TITLE)) {
                player.getOpenInventory().setItem(8, createCurrentEnergyItemStack(machine));
            }

        }

    }

    public boolean checkDirection(final BlockFace face, final Block block, final Block middle, final Block bottom) {

        if(!block.getRelative(face).getType().equals(Material.DROPPER)) {
            return false;
        }

        if(!middle.getRelative(face).getType().equals(Material.WORKBENCH)) {
            return false;
        }

        if(!bottom.getRelative(face).getType().equals(Material.DROPPER)) {
            return false;
        }

        return true;

    }

    private ItemStack createCurrentEnergyItemStack(final Machine machine) {

        String lore_energy = ChatColor.RESET + "" + ChatColor.GRAY + machine.getEnergy();

        return InventoryUtils.createSpecialItem(Material.REDSTONE, (short)0, ITEM_NAME_CURRENT_ENERGY, new String[] {ITEM_LORE_CONTRABAND, lore_energy});

    }

}
