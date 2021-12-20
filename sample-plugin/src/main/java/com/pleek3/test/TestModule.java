package com.pleek3.test;

import com.pleek3.minecraft.core.annotations.ModuleData;
import com.pleek3.minecraft.core.module.model.Module;

@ModuleData(name = "TestModule", author = "Yannick", version = "1.0.0", description = "A simple test module", modules = {"ModuleA"})
public class TestModule implements Module {

    public static void main(String[] args) {
        System.out.println(TestModule.class.getName());
    }

    @Override
    public void onEnable() {
        System.out.println("Hello World!");
    }

    @Override
    public void onDisable() {
        System.out.println("Bye Bye!");
    }

}
