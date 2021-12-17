package com.pleek3.minecraft.core.services;

import com.pleek3.minecraft.core.Bootstrap;
import com.pleek3.minecraft.core.annotations.ModuleData;
import com.pleek3.minecraft.core.module.ModuleClassLoader;
import com.pleek3.minecraft.core.module.model.ModuleAdapter;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarFile;

public class ModuleService {

    private final List<ModuleAdapter> moduleAdapters;

    public ModuleService() {
        this.moduleAdapters = new ArrayList<>(this.loadModules());
    }

    private List<ModuleAdapter> loadModules() {
        File[] files = new File(Bootstrap.getPathService().getRessource("plugin-folder")).listFiles(pathname -> pathname.getName().endsWith(".jar"));

        if (files == null) return new ArrayList<>();

        List<ModuleAdapter> adapters = new ArrayList<>();

        for (File file : files) {
            try {
                ModuleClassLoader loader = new ModuleClassLoader(file, ModuleService.this, getClass().getClassLoader());

                ModuleAdapter module = loader.load();
                System.out.println(module.getModuleClassLoader().getAdapter() == null);
                adapters.add(module);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return adapters;
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
        if (!isModule(file, name)) return;

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

    public boolean isModule(File file, String name) {
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
                return data != null && data.name().equals(name);
            } catch (ClassNotFoundException | MalformedURLException e) {
                return false;
            }
        });
    }

    public ModuleAdapter getModulAdapter(final String name) {
        for (ModuleAdapter adapter : this.moduleAdapters) {
            if (adapter.getModuleData().name().equalsIgnoreCase(name))
                return adapter;
        }
        return null;
    }

    public List<ModuleAdapter> getLoadedModules() {
        return moduleAdapters;
    }


}
