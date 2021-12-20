package com.pleek3.minecraft.core.module.scanner;

import com.pleek3.minecraft.core.Bootstrap;
import com.pleek3.minecraft.core.module.model.ModuleAdapter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PluginEnvironmentScan {

    private final ModuleAdapter moduleAdapter;
    private final List<String> requiredModules, requiredSpigotPlugins;

    private boolean canBoot;

    public PluginEnvironmentScan(final ModuleAdapter moduleAdapter) {
        this.moduleAdapter = moduleAdapter;
        this.requiredSpigotPlugins = new ArrayList<>(Arrays.asList(this.moduleAdapter.getModuleData().spigotPlugins()));
        this.requiredModules = new ArrayList<>(Arrays.asList(this.moduleAdapter.getModuleData().modules()));

        this.canBoot = false;
        startScan();
    }

    public void startScan() {
        this.canBoot = true;

        if (this.requiredModules.isEmpty() && this.requiredSpigotPlugins.isEmpty()) return;

       /* this.requiredSpigotPlugins.forEach(requiredDependencies -> {
            if (Bukkit.getPluginManager().getPlugin(requiredDependencies) == null) {
                this.canBoot = false;
                System.out.println("[Module] Module " + this.moduleAdapter.getModuleData().name() +
                        " cannot be started! Please install " + requiredDependencies + " (Plugin) to start this module.");
            }
        });*/

      /*  this.requiredModules.forEach(requiredModules -> {
            if (Bootstrap.getModuleService().getModulAdapter(requiredModules) == null) {
                this.canBoot = false;
                System.out.println("[Module] Module " + this.moduleAdapter.getModuleData().name() +
                        " cannot be started! Please install " + requiredModules + " (Module) to start this module.");
            }
        });*/
    }

    public boolean isBootable() {
        return canBoot;
    }

    public ModuleAdapter getModuleAdapter() {
        return moduleAdapter;
    }
}
