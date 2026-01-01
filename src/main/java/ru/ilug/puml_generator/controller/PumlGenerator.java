package ru.ilug.puml_generator.controller;

import com.github.javaparser.ast.CompilationUnit;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PumlGenerator {

    private final CompilationUnitToPumlConverter converter;
    private final CompilationUnitLoader loader;
    private final OutputSaver saver;

    public void generate() {
        List<CompilationUnit> units = load();
        String pumlContent = generate(units);
        save(pumlContent);
    }

    private List<CompilationUnit> load() {
        try {
            return loader.load();
        } catch (Exception e) {
            throw new RuntimeException("Error on loading java sources", e);
        }
    }

    private String generate(List<CompilationUnit> units) {
        try {
            return converter.convert(units);
        } catch (Exception e) {
            throw new RuntimeException("Error on generate puml content", e);
        }
    }

    private void save(String pumlContent) {
        try {
            saver.save(pumlContent);
        } catch (Exception e) {
            throw new RuntimeException("Error on save puml content");
        }
    }

}
