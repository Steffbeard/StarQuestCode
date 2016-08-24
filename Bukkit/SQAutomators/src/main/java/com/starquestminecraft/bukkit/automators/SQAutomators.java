package com.starquestminecraft.bukkit.automators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.starquestminecraft.sqtechbase.SQTechBase;

public class SQAutomators extends JavaPlugin {

    public static SQAutomators instance;
    public static double crafterUpgradeCost = 0.0;
    public static List<Recipe> recipes = new ArrayList<>();
    public static Map<ItemStack, Ingredient> ingredients = new HashMap<>();

    @Override
    public void onEnable() {

        getLogger().info("SQAutomators has been enabled!");

        instance = this;

        crafterUpgradeCost = getConfig().getDouble("crafterUpgradeCost");
        int run_speed = getConfig().getInt("runspeed");
        int production_speed = getConfig().getInt("productionSpeed");

        getServer().getScheduler().runTaskTimer(this, new AutomatorTask(this), run_speed, run_speed);

        AutoCrafter machine = new AutoCrafter(1000);
        SQTechBase.addMachineType(machine);

        Iterator<Recipe> recipeList = this.getServer().recipeIterator();

        while(recipeList.hasNext()) {

            Recipe r = recipeList.next();

            recipes.add(r);

            if(r instanceof ShapedRecipe) {

                for(ItemStack i : ((ShapedRecipe)r).getIngredientMap().values()) {

                    if(i != null) {

                        if(ingredients.containsKey(i)) {
                            ingredients.get(i).addRecipe(r);
                        }
                        else {
                            Ingredient ingredient = new Ingredient(i);
                            ingredient.addRecipe(r);
                            ingredients.put(i, ingredient);
                        }

                    }

                }

            }

            if(r instanceof ShapelessRecipe) {
                for(ItemStack i : ((ShapelessRecipe)r).getIngredientList()) {

                    if(i != null) {

                        if(ingredients.containsKey(i)) {
                            ingredients.get(i).addRecipe(r);
                        }
                        else {
                            ingredients.put(i, new Ingredient(i));
                        }

                    }

                }

            }

        }

        //This is where custom recipes are added
        List<Recipe> secondRecipeList = new ArrayList<>();

        secondRecipeList.add(new ShapedRecipe(new ItemStack(Material.REDSTONE, 9))
            .shape("...", ".*.", "...")
            .setIngredient('*', Material.REDSTONE_BLOCK)
            .setIngredient('.', Material.AIR)
        );

        secondRecipeList.add(new ShapedRecipe(new ItemStack(Material.IRON_INGOT, 9))
            .shape("...", ".*.", "...")
            .setIngredient('*', Material.IRON_BLOCK)
            .setIngredient('.', Material.AIR)
        );

        secondRecipeList.add(new ShapedRecipe(new ItemStack(Material.GOLD_INGOT, 9))
            .shape("...", ".*.", "...")
            .setIngredient('*', Material.GOLD_BLOCK)
            .setIngredient('.', Material.AIR)
        );

        secondRecipeList.add(new ShapedRecipe(new ItemStack(Material.DIAMOND, 9))
            .shape("...", ".*.", "...")
            .setIngredient('*', Material.DIAMOND_BLOCK)
            .setIngredient('.', Material.AIR)
        );

        secondRecipeList.add(new ShapedRecipe(new ItemStack(Material.EMERALD, 9))
            .shape("...", ".*.", "...")
            .setIngredient('*', Material.EMERALD_BLOCK)
            .setIngredient('.', Material.AIR)
        );

        secondRecipeList.add(new ShapedRecipe(new ItemStack(Material.INK_SACK, 9, (short)4))
            .shape("...", ".*.", "...")
            .setIngredient('*', Material.LAPIS_BLOCK)
            .setIngredient('.', Material.AIR)
        );

        secondRecipeList.add(new ShapedRecipe(new ItemStack(Material.WOOD_BUTTON, 1))
            .shape("...", ".*.", "...")
            .setIngredient('*', Material.WOOD)
            .setIngredient('.', Material.AIR)
        );

        //Iterates through custom recipes, adds Ingredients
        for(Recipe r : secondRecipeList) {

            recipes.add(r);

            for(ItemStack i : ((ShapedRecipe)r).getIngredientMap().values()) {

                if(i == null) {
                    continue;
                }

                if(ingredients.containsKey(i)) {
                    ingredients.get(i).addRecipe(r);
                }
                else {
                    Ingredient ingredient = new Ingredient(i);
                    ingredient.addRecipe(r);
                    ingredients.put(i, ingredient);
                }

            }

        }

        getServer().getPluginManager().registerEvents(new AutomatorListener(this), this);

    }

    @Override
    public void onDisable() {
        getLogger().info("SQAutomators has been disabled!");
    }

}
