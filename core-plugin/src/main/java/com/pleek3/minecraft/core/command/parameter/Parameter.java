package com.pleek3.minecraft.core.command.parameter;

public class Parameter {

    private final Class<?> classType;
    private final String parameter;
    private final String[] tabCompleteFlags;

    private final String defaultValue;
    private final boolean wildcard;

    public Parameter(Class<?> classType, String parameter, String defaultValue, boolean wildcard, String[] tabCompleteFlags) {
        this.classType = classType;
        this.parameter = parameter;
        this.defaultValue = defaultValue;
        this.wildcard = wildcard;
        this.tabCompleteFlags = tabCompleteFlags;
    }

    public Class<?> getClassType() {
        return classType;
    }

    public String getParameter() {
        return parameter;
    }

    public String[] getTabCompleteFlags() {
        return tabCompleteFlags;
    }
}
