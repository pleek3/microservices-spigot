package com.pleek3.test;

import com.pleek3.minecraft.core.annotations.ModuleData;
import com.pleek3.minecraft.core.module.model.JavaModule;
import com.pleek3.minecraft.core.module.model.Module;
import com.pleek3.minecraft.core.utils.ClassUtil;
import com.pleek3.minecraft.core.utils.Services;
import org.bukkit.Bukkit;

@ModuleData(name = "TestModule", author = "Yannick", version = "1.0.0", description = "A simple test module")
public class TestModule extends JavaModule {

    @Override
    public void onModuleStart() {
        setMainClass(this.getClass());
        loadCommands("com.pleek3.test");

        Bukkit.broadcastMessage("TestModule funktioniert einwandfrei!");
    }

    @Override
    public void onModuleStop() {
    }

}
