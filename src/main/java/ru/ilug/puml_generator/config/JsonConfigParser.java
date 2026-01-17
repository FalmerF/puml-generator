package ru.ilug.puml_generator.config;

import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.file.Path;

public class JsonConfigParser implements ConfigParser {

    @Override
    public Config load(Path configPath) throws IOException {
        JsonMapper jsonMapper = new JsonMapper();
        return jsonMapper.readValue(configPath.toFile(), Config.class);
    }

    @Override
    public boolean isMatchedFile(String file) {
        return file.endsWith(".json");
    }

    @Override
    public String[] getDefaultFiles(String fileName) {
        return new String[]{fileName + ".json"};
    }
}
