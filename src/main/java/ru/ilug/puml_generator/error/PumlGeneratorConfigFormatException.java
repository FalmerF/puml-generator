package ru.ilug.puml_generator.error;

public class PumlGeneratorConfigFormatException extends RuntimeException {
    public PumlGeneratorConfigFormatException(String path) {
        super("Unknown format of config '%s'".formatted(path));
    }
}
