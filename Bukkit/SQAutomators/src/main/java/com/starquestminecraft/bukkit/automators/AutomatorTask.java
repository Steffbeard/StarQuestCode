package com.starquestminecraft.bukkit.automators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dropper;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.starquestminecraft.sqtechbase.SQTechBase;
import com.starquestminecraft.sqtechbase.objects.Machine;

public class AutomatorTask implements Runnable {

    private final SQAutomators plugin;

    public AutomatorTask(final SQAutomators plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {

        for(Machine machine : SQTechBase.machines) {

            if(!(machine instanceof Automator)) {
                continue;
            }

            Automator automator = (Automator)machine;

            if(automator.getRecipe() == null) {
                continue;
            }

            if(automator.getEnergy() < 50) {
                continue;
            }

            int level = (int)automator.data.get("level");

            Block mainBlock = machine.getGUIBlock().getLocation().getBlock();
            Block middleBlock = mainBlock.getRelative(BlockFace.DOWN);
            Block bottomBlock = middleBlock.getRelative(BlockFace.DOWN);

            int builtLevel = 0;

            boolean north = checkDirection(BlockFace.NORTH, mainBlock, middleBlock, bottomBlock);
            boolean south = checkDirection(BlockFace.SOUTH, mainBlock, middleBlock, bottomBlock);
            boolean east = checkDirection(BlockFace.EAST, mainBlock, middleBlock, bottomBlock);
            boolean west = checkDirection(BlockFace.WEST, mainBlock, middleBlock, bottomBlock);

            if(north) {
                builtLevel = builtLevel + 1;
            }

            if(south) {
                builtLevel = builtLevel + 1;
                if(builtLevel > level) {
                    builtLevel = level;
                    south = false;
                    east = false;
                    west = false;
                }
            }

            if(east) {
                builtLevel = builtLevel + 1;
                if(builtLevel > level) {
                    builtLevel = level;
                    east = false;
                    west = false;
                }
            }

            if(west) {
                builtLevel = builtLevel + 1;
                if(builtLevel > level) {
                    builtLevel = level;
                    west = false;
                }
            }

            List<Inventory> inputInventories = new ArrayList<>();
            List<Inventory> outputInventories = new ArrayList<>();

            if(north) {

                Dropper dropper = (Dropper)mainBlock.getRelative(BlockFace.NORTH).getState();
                inputInventories.add(dropper.getInventory());
                outputInventories.add(((Dropper)mainBlock.getRelative(BlockFace.NORTH).getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getState()).getInventory());

            }

            if(east) {

                Dropper dropper = (Dropper)mainBlock.getRelative(BlockFace.EAST).getState();
                inputInventories.add(dropper.getInventory());
                outputInventories.add(((Dropper)mainBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getState()).getInventory());

            }

            if(south) {

                Dropper dropper = (Dropper)mainBlock.getRelative(BlockFace.SOUTH).getState();
                inputInventories.add(dropper.getInventory());
                outputInventories.add(((Dropper)mainBlock.getRelative(BlockFace.SOUTH).getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getState()).getInventory());

            }

            if(west) {

                Dropper dropper = (Dropper)mainBlock.getRelative(BlockFace.WEST).getState();
                inputInventories.add(dropper.getInventory());
                outputInventories.add(((Dropper)mainBlock.getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getState()).getInventory());

            }

            Recipe recipe = automator.getRecipe();
            Inventory totalInventory = plugin.getServer().createInventory(automator.getOwner(), 54);

            for(int i = 0; i < inputInventories.size(); i++) {

                Inventory inv = inputInventories.get(i);

                for(ItemStack item : inv.getContents()) {
                    if(item != null) {
                        totalInventory.addItem(item.clone());
                    }
                }

            }

            List<ItemStack> ingredientItems = new ArrayList<>();

            if(recipe instanceof ShapedRecipe) {

                for(ItemStack ingredient : ((ShapedRecipe)recipe).getIngredientMap().values()) {

                    if(ingredient != null) {

                        boolean addedAmount = false;

                        for(int i = 0; i < ingredientItems.size(); i++) {

                            ItemStack currentIngredient = ingredientItems.get(i);

                            if(ingredient.getType().equals(currentIngredient.getType())) {
                                addedAmount = true;
                                currentIngredient.setAmount(currentIngredient.getAmount() + ingredient.getAmount());
                            }

                        }

                        if(!addedAmount) {
                            ingredientItems.add(ingredient);
                        }

                    }

                }

            }

            if(recipe instanceof ShapelessRecipe) {
                ingredientItems = ((ShapelessRecipe)recipe).getIngredientList();
            }

            craftRecipe(automator, inputInventories, outputInventories, totalInventory, ingredientItems, recipe);

            if(level == 2) {
                craftRecipe(automator, inputInventories, outputInventories, totalInventory, ingredientItems, recipe);
            }
            else if(level == 3) {
                craftRecipe(automator, inputInventories, outputInventories, totalInventory, ingredientItems, recipe);
                craftRecipe(automator, inputInventories, outputInventories, totalInventory, ingredientItems, recipe);
            }
            else if(level == 4) {
                craftRecipe(automator, inputInventories, outputInventories, totalInventory, ingredientItems, recipe);
                craftRecipe(automator, inputInventories, outputInventories, totalInventory, ingredientItems, recipe);
                craftRecipe(automator, inputInventories, outputInventories, totalInventory, ingredientItems, recipe);
            }

        }

    }

    public void craftRecipe(final Automator automator, final List<Inventory> inventories, final List<Inventory> outputInventories, final Inventory totalInventory, final List<ItemStack> ingredientItems, final Recipe recipe) {

        List<ItemStack> inventoryContents = new ArrayList<>();

        for(ItemStack item : totalInventory) {

            if(item == null) {
                continue;
            }

            boolean addedAmount = false;

            if(!inventoryContents.isEmpty()) {

                int invContentsSize = inventoryContents.size();

                for(int x = 0; x < invContentsSize; x++) {

                    ItemStack i = inventoryContents.get(x);

                    if(i.getType() == item.getType()) {
                        addedAmount = true;
                        i.setAmount(i.getAmount() + item.getAmount());
                    }

                }

            }
            else {
                inventoryContents.add(item);
            }

            if(!addedAmount) {
                inventoryContents.add(item);
            }

        }

        List<ItemStack> itemsLeftOver = new ArrayList<>();

        for(int i = 0; i < ingredientItems.size(); i++) {
            itemsLeftOver.add(ingredientItems.get(i));
        }

        if(inventoryContents.isEmpty()) {
            return;
        }

        for(int x = 0; x < itemsLeftOver.size(); x++) {

            ItemStack ingrItem = itemsLeftOver.get(x);

            if(!totalInventory.contains(ingrItem.getType())) {
                return;
            }

            for(int i = 0; i < inventoryContents.size(); i++) {

                ItemStack item = inventoryContents.get(i);

                if(item.getType().equals(ingrItem.getType())) {

                    if(item.getAmount() < ingrItem.getAmount()) {
                        return;
                    }

                }

            }

        }

        boolean addedItem = false;

        ItemStack producedItem = recipe.getResult();
        int inventory = 0;

        for(Inventory inv : outputInventories) {

            inventory = inventory + 1;

            if(inventory >= 5) {
                continue;
            }

            if(inv.firstEmpty() == -1) {

                if(inv.first(producedItem.getType()) != -1) {

                    Map<Integer, ItemStack> leftOverItems = inv.addItem(producedItem);

                    if(!leftOverItems.isEmpty()) {

                        ItemStack unAddedItem = leftOverItems.get(0);
                        int itemsAdded = producedItem.getAmount() - unAddedItem.getAmount();
                        producedItem.setAmount(producedItem.getAmount() - itemsAdded);

                        if(inventory >= 4) {

                            if(unAddedItem.getAmount() != recipe.getResult().getAmount()) {

                                int amountToRemove = recipe.getResult().getAmount() - unAddedItem.getAmount();

                                unAddedItem.setAmount(amountToRemove);

                                inv.removeItem(unAddedItem);

                                return;

                            }

                        }

                    }

                }
                else {
                    return;
                }

            }
            else {
                inventory = 5;
                inv.addItem(producedItem);
            }

        }

        for(Inventory inv : inventories) {

            for(int i = 0; i < itemsLeftOver.size(); i++) {

                ItemStack ingredient = itemsLeftOver.get(i);

                if(!inv.contains(ingredient.getType())) {
                    continue;
                }

                for(int x = 0; x < inv.getContents().length; x++) {

                    ItemStack item = inv.getItem(x);

                    if(item == null) {
                        continue;
                    }

                    if(!item.getType().equals(ingredient.getType())) {
                        continue;
                    }

                    if(item.getAmount() >= ingredient.getAmount()) {

                        inv.removeItem(ingredient);
                        x = 100;

                    }
                    else {

                        ingredient.setAmount(ingredient.getAmount() - item.getAmount());
                        inv.removeItem(item);

                    }

                }

            }

        }

        automator.setEnergy(automator.getEnergy() - 50);

    }

    public boolean checkDirection(final BlockFace blockFace, final Block mainBlock, final Block middleBlock, final Block bottomBlock) {

        if(!mainBlock.getRelative(blockFace).getType().equals(Material.DROPPER)) {
            return false;
        }

        if(!middleBlock.getRelative(blockFace).getType().equals(Material.WORKBENCH)) {
            return false;
        }

        if(!bottomBlock.getRelative(blockFace).getType().equals(Material.DROPPER)) {
            return false;
        }

        return true;

    }

}
