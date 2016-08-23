package com.starquestminecraft.bukkit.boosters;

import java.util.HashMap;
import java.util.Map;

import com.gmail.nossr50.datatypes.skills.SkillType;

public class Booster {

    private final int id;
    private final Type type;
    private final int multiplier;
    private final String purchaser;
    private final long expire_time;
    private final boolean enabled;

    public Booster(final int id, final Type type, final int multiplier, final String purchaser, final long expire_time, final boolean enabled) {

        this.id = id;
        this.type = type;
        this.multiplier = multiplier;
        this.purchaser = purchaser;
        this.expire_time = expire_time;
        this.enabled = enabled;

    }

    public int getID() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public String getPurchaser() {
        return purchaser;
    }

    public boolean hasPurchaser() {
        return (purchaser != null);
    }

    public long getExpireTime() {
        return expire_time;
    }

    public boolean isActive() {
        return (enabled && (multiplier > 1));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public enum Type {

        EXPERIENCE("expboost", "Experience", "SQExpBoost"),
        MOB_DROP("mobdropboost", "Mob drop", "SQMobDropBoost"),
        SHEEP_SHEAR("sheepshearboost", "Sheep shearing", "SQSheepShearBoost"),
        SHOP("shopboost", "Shop", "SQShopBooster"),
        SPEED("speedboost", "Speed", "SQSpeedBooster"),
        SKILL_ACROBATICS("mcmmo-acrobaticsboost", "McMMO Acrobatics", "SQMMO-AcrobaticsBooster"),
        SKILL_ALCHEMY("mcmmo-alchemyboost", "McMMO Alchemy", "SQMMO-AlchemyBooster"),
        SKILL_ARCHERY("mcmmo-archeryboost", "McMMO Archery", "SQMMO-ArcheryBooster"),
        SKILL_AXES("mcmmo-axesboost", "McMMO Axes", "SQMMO-AxesBooster"),
        SKILL_EXCAVATION("mcmmo-excavationboost", "McMMO Excavation", "SQMMO-ExcavationBooster"),
        SKILL_FISHING("mcmmo-fishingboost", "McMMO Fishing", "SQMMO-FishingBooster"),
        SKILL_HERBALISM("mcmmo-herbalismboost", "McMMO Herbalism", "SQMMO-HerbalismBooster"),
        SKILL_MINING("mcmmo-miningboost", "McMMO Mining", "SQMMO-MiningBooster"),
        SKILL_PILOTING("mcmmo-pilotingboost", "McMMO Piloting", "SQMMO-PilotingBooster"),
        SKILL_REPAIR("mcmmo-repairboost", "McMMO Repair", "SQMMO-RepairBooster"),
        SKILL_SALVAGE("mcmmo-salvageboost", "McMMO Salvage", "SQMMO-SalvageBooster"),
        SKILL_SMELTING("mcmmo-smeltingboost", "McMMO Smelting", "SQMMO-SmeltingBooster"),
        SKILL_SWORDS("mcmmo-swordsboost", "McMMO Swords", "SQMMO-SwordsBooster"),
        SKILL_TAMING("mcmmo-tamingboost", "McMMO Taming", "SQMMO-TamingBooster"),
        SKILL_UNARMED("mcmmo-unarmedboost", "McMMO Unarmed", "SQMMO-UnarmedBooster"),
        SKILL_WOODCUTTING("mcmmo-woodcuttingboost", "McMMO Woodcutting", "SQMMO-WoodcuttingBooster");

        private static final Map<String, Type> BY_CONFIG_KEY = new HashMap<>();

        private final String config_key;
        private final String multiplier_name;
        private final String permission;
        private final String name;

        private Type(final String config_key, final String multiplier_name, final String permission) {

            this.config_key = config_key;
            this.multiplier_name = multiplier_name;
            this.permission = permission.toLowerCase();
            this.name = permission;

        }

        public String getName() {
            return name;
        }

        public String getConfigKey() {
            return config_key;
        }

        public String getMultiplierName() {
            return multiplier_name;
        }

        public String getPermission() {
            return permission;
        }

        public static Type byName(final String key) throws IllegalArgumentException {

            Type type = BY_CONFIG_KEY.get(key.toLowerCase());

            if(type != null) {
                return type;
            }

            return Type.valueOf(key.toUpperCase());

        }

        public static Type bySkillType(final SkillType skill) throws IllegalArgumentException {

            switch(skill) {

                case ACROBATICS:
                    return SKILL_ACROBATICS;

                case ALCHEMY:
                    return SKILL_ALCHEMY;

                case ARCHERY:
                    return SKILL_ARCHERY;

                case AXES:
                    return SKILL_AXES;

                case EXCAVATION:
                    return SKILL_EXCAVATION;

                case FISHING:
                    return SKILL_FISHING;

                case HERBALISM:
                    return SKILL_HERBALISM;

                case MINING:
                    return SKILL_MINING;

                case REPAIR:
                    return SKILL_REPAIR;

                case SALVAGE:
                    return SKILL_SALVAGE;

                case SMELTING:
                    return SKILL_SMELTING;

                case SWORDS:
                    return SKILL_SWORDS;

                case TAMING:
                    return SKILL_TAMING;

                case UNARMED:
                    return SKILL_UNARMED;

                case WOODCUTTING:
                    return SKILL_WOODCUTTING;

            }

            throw new IllegalArgumentException("Unknown skill " + skill + "!");

        }

        static {

            for(Type type : values()) {
                BY_CONFIG_KEY.put(type.config_key.toLowerCase(), type);
            }

        }

    }

}
