package ru.ilug.puml_generator.error;

public class PumlGeneratorConfigLoadException extends RuntimeException {
    public PumlGeneratorConfigLoadException(String path, Throwable throwable) {
        super("Puml generator config in path '%s' not loaded".formatted(path), throwable);
    }
}
