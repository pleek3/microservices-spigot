package com.pleek3.minecraft.core.command.parameter;

public class ParameterData {

    private final Parameter[] parameters;

    public ParameterData(Parameter[] parameters) {
        this.parameters = parameters;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public Parameter get(int index) {
        return parameters[index];
    }

    public int getParameterCount() {
        return this.parameters.length;
    }

}
