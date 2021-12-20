package com.pleek3.minecraft.core;

import com.pleek3.minecraft.core.utils.Services;
import org.bukkit.plugin.java.JavaPlugin;

public class Bootstrap extends JavaPlugin {

    public static void main(String[] args) {
        Services.getModuleService.loadModules();

    }

}
