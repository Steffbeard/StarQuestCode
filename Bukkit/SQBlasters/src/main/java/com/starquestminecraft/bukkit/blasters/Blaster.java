package com.starquestminecraft.bukkit.blasters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Blaster {

    public enum Type {

        STANDARD,
        AUTOMATIC;

    }

    public static final Material MATERIAL = Material.BOW;

    public static final String ITEM_DISPLAYNAME_FORMAT = ChatColor.RESET + "Blaster " + ChatColor.GOLD + "(%d/%d)";
    public static final String LORE_PREFIX_TYPE = ChatColor.GOLD + "Type: " + ChatColor.WHITE;
    public static final String LORE_PREFIX_AMMO = ChatColor.GOLD + "Ammo: " + ChatColor.WHITE;
    public static final String LORE_PREFIX_DAMAGE = ChatColor.GOLD + "Damage: " + ChatColor.WHITE;
    public static final String LORE_PREFIX_FIRE_DELAY = ChatColor.GOLD + "Fire Time: " + ChatColor.WHITE;
    public static final String LORE_PREFIX_RELOAD_DELAY = ChatColor.GOLD + "Reload Time: " + ChatColor.WHITE;

    public static final String LORE_TYPE_NEW = LORE_PREFIX_TYPE + "New Blaster";

    private final String name;
    private final Blaster.Type type;
    private final int ammo_max;
    private final int fire_delay;
    private final int reload_delay;
    private final int scope;
    private final double bolt_damage;
    private final int bolt_lifetime;
    private final int bolt_fire_ticks;
    private final double damage_multiplier;
    private final boolean is_flaming;

    private int ammo;

    public Blaster(final String name, final Blaster.Type type, final int mag_remain, final int mag_size, final int fire_delay, final int reload_delay, final int scope, final double bolt_damage, final int bolt_lifetime, final int bolt_fire_ticks, final double damage_multiplier, final boolean is_flaming) {

        this.name = name;
        this.type = type;
        this.ammo = mag_remain;
        this.ammo_max = mag_size;
        this.fire_delay = fire_delay;
        this.reload_delay = reload_delay;
        this.scope = scope;
        this.bolt_damage = bolt_damage;
        this.bolt_lifetime = bolt_lifetime;
        this.bolt_fire_ticks = bolt_fire_ticks;
        this.damage_multiplier = damage_multiplier;
        this.is_flaming = is_flaming;

    }

    public Blaster(final BlasterBase base, final ItemMeta meta) {
        this(base.getName(), base.getType(), base.getMagazineCapacity(), base.getMagazineCapacity(), base.getFireDelay(), base.getReloadDelay(), base.getScope(), base.getBoltBaseDamage(), base.getBoltLifetime(), base.getBoltFireTicks(), calcDamageMultiplier(meta), isFlaming(meta));
    }

    public String getName() {
        return name;
    }

    public Blaster.Type getType() {
        return type;
    }

    public int getMagazineRemaining() {
        return ammo;
    }

    public int getMagazineCapacity() {
        return ammo_max;
    }

    public int getFireDelay() {
        return fire_delay;
    }

    public int getReloadDelay() {
        return reload_delay;
    }

    public int getScope() {
        return scope;
    }

    public double getBoltBaseDamage() {
        return bolt_damage;
    }

    public int getBoltLifetime() {
        return bolt_lifetime;
    }

    public int getBoltFireTicks() {
        return bolt_fire_ticks;
    }

    public boolean isFlaming() {
        return is_flaming;
    }

    public double getBoltDamage() {
        return bolt_damage * damage_multiplier;
    }

    public double getBoltDamageMultiplier() {
        return damage_multiplier;
    }

    public boolean isMagazineEmpty() {
        return (ammo == 0);
    }

    public void reload(final ItemStack item, final ItemMeta meta) {
        reload(item, meta, ammo_max);
    }

    public void reload(final ItemStack item, final ItemMeta meta, final int amount) {

        ammo = amount;

        List<String> lore = meta.getLore();

        lore.set(1, Blaster.LORE_PREFIX_AMMO + ammo + "/" + ammo_max);

        meta.setLore(lore);
        meta.setDisplayName(ChatColor.GREEN + "Reloading...");

        item.setItemMeta(meta);

    }

    public void useAmmo(final ItemStack item, final ItemMeta meta) {

        ammo--;

        List<String> lore = meta.getLore();

        lore.set(1, Blaster.LORE_PREFIX_AMMO + ammo + "/" + ammo_max);

        meta.setLore(lore);
        meta.setDisplayName(formatDisplayName());

        item.setItemMeta(meta);

    }

    public static boolean checkLore(final List<String> lore) {

        boolean type_ok = false;
        boolean ammo_ok = false;
        boolean ammo_max_ok = false;
        boolean damage_ok = false;
        boolean fire_delay_ok = false;
        boolean reload_delay_ok = false;

        for(String line : lore) {

            if(line.startsWith(Blaster.LORE_PREFIX_TYPE)) {
                type_ok = true;
            }
            else if(line.startsWith(Blaster.LORE_PREFIX_AMMO)) {
                ammo_ok = true;
            }
            else if(line.startsWith(Blaster.LORE_PREFIX_DAMAGE)) {
                damage_ok = true;
            }
            else if(line.startsWith(Blaster.LORE_PREFIX_FIRE_DELAY)) {
                fire_delay_ok = true;
            }
            else if(line.startsWith(Blaster.LORE_PREFIX_RELOAD_DELAY)) {
                reload_delay_ok = true;
            }

        }

        return (type_ok && ammo_ok && ammo_max_ok && damage_ok && fire_delay_ok && reload_delay_ok);

    }

    public String formatDisplayName() {
        return formatDisplayName(ammo, ammo_max);
    }

    public void resetLore(final List<String> lore) {

        removeLore(lore);

        List<String> other = Collections.emptyList();

        if(!lore.isEmpty()) {
            other = new ArrayList<>(lore);
        }

        lore.clear();

        lore.add(LORE_PREFIX_TYPE + name);
        lore.add(LORE_PREFIX_AMMO + ammo + "/" + ammo_max);
        lore.add(LORE_PREFIX_DAMAGE + bolt_damage);
        lore.add(LORE_PREFIX_FIRE_DELAY + fire_delay);
        lore.add(LORE_PREFIX_RELOAD_DELAY + reload_delay);

        lore.addAll(other);

    }

    public static void removeLore(final List<String> lore) {

        Iterator<String> itr = lore.iterator();
        while(itr.hasNext()) {

            String line = itr.next();
            boolean remove = false;

            if(line.startsWith(Blaster.LORE_PREFIX_TYPE)) {
                remove = true;
            }
            else if(line.startsWith(Blaster.LORE_PREFIX_AMMO)) {
                remove = true;
            }
            else if(line.startsWith(Blaster.LORE_PREFIX_DAMAGE)) {
                remove = true;
            }
            else if(line.startsWith(Blaster.LORE_PREFIX_FIRE_DELAY)) {
                remove = true;
            }
            else if(line.startsWith(Blaster.LORE_PREFIX_RELOAD_DELAY)) {
                remove = true;
            }

            if(remove) {
                itr.remove();
            }

        }

    }

    public static double calcDamageMultiplier(final ItemMeta meta) {
        return (1 + (0.25 * meta.getEnchantLevel(Enchantment.ARROW_DAMAGE)));
    }

    public static String formatDisplayName(final int ammo, final int ammo_max) {
        return String.format(ITEM_DISPLAYNAME_FORMAT, ammo, ammo_max);
    }

    public static boolean isFlaming(final ItemMeta meta) {
        return meta.hasEnchant(Enchantment.ARROW_FIRE);
    }

}
