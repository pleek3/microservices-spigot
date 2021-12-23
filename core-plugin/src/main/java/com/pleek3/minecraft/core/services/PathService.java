package com.pleek3.minecraft.core.services;

import java.util.HashMap;
import java.util.Map;

public class PathService {

    public final String pluginFolder = "C:\\Users\\Anwender\\IdeaProjects\\minecraft-core\\plugins";

    private final Map<String, String> paths;

    public PathService() {
        this.paths = new HashMap<String, String>() {{
            put("plugin-folder", "C:\\Users\\Anwender\\IdeaProjects\\minecraft-core\\plugins");
        }};
    }

    public String getRessource(String path) {
        return this.paths.getOrDefault(path, null);
    }

}
