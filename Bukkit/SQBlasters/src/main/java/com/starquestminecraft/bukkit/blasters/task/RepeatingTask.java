package com.starquestminecraft.bukkit.blasters.task;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

import org.bukkit.Material;

import com.starquestminecraft.bukkit.blasters.Blaster;
import com.starquestminecraft.bukkit.blasters.SQBlasters;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.starquestminecraft.bukkit.blasters.util.ItemUtil;

public class RepeatingTask implements Runnable {

    private final SQBlasters plugin;
    private final Set<Player> automatic_players = Collections.newSetFromMap(new WeakHashMap<>());
    private final Set<Player> scoped_players = Collections.newSetFromMap(new WeakHashMap<>());

    public RepeatingTask(final SQBlasters plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {

        if(!automatic_players.isEmpty()) {

            Iterator<Player> itr = automatic_players.iterator();
            while(itr.hasNext()) {

                Player player = itr.next();

                if(!plugin.canFireOrReloadBlaster(player)) {
                    continue;
                }

                ItemStack item = player.getInventory().getItemInMainHand();

                if(!ItemUtil.isTypeWithMeta(item, Blaster.MATERIAL)) {
                    itr.remove();
                    continue;
                }

                ItemMeta meta = item.getItemMeta();
                Blaster blaster = plugin.getBlaster(item);

                if((blaster == null) || (blaster.getType() != Blaster.Type.AUTOMATIC)) {
                    itr.remove();
                    continue;
                }

                plugin.useBlaster(player, blaster, item, meta);

            }

        }

        if(!scoped_players.isEmpty()) {

            Iterator<Player> itr = scoped_players.iterator();
            while(itr.hasNext()) {

                Player player = itr.next();
                ItemStack handItem = player.getInventory().getItemInMainHand();
                Blaster blaster = plugin.getBlaster(handItem);

                if(blaster == null) {
                    itr.remove();
                    player.removePotionEffect(PotionEffectType.SLOW);
                    continue;
                }

                int scope = blaster.getScope();

                if(!player.hasPotionEffect(PotionEffectType.SLOW)) {
                    itr.remove();
                    continue;
                }

                for(PotionEffect effect : player.getActivePotionEffects()) {

                    if(effect.getType() != PotionEffectType.SLOW) {
                        continue;
                    }

                    if(scope != effect.getAmplifier()) {

                        itr.remove();
                        player.removePotionEffect(PotionEffectType.SLOW);

                    }

                    break;

                }

            }

        }

    }

    public void toggleAutomatic(final Player player) {

        if(!automatic_players.add(player)) {
            automatic_players.remove(player);
        }

    }

    public void toggleScope(final Player player, final int scope) {

        if(scoped_players.add(player)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000000, scope));
        }
        else {

            scoped_players.remove(player);

            player.removePotionEffect(PotionEffectType.SLOW);

        }

    }

}
