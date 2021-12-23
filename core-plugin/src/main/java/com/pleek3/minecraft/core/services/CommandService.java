package com.pleek3.minecraft.core.services;

import com.pleek3.minecraft.core.command.CoreCommand;
import com.pleek3.minecraft.core.command.cooldown.CommandExecutionCooldown;
import com.pleek3.minecraft.core.command.parameter.type.CommandTypeParameter;
import com.pleek3.minecraft.core.command.parameter.type.PlayerParameterType;
import com.pleek3.minecraft.core.command.parameter.type.StringParameterType;
import com.pleek3.minecraft.core.module.model.Module;
import com.pleek3.minecraft.core.utils.ClassUtil;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandService {

    private final List<CoreCommand> commands;
    private final Map<Class<?>, CommandTypeParameter<?>> commandTypeParameters;

    private final Map<UUID, CommandExecutionCooldown> commandExecutionCooldowns;


    public CommandService() {
        this.commands = new ArrayList<>();
        this.commandTypeParameters = new HashMap<>();
        this.commandExecutionCooldowns = new HashMap<>();
        loadCommands(null, "com.pleek3.minecraft.core.commands");

        registerTypeParameter(Player.class, new PlayerParameterType());
        registerTypeParameter(String.class, new StringParameterType());
    }

    /*
     * Vielleicht wollen wir die registrierten Parameter mal überschreiben, d.h. keine contains Abfrage
     */
    public void registerTypeParameter(Class<?> clazz, CommandTypeParameter<?> parameter) {
        this.commandTypeParameters.put(clazz, parameter);
    }

    public void loadCommands(Module module, String packageName) {
        ClassUtil.getClassesInPackage(module, packageName).stream().filter(CoreCommand.class::isAssignableFrom)
                .forEach(aClass -> {
                    try {
                        this.commands.add((CoreCommand) aClass.newInstance());
                    } catch (InstantiationException | IllegalAccessException ignored) {
                    }
                });
    }

    public CommandTypeParameter<?> getParameter(Class<?> clazz) {
        return this.commandTypeParameters.get(clazz);
    }

    public List<CoreCommand> getCommands() {
        return commands;
    }

    public CommandExecutionCooldown getCooldown(Player player) {
        if (!this.commandExecutionCooldowns.containsKey(player.getUniqueId()))
            this.commandExecutionCooldowns.put(player.getUniqueId(), new CommandExecutionCooldown(false));

        return this.commandExecutionCooldowns.get(player.getUniqueId());
    }

}
