package com.pleek3.minecraft.core.module;

import com.pleek3.minecraft.core.Bootstrap;
import com.pleek3.minecraft.core.annotations.ModuleData;
import com.pleek3.minecraft.core.exceptions.NoModuleFoundException;
import com.pleek3.minecraft.core.module.model.Module;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModuleClassLoader {

    private final List<Class<Module>> pluginClasses;

    public ModuleClassLoader() {
        this.pluginClasses = this.loadClasses();
        // this.loadPlugins();

        loadModules();
    }

    public void loadModules() {
        File[] pluginFolder = new File(Bootstrap.getPathService().getRessource("plugin-folder")).listFiles(pathname -> pathname.getName().endsWith(".jar"));


        if (pluginFolder == null) return;

        for (File file : pluginFolder) {
            JarFile jarFile = null;
            try {
                jarFile = new JarFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (jarFile == null) {
                try {
                    throw new NoModuleFoundException(file.getName());
                } catch (NoModuleFoundException ignored) {
                }
                return;
            }

            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry entry = jarEntries.nextElement();

                if (!entry.getName().endsWith(".class")) continue;

                String className = entry.getName().replace("/", ".").replace(".class", "");

                System.out.println(className);

                try {
                    ModuleClassLoader.class.getClassLoader().loadClass(className);
                    //todo: load class into spigot
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            Class<?> clazz = jarFile.stream().filter(jarEntry -> jarEntry.getName().endsWith(".class")).filter(jarEntry -> {
                try {
                    return Class.forName(jarEntry.getName().replace(".class", "").replace("/", ".")).getAnnotation(ModuleData.class) != null;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
            }).map(jarEntry -> {
                try {
                    return Class.forName(jarEntry.getName().replace(".class", "").replace("/", "."));
                } catch (ClassNotFoundException e) {
                    return null;
                }
            }).findFirst().orElse(null);

            if (clazz == null) return;


            Class<? extends Module> moduleClass = clazz.asSubclass(Module.class);

            ModuleData data = moduleClass.getAnnotation(ModuleData.class);

            if (data == null) return;

            Module module = null;

            try {
                module = moduleClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

            if (module == null) return;

            Bootstrap.getModuleService().injectModule(module, data);
        }
    }

    public List<Class<Module>> loadClasses() {
        File[] plugins = new File(Bootstrap.getPathService().getRessource("plugin-folder")).listFiles(pathname -> pathname.getName().endsWith(".jar"));

        if (plugins == null) {
            return null;
        }

        List<URL> pluginURLS = new ArrayList<URL>(plugins.length) {{
            Arrays.stream(plugins).forEach(file -> {
                try {
                    add(file.toURI().toURL());
                } catch (MalformedURLException ignored) {
                }
            });
        }};

        URLClassLoader loader = new URLClassLoader(pluginURLS.toArray(new URL[0]));


        Enumeration<URL> resources = null;
        try {
            resources = loader.findResources("plugin.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (resources == null) {
            return null;
        }

        List<Class<Module>> classes = new ArrayList<>(pluginURLS.size());

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            Properties properties = new Properties();
            try (InputStream is = resource.openStream()) {
                properties.load(is);
                String className = properties.getProperty("entry-point");

                Class<?> coreClass = loader.loadClass(className);

                if (coreClass.getSuperclass().isAssignableFrom(Module.class)) {
                    Class<Module> moduleClass = (Class<Module>) coreClass;
                    classes.add(moduleClass);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return classes;
    }

    private void loadPlugins() {
        for (Class<Module> coreClass : this.pluginClasses) {
            try {
                Module module = coreClass.newInstance();
                //       Bootstrap.getModuleService().injectModule(module);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

}
