package com.pleek3.minecraft.core.utils;

import com.google.common.collect.ImmutableSet;
import com.pleek3.minecraft.core.module.model.ModuleAdapter;

import java.util.Set;

public class Synchronizer {

    private Synchronizer() {
        throw new AssertionError("No com.pleek3.minecraft.core.utils.Synchronizer instances for you!");
    }

    public static ImmutableSet<ModuleAdapter> copyOf(Set<ModuleAdapter> moduleAdapters) {
        return ImmutableSet.copyOf(moduleAdapters);
    }

    public static boolean canSynchronizeWithSpigot(ImmutableSet<ModuleAdapter> moduleAdapters, int moduleCounter) {
        return moduleAdapters.size() == moduleCounter;
    }

}
