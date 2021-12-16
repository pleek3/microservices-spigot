package com.pleek3.minecraft.core.module.model;

import com.pleek3.minecraft.core.annotations.ModuleData;


public class ModuleAdapter {

    private final Module module;
    private final ModuleData moduleData;

    public ModuleAdapter(final Module module, final ModuleData moduleData) {
        this.module = module;
        this.moduleData = moduleData;
    }

    public Module getModule() {
        return module;
    }

    public ModuleData getModuleData() {
        return moduleData;
    }
}
