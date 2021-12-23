package com.pleek3.minecraft.core.command;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

import java.util.List;

public class CommandMap extends SimpleCommandMap {

    public CommandMap(Server server) {
        super(server);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String cmdLine) {
        return super.tabComplete(sender, cmdLine, null);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String cmdLine, Location location) {
        return this.tabComplete(sender, cmdLine);
    }
}
