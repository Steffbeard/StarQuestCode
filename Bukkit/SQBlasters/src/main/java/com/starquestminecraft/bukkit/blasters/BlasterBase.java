package com.starquestminecraft.bukkit.blasters;

public class BlasterBase {

    private final String name;
    private final Blaster.Type type;
    private final int mag_size;
    private final int fire_delay;
    private final int reload_delay;
    private final int scope;
    private final double bolt_damage;
    private final int bolt_lifetime;
    private final int bolt_fire_ticks;

    public BlasterBase(final String name, final Blaster.Type type, final int mag_size, final int fire_delay, final int reload_delay, final int scope, final double bolt_damage, final int bolt_lifetime, final int bolt_fire_ticks) {

        this.name = name;
        this.type = type;
        this.mag_size = mag_size;
        this.fire_delay = fire_delay;
        this.reload_delay = reload_delay;
        this.scope = scope;
        this.bolt_damage = bolt_damage;
        this.bolt_lifetime = bolt_lifetime;
        this.bolt_fire_ticks = bolt_fire_ticks;

    }

    public String getName() {
        return name;
    }

    public Blaster.Type getType() {
        return type;
    }

    public int getMagazineCapacity() {
        return mag_size;
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

}
