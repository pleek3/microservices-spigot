package com.pleek3.minecraft.core.module.model;

import org.bukkit.Bukkit;

public interface Module {

    void onEnable();

    void onDisable();

    default void test() {
        System.out.println(Bukkit.isPrimaryThread());
    }


}
