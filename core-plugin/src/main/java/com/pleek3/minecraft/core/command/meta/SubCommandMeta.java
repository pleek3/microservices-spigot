package com.pleek3.minecraft.core.command.meta;

import com.pleek3.minecraft.core.annotations.SubCommand;
import com.pleek3.minecraft.core.command.parameter.ParameterData;
import com.pleek3.minecraft.core.command.parameter.type.CommandTypeParameter;
import com.pleek3.minecraft.core.command.parameter.Parameter;
import com.pleek3.minecraft.core.utils.Services;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SubCommandMeta {

    //  private final String[] parameters;
    private final SubCommand subCommand;
    private final ParameterData parameterData;

    private final String[] aliases;

    private final Object instance;
    private final Method method;

    private final String permissionString;
    private final String defaultAlias;
    private final String parameterString;

    public SubCommandMeta(SubCommand subCommand, ParameterData parameterData, Object instance, Method method) {
        this.subCommand = subCommand;
        this.parameterData = parameterData;
        this.aliases = this.subCommand.command();
        this.permissionString = this.subCommand.perm();
        this.parameterString = this.subCommand.parameters();
        //   this.parameters = this.subCommand.parameters().split(" ");
        this.defaultAlias = this.aliases[0];

        this.instance = instance;
        this.method = method;
    }

    public String getParameterString() {
        return parameterString;
    }

    public String getDefaultAlias() {
        return this.defaultAlias;
    }

    public String[] getAliases() {
        return aliases;
    }

    public SubCommand getSubCommand() {
        return subCommand;
    }

    public String getPermissionString() {
        return permissionString;
    }

    public ParameterData getParameterData() {
        return parameterData;
    }

    public boolean execute(Player player, String[] params) {
        Object[] objects = new Object[this.parameterData.getParameterCount() + 1];
        objects[0] = player;

        for (int i = 0; i < this.parameterData.getParameterCount(); i++) {
            if (i == params.length) return false;
            Parameter parameter = this.parameterData.getParameters()[i];

            CommandTypeParameter<?> commandTypeParameter = Services.getCommandService.getParameter(parameter.getClassType());

            if (commandTypeParameter == null)
                throw new NullPointerException("Found no type parameter for class: " + parameter.getClassType());

            if (parameter.getClassType() == String.class && i + 1 >= this.parameterData.getParameterCount() && i + 1 < params.length) {
                String builder = Arrays.stream(params, i, params.length).collect(Collectors.joining(" "));
                objects[i + 1] = builder;
            } else objects[i + 1] = commandTypeParameter.parse(player, params[i]);
        }

        try {
            this.method.invoke(this.instance, objects);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return true;
    }

}
