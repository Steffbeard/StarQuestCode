package com.starquestminecraft.bukkit.powerboost.boost;

/**
 * contains data about a powerboost
 */
public abstract class PowerBoost {

    private final int amount;

    public PowerBoost(final int amount) {
        this.amount = amount;
    }

    public final int getAmount() {
        return amount;
    }

}
