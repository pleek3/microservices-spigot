package com.pleek3.minecraft.core.module.model;

import com.pleek3.minecraft.core.utils.Services;

public abstract class JavaModule implements Module {

    private Class<?> mainClass;

    public void setMainClass(Class<?> mainClass) {
        this.mainClass = mainClass;
    }

    public Class<?> getMainClass() {
        return mainClass;
    }

    public void loadCommands(String packageName) {
        Services.getCommandService.loadCommands(this, packageName);
    }

}
