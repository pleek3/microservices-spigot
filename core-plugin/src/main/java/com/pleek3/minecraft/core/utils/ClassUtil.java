package com.pleek3.minecraft.core.utils;


import com.google.common.collect.ImmutableSet;
import com.pleek3.minecraft.core.Bootstrap;
import com.pleek3.minecraft.core.module.model.Module;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ClassUtil {

    // Static utility class -- cannot be created.
    private ClassUtil() {
    }

    /**
     * Gets all the classes in a the provided package.
     *
     * @param packageName The package to scan classes in.
     * @return The classes in the package packageName.
     */
    public static Collection<Class<?>> getClassesInPackage(Module module, String packageName) {
        Collection<Class<?>> classes = new ArrayList<>();

        CodeSource codeSource;

        if (module == null) {
            codeSource = Bootstrap.class.getProtectionDomain().getCodeSource();
        } else {
            codeSource = module.getClass().getProtectionDomain().getCodeSource();
        }

        URL resource = codeSource.getLocation();
        String relPath = packageName.replace('.', '/');
        String resPath = resource.getPath().replace("%20", " ");
        String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        JarFile jarFile;

        System.out.println(jarPath);


        try {
            jarFile = new JarFile(jarPath);
        } catch (IOException e) {
            throw (new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e));
        }

        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            String className = null;

            if (entryName.startsWith("target")) continue;

            if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length()))
                className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");


            if (className != null) {
                Class<?> clazz = null;

                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if (clazz != null)
                    classes.add(clazz);
            }
        }

        try {
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (ImmutableSet.copyOf(classes));
    }

}