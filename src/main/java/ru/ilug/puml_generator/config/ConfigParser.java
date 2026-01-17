package ru.ilug.puml_generator.config;

import java.io.IOException;
import java.nio.file.Path;

public interface ConfigParser {

    Config load(Path configPath) throws IOException;

    boolean isMatchedFile(String file);

    String[] getDefaultFiles(String fileName);

}
