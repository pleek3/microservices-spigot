package com.pleek3.minecraft.core;

import com.pleek3.minecraft.core.module.ModuleClassLoader;
import com.pleek3.minecraft.core.services.ModuleService;
import com.pleek3.minecraft.core.services.PathService;
import org.bukkit.plugin.java.JavaPlugin;

public class Bootstrap extends JavaPlugin {

    private static PathService pathService;
    private static ModuleClassLoader moduleClassLoader;
    private static ModuleService moduleService;

    public static void main(String[] args) {
        pathService = new PathService();
        moduleService = new ModuleService();
        moduleClassLoader = new ModuleClassLoader();
    }

    public static ModuleClassLoader getModuleManager() {
        return moduleClassLoader;
    }

    public static PathService getPathService() {
        return pathService;
    }

    public static ModuleService getModuleService() {
        return moduleService;
    }
}
