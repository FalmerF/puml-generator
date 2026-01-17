package ru.ilug.puml_generator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Path;

public class YamlConfigParser implements ConfigParser {

    @Override
    public Config load(Path configPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(configPath.toFile(), Config.class);
    }

    @Override
    public boolean isMatchedFile(String file) {
        return file.endsWith(".yaml") || file.endsWith(".yml");
    }

    @Override
    public String[] getDefaultFiles(String fileName) {
        return new String[] {fileName + ".yaml", fileName + ".yml"};
    }
}
