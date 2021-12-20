package com.pleek3.minecraft.core.services;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.pleek3.minecraft.core.annotations.ModuleData;
import com.pleek3.minecraft.core.module.ModuleClassLoader;
import com.pleek3.minecraft.core.utils.Synchronizer;
import com.pleek3.minecraft.core.module.model.ModuleAdapter;
import com.pleek3.minecraft.core.module.scanner.PluginEnvironmentScan;
import com.pleek3.minecraft.core.utils.Services;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarFile;

public class ModuleService {

    private final Set<ModuleAdapter> moduleAdapters;

    public ModuleService() {
        this.moduleAdapters = new HashSet<>();
    }

    /**
     * This method implements all modules asynchronously from the plugin folder.
     * If a dependent module is not ready yet due to the asynchrone, the method tries 5 more times
     * with a delay of 5 milliseconds to implement the module.
     */
    public void loadModules() {
        System.out.println("[Module] Search for modules in " + Services.getPathService.getRessource("plugin-folder"));
        final File[] files = new File(Services.getPathService.getRessource("plugin-folder")).listFiles(pathname -> pathname.getName().endsWith(".jar"));

        if (files == null) {
            System.out.println("[Module] No modules could be found");
            return;
        }

        final int moduleCount = Arrays.stream(files).filter(this::isModule).toArray().length;
        AtomicBoolean enabled = new AtomicBoolean(false);

        System.out.println("[Module] " + moduleCount + " modules found");
        System.out.println("[Module] Start async implementation of modules");

        for (File file : files) {
            new Thread(() -> {
                try {
                    ModuleClassLoader loader = new ModuleClassLoader(file, ModuleService.this, getClass().getClassLoader());
                    System.out.println("[Module] Implementation of module " + file.getName().replace(".jar", "") + " started");

                    ModuleAdapter adapter;
                    int moduleLoadingTries = 0;

                    do {
                        if (moduleLoadingTries != 0) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ignored) {
                            }
                        }

                        adapter = loadModule(loader);
                    } while (adapter == null && moduleLoadingTries++ < 5);

                    this.moduleAdapters.add(adapter);

                    if (!enabled.get() && Synchronizer.canSynchronizeWithSpigot(this.getLoadedModules(), moduleCount)) {
                        enabled.set(true);
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                ModuleService.this.enableModules(ModuleService.this.moduleAdapters, 0);
                            }
                        }.runTask(null);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void enableModules(final Set<ModuleAdapter> moduleAdapters, int moduleLoadingTries) {
        moduleAdapters.removeIf(this::enableModule);

        if (!moduleAdapters.isEmpty() && moduleLoadingTries++ < 20) {
            enableModules(moduleAdapters, moduleLoadingTries);
        } else {
            System.out.println("[Module] All modules successfully enabled!");
        }
    }

    private boolean enableModule(final ModuleAdapter moduleAdapter) {
        PluginEnvironmentScan scan = new PluginEnvironmentScan(moduleAdapter);

        if (scan.isBootable()) {
            moduleAdapter.getModule().onEnable();
            return true;
        }

        return false;
    }

    private ModuleAdapter loadModule(final ModuleClassLoader moduleClassLoader) {
        return moduleClassLoader.load();
    }

    public void unloadModule(String name) {
        ModuleAdapter adapter = getModulAdapter(name);
        if (adapter == null) return;

        adapter.getModuleClassLoader().unload();
        this.moduleAdapters.remove(adapter);
    }

    public void loadModule(String name, File file) {
        ModuleAdapter adapter = getModulAdapter(name);

        if (adapter != null) return;
        if (!isModule(file)) return;

        try {
            adapter = new ModuleClassLoader(file, this, getClass().getClassLoader()).load();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (adapter != null) {
            this.moduleAdapters.add(adapter);
            System.out.println("[Module] Module Adapter " + name + " successfully loaded!");
        }
    }

    public boolean isModule(File file) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (jarFile == null)
            return false;
        return jarFile.stream().anyMatch(entry -> {
            try {
                ModuleData data = Class.forName(entry.getName().replace(".class", "").replace("/", "."),
                        false, new URLClassLoader(new URL[]{file.toURI().toURL()}, this.getClass().getClassLoader())).getAnnotation(ModuleData.class);
                return data != null;
            } catch (ClassNotFoundException | MalformedURLException e) {
                return false;
            }
        });
    }

    public ModuleAdapter getModulAdapter(final String name) {
        for (ModuleAdapter adapter : getLoadedModules()) {
            if (adapter.getModuleData().name().equalsIgnoreCase(name))
                return adapter;
        }
        return null;
    }

    public ImmutableSet<ModuleAdapter> getLoadedModules() {
        return Synchronizer.copyOf(this.moduleAdapters);
    }


}
