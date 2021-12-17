package com.pleek3.minecraft.core;

import com.pleek3.minecraft.core.services.ModuleService;
import com.pleek3.minecraft.core.services.PathService;
import org.bukkit.plugin.java.JavaPlugin;

public class Bootstrap extends JavaPlugin {

    private static PathService pathService;
    private static ModuleService moduleService;

    public static void main(String[] args) {
        pathService = new PathService();
        moduleService = new ModuleService();
    }

    public static PathService getPathService() {
        return pathService;
    }

    public static ModuleService getModuleService() {
        return moduleService;
    }
}
