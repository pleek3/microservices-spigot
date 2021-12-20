package com.pleek3.minecraft.core.utils;

import com.pleek3.minecraft.core.services.ModuleService;
import com.pleek3.minecraft.core.services.PathService;

/**
 * This class consists of {@code static} getter methods to store all services uniquely. These getter methods
 * Include the ModuleService and the PathService.
 */
public class Services {

    public static ModuleService getModuleService;
    public static PathService getPathService;

    static {
        getPathService = new PathService();
        getModuleService = new ModuleService();
    }

    private Services() {
        throw new AssertionError("No com.pleek3.minecraft.core.utils.Services instances for you!");
    }


}
