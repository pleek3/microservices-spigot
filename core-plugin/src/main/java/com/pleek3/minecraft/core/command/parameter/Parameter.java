package com.pleek3.minecraft.core.command.parameter;

public class Parameter {

    private final Class<?> classType;
    private final String parameter;

    public Parameter(Class<?> classType, String parameter) {
        this.classType = classType;
        this.parameter = parameter;
    }

    public Class<?> getClassType() {
        return classType;
    }

    public String getParameter() {
        return parameter;
    }
}
