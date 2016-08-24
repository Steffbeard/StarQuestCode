package com.starquestminecraft.bukkit.automators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dropper;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.starquestminecraft.bukkit.StarQuest;
import com.starquestminecraft.sqtechbase.SQTechBase;
import com.starquestminecraft.sqtechbase.gui.GUI;
import com.starquestminecraft.sqtechbase.objects.GUIBlock;
import com.starquestminecraft.sqtechbase.objects.Machine;
import com.starquestminecraft.sqtechbase.util.InventoryUtils;
import com.starquestminecraft.sqtechbase.util.ObjectUtils;

public class AutomatorGUI extends GUI {

    private Automator automator;
    private GUIBlock gui_block;

    public AutomatorGUI(final Player player, final int id) {
        super(player, id);
    }

    @Override
    public void open() {

        Machine machine = ObjectUtils.getMachineFromMachineGUI(this);

        gui_block = machine.getGUIBlock();

        if(machine instanceof Automator) {
            automator = (Automator)machine;
        }
        else {

            SQTechBase.machines.remove(machine);
            Automator a = new Automator(machine.getEnergy(), machine.getGUIBlock(), machine.getMachineType(), SQAutomators.instance.crafterUpgradeCost);

            a.data.put("level", machine.data.get("level"));
            a.setOwner(owner);

            SQTechBase.machines.add(a);
            automator = a;

        }

        int level = (int)automator.data.get("level");
        Inventory gui = Bukkit.createInventory(owner, 27, ChatColor.GRAY + "AutoCrafter");

        if(automator.getScreen() == 0) {

            gui.setItem(0, InventoryUtils.createSpecialItem(Material.WORKBENCH, (short)0, ChatColor.RESET + "AutoCrafter", new String[] {ChatColor.RESET + "" + ChatColor.GRAY + "Automatically crafts items.", ChatColor.RESET + "" + ChatColor.GRAY + "Currently, this autocrafter can", ChatColor.RESET + "" + ChatColor.GRAY + "support up to " + level + " crafting tables.", ChatColor.RED + "" + ChatColor.MAGIC + "Contraband"}));

            if(level < 4) {
                gui.setItem(1, InventoryUtils.createSpecialItem(Material.ANVIL, (short)0, ChatColor.RESET + "Upgrade", new String[] {ChatColor.RESET + "" + ChatColor.GOLD + "Cost: " + automator.getUpgradeCost(), ChatColor.RED + "" + ChatColor.MAGIC + "Contraband"}));
            }

            gui.setItem(2, InventoryUtils.createSpecialItem(Material.IRON_PICKAXE, (short)0, ChatColor.RESET + "Choose Recipe", new String[] {ChatColor.RED + "" + ChatColor.MAGIC + "Contraband"}));

            if(automator.getRecipe() == null) {
                gui.setItem(3, InventoryUtils.createSpecialItem(Material.BARRIER, (short)0, ChatColor.RESET + "Selected Recipe", new String[] {ChatColor.RESET + "No recipe has been selected!", ChatColor.RED + "" + ChatColor.MAGIC + "Contraband"}));
            }
            else {
                gui.setItem(3, InventoryUtils.createSpecialItem(automator.getRecipe().getResult().getType(), (short)0, ChatColor.RESET + "Selected Recipe", new String[] {ChatColor.RESET + automator.getRecipe().getResult().getType().toString(), ChatColor.RED + "" + ChatColor.MAGIC + "Contraband"}));
            }

            gui.setItem(8, InventoryUtils.createSpecialItem(Material.REDSTONE, (short)0, ChatColor.RESET + "Energy", new String[] {ChatColor.RESET + "" + ChatColor.GRAY + automator.getEnergy(), ChatColor.RED + "" + ChatColor.MAGIC + "Contraband"}));
            gui.setItem(26, InventoryUtils.createSpecialItem(Material.WOOD_DOOR, (short)0, ChatColor.RESET + "Back to Main GUI", new String[] {ChatColor.RED + "" + ChatColor.MAGIC + "Contraband"}));

        }
        else if(automator.getScreen() == 1) {

        }

        owner.openInventory(gui);

        if(SQTechBase.currentGui.containsKey(owner)) {

            SQTechBase.currentGui.remove(owner);
            SQTechBase.currentGui.put(owner, this);

        }
        else {

            SQTechBase.currentGui.put(owner, this);

        }

    }

    @Override
    public void click(final InventoryClickEvent event) {

        SQAutomators plugin = SQAutomators.instance;
        int level = (int)automator.data.get("level");

        if((event.getCurrentItem() == null) || (event.getCurrentItem().getType() == Material.AIR)) {
            return;
        }

        if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Upgrade")) {

            if(StarQuest.getEconomy().getBalance(owner) < automator.getUpgradeCost()) {
                return;
            }

            level = level + 1;
            automator.data.put("level", level);
            StarQuest.getEconomy().withdrawPlayer(owner, automator.getUpgradeCost());

            Inventory gui = event.getClickedInventory();

            gui.setItem(0, InventoryUtils.createSpecialItem(Material.WORKBENCH, (short)0, ChatColor.RESET + "AutoCrafter", new String[] {ChatColor.RESET + "" + ChatColor.GRAY + "Automatically crafts items.", ChatColor.RESET + "" + ChatColor.GRAY + "Currently, this autocrafter can", ChatColor.RESET + "" + ChatColor.GRAY + "support up to " + level + " crafting tables.", ChatColor.RED + "" + ChatColor.MAGIC + "Contraband"}));

            if(level >= 4) {
                gui.setItem(1, new ItemStack(Material.AIR));
            }

        }

        if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Back to Main GUI")) {

            close = false;
            automator.getGUIBlock().getGUI(owner).open();
            close = true;

        }

        if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Choose Recipe")) {

            automator.getCurrentRecipes().clear();
            automator.getPageList().clear();
            automator.getOpenRecipes().clear();

            Block mainBlock = automator.getGUIBlock().getLocation().getBlock();
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

            if(north) {

                Dropper dropper = (Dropper)mainBlock.getRelative(BlockFace.NORTH).getState();
                inputInventories.add(dropper.getInventory());

            }

            if(east) {

                Dropper dropper = (Dropper)mainBlock.getRelative(BlockFace.EAST).getState();
                inputInventories.add(dropper.getInventory());

            }

            if(south) {

                Dropper dropper = (Dropper)mainBlock.getRelative(BlockFace.SOUTH).getState();
                inputInventories.add(dropper.getInventory());

            }

            if(west) {

                Dropper dropper = (Dropper)mainBlock.getRelative(BlockFace.WEST).getState();
                inputInventories.add(dropper.getInventory());

            }

            Map<ItemStack, Ingredient> ingredientList = SQAutomators.ingredients;

            /*for(Ingredient i : ingredientList.values()) {

            Bukkit.broadcastMessage(i.item.toString());
            if(i.item != null) {
                Bukkit.broadcastMessage("IngredientItem");
                Bukkit.broadcastMessage(i.item.toString());
                Bukkit.broadcastMessage(i.item.getItemMeta().toString());
                Bukkit.broadcastMessage("");
            }

        }
             */
            for(int i = 0; i < inputInventories.size(); i++) {

                Inventory inventory = inputInventories.get(i);

                for(ItemStack item : inventory.getContents()) {

                    ItemStack newItem = null;

                    if(item != null) {
                        newItem = item.clone();
                        newItem.setAmount(1);
                    }

                    /*
                    Bukkit.broadcastMessage("InvItem");
                        if(newItem != null) {
                        Bukkit.broadcastMessage(newItem.toString());
                        Bukkit.broadcastMessage(newItem.getItemMeta().toString());
                        Bukkit.broadcastMessage("");
                    }
                     */
                    if(ingredientList.containsKey(newItem)) {

                        List<Recipe> recipes = ingredientList.get(newItem).getRecipes();

                        for(int x = 0; x < recipes.size(); x++) {

                            Recipe r = recipes.get(x);

                            //Bukkit.broadcastMessage(r.getResult().toString());
                            //Bukkit.broadcastMessage("");
                            if(!automator.getCurrentRecipes().contains(r)) {
                                automator.getCurrentRecipes().add(recipes.get(x));
                            }

                        }

                    }

                }

            }

            Inventory recipeGUI = Bukkit.createInventory(owner, 27, ChatColor.GRAY + "AutoCrafter");

            recipeGUI.setItem(18, InventoryUtils.createSpecialItem(Material.STAINED_GLASS_PANE, (short)14, ChatColor.RESET + "Back", new String[] {ChatColor.RED + "" + ChatColor.MAGIC + "Contraband"}));
            recipeGUI.setItem(26, InventoryUtils.createSpecialItem(Material.STAINED_GLASS_PANE, (short)5, ChatColor.RESET + "Next Page", new String[] {ChatColor.RED + "" + ChatColor.MAGIC + "Contraband"}));

            if(!automator.getCurrentRecipes().isEmpty()) {

                List<List<Recipe>> pages = new ArrayList<>();

                if(automator.getCurrentRecipes().size() <= 18) {
                    pages.add(automator.getCurrentRecipes());
                }
                else {

                    List<Recipe> newRecipeList = new ArrayList<>();

                    for(int i = 0; automator.getCurrentRecipes().size() > 0; i++) {

                        Recipe r = automator.getCurrentRecipes().get(0);

                        if(newRecipeList.contains(r)) {
                            automator.getCurrentRecipes().remove(r);
                        }
                        else if(newRecipeList.size() < 18) {

                            newRecipeList.add(r);
                            automator.getCurrentRecipes().remove(r);

                        }
                        else {

                            pages.add(new ArrayList<>(newRecipeList));

                            newRecipeList.clear();
                            newRecipeList.add(r);

                        }

                    }

                    if(!newRecipeList.isEmpty()) {
                        pages.add(newRecipeList);
                    }

                }

                automator.setPage(0);
                automator.setPageList(pages);

                openUpgradePage(recipeGUI);

            }
            else {

                recipeGUI.setItem(0, InventoryUtils.createSpecialItem(Material.BARRIER, (short)14, ChatColor.RESET + "No Recipes", new String[] {ChatColor.RESET + "Add items to the input", ChatColor.RESET + "droppers to access recipes.", ChatColor.RED + "" + ChatColor.MAGIC + "Contraband"}));

            }

            close = false;
            owner.openInventory(recipeGUI);
            close = true;

        }

        if(event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null && event.getCurrentItem().getItemMeta().getDisplayName() != null) {

            if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Next Page")) {

                Inventory recipeGUI = Bukkit.createInventory(owner, 27, ChatColor.GRAY + "AutoCrafter");

                recipeGUI.setItem(18, InventoryUtils.createSpecialItem(Material.STAINED_GLASS_PANE, (short)14, ChatColor.RESET + "Back", new String[] {ChatColor.RED + "" + ChatColor.MAGIC + "Contraband"}));
                recipeGUI.setItem(26, InventoryUtils.createSpecialItem(Material.STAINED_GLASS_PANE, (short)5, ChatColor.RESET + "Next Page", new String[] {ChatColor.RED + "" + ChatColor.MAGIC + "Contraband"}));

                automator.nextPage();

                openUpgradePage(recipeGUI);

                close = false;
                owner.openInventory(recipeGUI);
                close = true;

            }
            else if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Back")) {

                if(automator.getPage() > 0) {
                    Inventory recipeGUI = Bukkit.createInventory(owner, 27, ChatColor.GRAY + "AutoCrafter");

                    recipeGUI.setItem(18, InventoryUtils.createSpecialItem(Material.STAINED_GLASS_PANE, (short)14, ChatColor.RESET + "Back", new String[] {ChatColor.RED + "" + ChatColor.MAGIC + "Contraband"}));
                    recipeGUI.setItem(26, InventoryUtils.createSpecialItem(Material.STAINED_GLASS_PANE, (short)5, ChatColor.RESET + "Next Page", new String[] {ChatColor.RED + "" + ChatColor.MAGIC + "Contraband"}));

                    automator.previousPage();

                    openUpgradePage(recipeGUI);

                    close = false;
                    owner.openInventory(recipeGUI);
                    close = true;

                }
                else {
                    open();
                }
            }
            else if(event.getCurrentItem().getItemMeta().getDisplayName().contains("Choose This Recipe")) {

                automator.setRecipe(automator.getOpenRecipes().get(event.getSlot()));

            }

        }

    }

    public void openUpgradePage(Inventory currentGUI) {

        if(automator.getPage() < automator.getPageList().size()) {

            List<Recipe> recipeList = automator.getPageList().get(automator.getPage());

            for(int i = 0; i < recipeList.size(); i++) {

                Recipe recipe = recipeList.get(i);
                automator.setOpenRecipes(recipeList);

                ItemStack displayItem = recipe.getResult();
                ItemMeta meta = displayItem.getItemMeta();

                String itemName = displayItem.getType().toString();
                List<String> lore = new ArrayList<>();

                if(meta.hasLore()) {
                    lore = meta.getLore();
                }

                lore.add(ChatColor.RED + "" + ChatColor.MAGIC + "Contraband");

                meta.setDisplayName(ChatColor.RESET + "Choose This Recipe");
                meta.setLore(lore);

                displayItem.setItemMeta(meta);

                currentGUI.setItem(i, displayItem);

            }

        }

    }

    public boolean checkDirection(final BlockFace face, final Block main, final Block middle, final Block bottom) {

        if(!main.getRelative(face).getType().equals(Material.DROPPER)) {
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

}
