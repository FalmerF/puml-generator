package ru.ilug.puml_generator.config;

import lombok.Data;

import java.nio.file.Path;
import java.util.regex.Pattern;

@Data
public final class Config {

    private final Path srcPath = Path.of("./src/main/java/");
    private final Path outputFile = Path.of("./classes.puml");
    private final PackagesConfig packages = new PackagesConfig(new Pattern[0], new Pattern[0]);

}
