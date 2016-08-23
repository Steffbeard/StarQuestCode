package com.starquestminecraft.bukkit.boosters.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import com.starquestminecraft.bukkit.boosters.Booster;
import com.starquestminecraft.bukkit.boosters.SQBoosters;

public class EntityListener implements Listener {

    private final SQBoosters plugin;

    public EntityListener(final SQBoosters plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(final EntityDeathEvent event) {

        LivingEntity entity = event.getEntity();

        if(!plugin.isAllowedMobDropType(entity.getType())) {
            return;
        }

        Booster booster = plugin.getBooster(Booster.Type.MOB_DROP);

        if((booster == null) || !booster.isActive()) {
            return;
        }

        List<ItemStack> drops = new ArrayList<>(event.getDrops());
        EntityEquipment equipment = entity.getEquipment();

        drops.remove(equipment.getHelmet());
        drops.remove(equipment.getChestplate());
        drops.remove(equipment.getLeggings());
        drops.remove(equipment.getBoots());

        checkAndDecrement(drops, equipment.getItemInMainHand());
        checkAndDecrement(drops, equipment.getItemInOffHand());

        for(ItemStack item : drops) {

            item.setAmount(item.getAmount() * booster.getMultiplier());

            event.getDrops().add(item);

        }

    }

    private static void checkAndDecrement(final List<ItemStack> list, final ItemStack item) {

        if(item == null) {
            return;
        }

        if(!list.contains(item)) {
            return;
        }

        Iterator<ItemStack> itr = list.iterator();

        while(itr.hasNext()) {

            ItemStack itr_item = itr.next();

            if(!itr_item.equals(item)) {
                continue;
            }

            itr_item.setAmount(itr_item.getAmount() - 1);

            if(itr_item.getAmount() < 1) {
                itr.remove();
            }

        }

    }

}
