package com.starquestminecraft.bukkit.ingameide;

import java.io.File;
import java.io.PrintWriter;

import org.codehaus.janino.JavaSourceClassLoader;

public class ExecutionFactory {

    private static final ClassLoader CLASS_LOADER = new JavaSourceClassLoader(IngameIDE.class.getClassLoader(), new File[] {IngameIDE.getInstance().getDataFolder()}, null);
    private static final String NEWLINE = System.getProperty("line.separator");

    private static final String BEFORE_CODE =
        "import org.bukkit.*;" + NEWLINE +
        "import org.bukkit.block.*;" + NEWLINE +
        "import org.bukkit.enchantments.*;" + NEWLINE +
        "import org.bukkit.entity.*;" + NEWLINE +
        "import org.bukkit.entity.minecart.*;" + NEWLINE +
        "import org.bukkit.inventory.*;" + NEWLINE +
        "import org.bukkit.inventory.meta.*;" + NEWLINE +
        "import org.bukkit.material.*;" + NEWLINE +
        "import org.bukkit.potion.*;" + NEWLINE +
        "import org.bukkit.projectiles.*;" + NEWLINE +
        "import org.bukkit.scheduler.*;" + NEWLINE +
        NEWLINE +
        "public class IngameIDEExecutable implements Runnable {" + NEWLINE +
        NEWLINE +
        "public void run() {";

    private static final String AFTER_CODE = "}" + NEWLINE + "}";

    public static void execute(final Iterable<String> lines) {

        File file = writeFile(lines);

        try {
            ((Runnable)CLASS_LOADER.loadClass(file.getName()).newInstance()).run();
        }
        catch(ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

    }

    private static File writeFile(final Iterable<String> lines) {

        try {

            File file = new File(IngameIDE.getInstance().getDataFolder() + "/IngameIDEExecutable.java");

            file.getParentFile().mkdirs();

            try(PrintWriter out = new PrintWriter(file)) {

                out.println(BEFORE_CODE);

                for(String line : lines) {
                    out.println(line);
                }

                out.println(AFTER_CODE);

            }

            return file;

        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return null;

    }

}
