package com.starquestminecraft.bukkit.asteroidbays;

import java.io.File;
import java.io.FilenameFilter;

class SchematicFilenameFilter implements FilenameFilter {

    private final String prefix;

    SchematicFilenameFilter(final String prefix) {
        this.prefix = prefix.toLowerCase();
    }

    @Override
    public boolean accept(final File dir, final String name) {

        String namelc = name.toLowerCase();

        return namelc.startsWith(prefix) && namelc.endsWith(".schematic");

    }

}
