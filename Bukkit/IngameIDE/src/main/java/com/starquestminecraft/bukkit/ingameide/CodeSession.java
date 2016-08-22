package com.starquestminecraft.bukkit.ingameide;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;

public class CodeSession {

    private static final String OPEN_IDE_MESSAGE = ChatColor.GREEN + "========" + ChatColor.GOLD + "[" + ChatColor.BLUE + "IGIDE v1.0.0" + ChatColor.GOLD + "]" + ChatColor.GREEN + "========";
    private static final String EXECUTE_MESSAGE = ChatColor.GREEN + "========" + ChatColor.GOLD + "[" + ChatColor.BLUE + "Executing" + ChatColor.GOLD + "]" + ChatColor.GREEN + "========";

    private final Player coder;
    private final List<String> lines;

    public CodeSession(final Player sender) {

        this.coder = sender;
        this.lines = new ArrayList<>();

        coder.sendMessage(OPEN_IDE_MESSAGE);

    }

    public void execute() {

        coder.sendMessage(EXECUTE_MESSAGE);

        ExecutionFactory.execute(lines);

    }

    public List<String> getLines() {
        return lines;
    }

    public void addLine(final String line) {

        lines.add(line);

        coder.sendMessage(ChatColor.DARK_AQUA + line);

    }

}
