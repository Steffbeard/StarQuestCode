package com.starquestminecraft.bukkit.blasters.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {

    private ItemUtil() {

    }

    public static boolean isTypeWithMeta(final ItemStack item, final Material type) {

        if(item.getType() != type) {
            return false;
        }

        if(!item.hasItemMeta()) {
            return false;
        }

        return true;

    }

}
