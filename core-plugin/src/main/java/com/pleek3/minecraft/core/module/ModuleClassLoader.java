package com.pleek3.minecraft.core.module;

import com.pleek3.minecraft.core.Bootstrap;
import com.pleek3.minecraft.core.annotations.ModuleData;
import com.pleek3.minecraft.core.module.model.Module;
import com.pleek3.minecraft.core.module.model.ModuleAdapter;
import com.pleek3.minecraft.core.services.ModuleService;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModuleClassLoader extends URLClassLoader {

    //Spigot start
    static {
        try {
            Method method = ClassLoader.class.getDeclaredMethod("registerAsParallelCapable");
            boolean oldAccessible = method.isAccessible();
            method.setAccessible(true);
            method.invoke(null);
            method.setAccessible(oldAccessible);
            System.out.println("Set ModuleClassLoader as paralles capable");

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ignored) {
        }
    }
    //Spigot end

    private final ModuleService loaderService;
    private final File file, dataFolder;
    private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();

    private ModuleAdapter adapter;

    public ModuleClassLoader(File file, ModuleService loaderService, ClassLoader parent) throws MalformedURLException {
        super(new URL[]{file.toURI().toURL()}, parent);

        this.loaderService = loaderService;
        this.file = file;
        this.dataFolder = new File("");
    }

    public ModuleAdapter load() {

        JarFile jar = null;
        try {
            jar = new JarFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (jar == null)
            return null;

        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();

            if (!entry.getName().endsWith(".class"))
                continue;

            String className = entry.getName().replace("/", ".").replace(".class", "");

            try {
                loadClass(className, true);
                Class<?> clazz = Class.forName(className, false, this);
                this.classes.put(className, clazz);
                /*
                Map<PluginLoader, ConcurrentHashMap<String, Class<?>>> l = ModuleReflectionHelper.getJavaPluginLoaderClasses();
                l.values().forEach(m -> {
                    m.put(className, clazz);
                });
                ConcurrentHashMap<String, Class<?>> p = ModuleReflectionHelper.getPluginClassLoaderClasses();
                p.put(className, clazz);
                ModuleReflectionHelper.setPluginClassLoaderClasses(p);
                ModuleReflectionHelper.setJavaPluginLoaderClasses(l);
                 */
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        Class<?> clazz = jar.stream().filter(entry -> entry.getName().endsWith(".class")).filter(entry -> {
            try {
                return Class.forName(entry.getName().replace(".class", "").replace("/", "."),
                                false,
                                this)
                        .getAnnotation(ModuleData.class) != null;
            } catch (ClassNotFoundException e) {
                return false;
            }
        }).map(entry -> {
            try {
                return Class.forName(entry.getName().replace(".class", "").replace("/", "."), false,
                        this);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }).findFirst().orElse(null);

        if (clazz == null)
            return null;

        Class<? extends Module> moduleClass = null;
        try {
            moduleClass = clazz.asSubclass(Module.class);
            if (findLoadedClass(moduleClass.getName()) == null) {
                loadClass(moduleClass.getName());
            }
        } catch (ClassCastException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (moduleClass == null) return null;

        ModuleData data = moduleClass.getAnnotation(ModuleData.class);

        if (data == null) return null;

        Module module = null;
        try {
            module = moduleClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (module == null) return null;

        module.onEnable();
        return this.adapter = new ModuleAdapter(module, data, this);
    }

    public void unload() {
        this.classes.clear();
    }

    public void reload() {
        if (this.adapter == null) {
            this.load();
            return;
        }

        String moduleName = this.adapter.getModuleData().name();

        System.out.println("[Module] The " + moduleName + " module is restarting");

        this.unload();
        new Thread(() -> Bootstrap.getModuleService().loadModule(moduleName, this.file)).start();

    }

    public ModuleService getLoaderService() {
        return loaderService;
    }

    public File getFile() {
        return file;
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public Map<String, Class<?>> getClasses() {
        return classes;
    }

    public ModuleAdapter getAdapter() {
        return adapter;
    }
}
