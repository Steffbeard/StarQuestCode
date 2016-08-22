package com.starquestminecraft.bukkit.automators;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class Ingredient {

    private final ItemStack item;
    private final List<Recipe> recipes;

    public Ingredient(final ItemStack item) {

        this.item = item;
        this.recipes = new ArrayList<>();

    }

    public void addRecipe(final Recipe recipe) {

        for(int i = 0; i < recipes.size(); i++) {

            if(recipes.get(i).getResult().equals(recipe.getResult())) {
                return;
            }

        }

        recipes.add(recipe);

    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

}
