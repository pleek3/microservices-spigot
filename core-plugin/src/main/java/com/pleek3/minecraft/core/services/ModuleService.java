package com.pleek3.minecraft.core.services;

import com.pleek3.minecraft.core.annotations.ModuleData;
import com.pleek3.minecraft.core.module.model.Module;
import com.pleek3.minecraft.core.module.model.ModuleAdapter;
import com.pleek3.minecraft.core.module.scanner.PluginEnvironmentScan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleService {

    private final Map<String, ModuleAdapter> injectedModules;

    public ModuleService() {
        this.injectedModules = new HashMap<>();
    }

    public void injectModule(final Module module, final ModuleData data) {
        if (this.injectedModules.containsKey(data.name()))
            return;

        ModuleAdapter adapter = new ModuleAdapter(module, data);

        PluginEnvironmentScan scan = new PluginEnvironmentScan(adapter);

        if (!scan.isBootable())
            return;


        this.injectedModules.put(data.name(), adapter);
        System.out.println("Trying to start " + data.name() + " module");
        module.onEnable();
        System.out.println(data.name() + " successfully loaded!");
    }

    public void subtractModule(final String name) {
        if (!this.injectedModules.containsKey(name)) return;

        ModuleAdapter adapter = getModulAdapter(name);
        System.out.println("Trying to stop " + adapter.getModuleData().name() + " module");
        adapter.getModule().onDisable();
        System.out.println(adapter.getModuleData().name() + " successfully stopped!");

        this.injectedModules.remove(name);
    }

    public ModuleAdapter getModulAdapter(final String name) {
        return this.injectedModules.getOrDefault(name, null);
    }

    public List<String> getActiveModules() {
        return new ArrayList<>(this.injectedModules.keySet());
    }


}
