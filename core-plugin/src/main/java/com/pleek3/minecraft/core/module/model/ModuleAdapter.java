package com.pleek3.minecraft.core.module.model;

import com.pleek3.minecraft.core.annotations.ModuleData;
import com.pleek3.minecraft.core.module.ModuleClassLoader;

public class ModuleAdapter {

    private final Module module;
    private final ModuleData moduleData;
    private final ModuleClassLoader moduleClassLoader;

    public ModuleAdapter(final Module module, final ModuleData moduleData, final ModuleClassLoader moduleClassLoader) {
        this.module = module;
        this.moduleData = moduleData;
        this.moduleClassLoader = moduleClassLoader;
    }

    public Module getModule() {
        return module;
    }

    public ModuleData getModuleData() {
        return moduleData;
    }

    public ModuleClassLoader getModuleClassLoader() {
        return moduleClassLoader;
    }
}
