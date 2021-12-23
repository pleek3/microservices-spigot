package com.pleek3.minecraft.core;

import com.pleek3.minecraft.core.utils.Services;
import org.bukkit.plugin.java.JavaPlugin;

public class Bootstrap extends JavaPlugin {
    private static Bootstrap instance;

    @Override
    public void onEnable() {
        instance = this;
        Services.getModuleService.loadModules();
    }

    public static Bootstrap getInstance() {
        return instance;
    }
}
