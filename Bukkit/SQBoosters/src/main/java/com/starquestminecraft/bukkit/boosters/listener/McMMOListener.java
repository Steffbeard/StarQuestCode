package com.starquestminecraft.bukkit.boosters.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.nossr50.datatypes.skills.XPGainReason;
import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;

import com.starquestminecraft.bukkit.boosters.Booster;
import com.starquestminecraft.bukkit.boosters.SQBoosters;

public class McMMOListener implements Listener {

    private final SQBoosters plugin;

    public McMMOListener(final SQBoosters plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMcMMOPlayerXpGain(final McMMOPlayerXpGainEvent event) {

        if(event.getXpGainReason().equals(XPGainReason.COMMAND)) {
            return;
        }

        Booster booster = plugin.getBooster(Booster.Type.bySkillType(event.getSkill()));

        if((booster == null) || !booster.isActive()) {
            return;
        }

        event.setRawXpGained(event.getRawXpGained() * booster.getMultiplier());

    }

}
