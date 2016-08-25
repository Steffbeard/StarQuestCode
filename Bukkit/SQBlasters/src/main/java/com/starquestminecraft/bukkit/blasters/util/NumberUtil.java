package com.starquestminecraft.bukkit.blasters.util;

public final class NumberUtil {

    private NumberUtil() {

    }

    public static int parseInt(final String str, final int def) {

        try {
            return Integer.parseInt(str);
        }
        catch(NumberFormatException ex) {

        }

        return def;

    }

    public static double parseDouble(final String str, final double def) {

        try {
            return Double.parseDouble(str);
        }
        catch(NumberFormatException ex) {

        }

        return def;

    }

}
