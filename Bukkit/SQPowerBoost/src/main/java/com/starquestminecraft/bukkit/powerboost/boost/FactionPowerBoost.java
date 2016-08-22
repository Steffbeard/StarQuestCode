package com.starquestminecraft.bukkit.powerboost.boost;

import com.massivecraft.factions.entity.Faction;

public class FactionPowerBoost extends PowerBoost {

    private final Faction faction;

    public FactionPowerBoost(final Faction faction, final int amount) {

        super(amount);

        this.faction = faction;

    }

    public Faction getFaction() {
        return faction;
    }

}
