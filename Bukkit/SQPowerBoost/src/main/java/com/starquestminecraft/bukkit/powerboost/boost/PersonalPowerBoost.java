package com.starquestminecraft.bukkit.powerboost.boost;

import java.util.UUID;

public class PersonalPowerBoost extends PowerBoost {

    private final UUID profile_id;

    public PersonalPowerBoost(final UUID profile_id, final int amount) {

        super(amount);

        this.profile_id = profile_id;

    }

    public UUID getProfileID() {
        return profile_id;
    }

}
