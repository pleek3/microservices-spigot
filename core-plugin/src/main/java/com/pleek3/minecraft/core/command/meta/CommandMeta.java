package com.pleek3.minecraft.core.command.meta;

import com.pleek3.minecraft.core.annotations.Command;
import com.pleek3.minecraft.core.annotations.SubCommand;
import com.pleek3.minecraft.core.command.CoreCommand;
import com.pleek3.minecraft.core.command.parameter.Parameter;
import com.pleek3.minecraft.core.command.parameter.ParameterData;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

public class CommandMeta {

    private final CoreCommand parent;

    private String defaultName;

    private String commandPermission;
    private String commandDescription;
    private String[] commandNames;
    private boolean asyncExecution;

    private final HashMap<String, SubCommandMeta> subCommandMeta;
    private final HashMap<String, List<String>> aliases;

    private final List<String> subCommandAliases;

    public CommandMeta(CoreCommand parent) {
        this.parent = parent;
        this.subCommandAliases = new ArrayList<>();
        this.aliases = new HashMap<>();

        this.fetchCommand();
        this.subCommandMeta = fetchSubCommands();
    }

    private void fetchCommand() {
        Command command = this.parent.getClass().getAnnotation(Command.class);

        if (command == null) return;

        this.commandPermission = command.permission();
        this.commandDescription = command.description();
        this.commandNames = command.commands();
        this.asyncExecution = command.async();

        this.defaultName = this.commandNames[0];
    }

    private HashMap<String, SubCommandMeta> fetchSubCommands() {
        HashMap<String, SubCommandMeta> tmpHashMap = new HashMap<>();
        Method[] methods = this.parent.getClass().getDeclaredMethods();

        for (Method method : methods)
            if (method.isAnnotationPresent(SubCommand.class)) {
                SubCommand subCommand = method.getAnnotation(SubCommand.class);

                Class<?>[] parameterTypes = method.getParameterTypes();
                String[] annotationParameterTypes = StringUtils.substringsBetween(subCommand.parameters(), "{", "}");

                if (parameterTypes.length == 0) break;

                Parameter[] parameters = new Parameter[parameterTypes.length - 1];

                for (int i = 1; i < parameterTypes.length; i++) {
                    Class<?> parameter = method.getParameterTypes()[i];
                    parameters[i - 1] = new Parameter(parameter, annotationParameterTypes[i - 1]);
                }

                ParameterData parameterData = new ParameterData(parameters);
                SubCommandMeta meta = new SubCommandMeta(subCommand, parameterData, this.parent, method);

                String defaultAlias = meta.getDefaultAlias();

                tmpHashMap.put(defaultAlias, meta);
                this.aliases.put(defaultAlias, Arrays.asList(meta.getAliases()));
            }

        return tmpHashMap;
    }

    public HashMap<String, SubCommandMeta> getSubCommandMeta() {
        return subCommandMeta;
    }

    public SubCommandMeta getSubCommandMeta(String input) {
        return this.aliases.entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(input))
                .findFirst()
                .map(entry -> this.subCommandMeta.get(entry.getKey()))
                .orElse(null);
    }

    public CoreCommand getParent() {
        return parent;
    }

    public String getCommandPermission() {
        return commandPermission;
    }

    public String getCommandDescription() {
        return commandDescription;
    }

    public String[] getCommandNames() {
        return commandNames;
    }

    public boolean isAsyncExecution() {
        return asyncExecution;
    }
}
