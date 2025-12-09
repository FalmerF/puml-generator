package ru.ilug.puml_generator.config;

import java.nio.file.Path;

public record Config(Path srcPath, Path outputFile, PackagesConfig packages) {
}
