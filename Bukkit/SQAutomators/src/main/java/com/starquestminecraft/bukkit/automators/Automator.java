package com.starquestminecraft.bukkit.automators;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

import com.starquestminecraft.sqtechbase.objects.GUIBlock;
import com.starquestminecraft.sqtechbase.objects.Machine;
import com.starquestminecraft.sqtechbase.objects.MachineType;

public class Automator extends Machine {

    private final List<Recipe> current_recipes;
    private final List<Recipe> open_recipes;
    private final List<List<Recipe>> pages;
    private final double upgrade_cost;

    private Player owner;
    private Recipe recipe;

    private int screen;
    private int page;

    public Automator(final int energy, final GUIBlock gui_block, final MachineType type, final double upgrade_cost) {

        super(energy, gui_block, type);

        this.current_recipes = new ArrayList<>();
        this.open_recipes = new ArrayList<>();
        this.pages = new ArrayList<>();
        this.upgrade_cost = upgrade_cost;

        data.put("level", 1);

    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(final Recipe recipe) {
        this.recipe = recipe;
    }

    public int getPage() {
        return page;
    }

    public void setPage(final int page) {
        this.page = page;
    }

    public void nextPage() {
        this.page++;
    }

    public void previousPage() {
        this.page--;
    }

    public int getScreen() {
        return screen;
    }

    public void setScreen(final int screen) {
        this.screen = screen;
    }

    public double getUpgradeCost() {
        return upgrade_cost;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(final Player owner) {
        this.owner = owner;
    }

    public List<Recipe> getCurrentRecipes() {
        return current_recipes;
    }

    public void setCurrentRecipes(final List<Recipe> recipes) {

        this.current_recipes.clear();
        this.current_recipes.addAll(recipes);

    }

    public List<Recipe> getOpenRecipes() {
        return open_recipes;
    }

    public void setOpenRecipes(final List<Recipe> recipes) {

        this.open_recipes.clear();
        this.open_recipes.addAll(recipes);

    }

    public List<List<Recipe>> getPageList() {
        return pages;
    }

    public void setPageList(final List<List<Recipe>> list) {

        this.pages.clear();
        this.pages.addAll(list);

    }

}
