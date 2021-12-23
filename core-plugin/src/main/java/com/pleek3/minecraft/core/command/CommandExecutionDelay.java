package com.pleek3.minecraft.core.command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandExecutionDelay {

    private static final int DEFAULT_COMMAND_DELAY = 300;

    private final UUID uuid;
    private final Map<String, Long> commands;

    public CommandExecutionDelay(UUID uuid) {
        this.uuid = uuid;
        this.commands = new HashMap<>();
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean containsCommand(String command) {
        return this.commands.containsKey(command) && this.commands.get(command) - System.currentTimeMillis() > 0L;
    }

    public void addCommand(String command) {
        this.commands.put(command, System.currentTimeMillis() + DEFAULT_COMMAND_DELAY);
    }

    public void removeCommand(String command) {
        this.commands.remove(command);
    }

    public Map<String, Long> getCommands() {
        return commands;
    }
}
