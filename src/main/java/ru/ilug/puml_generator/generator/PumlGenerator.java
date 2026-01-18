package ru.ilug.puml_generator.generator;

import com.github.javaparser.ast.CompilationUnit;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * A class diagram generator in the PlantUML format.
 * <p>
 * The class is responsible for the full cycle of diagram generation:
 * <ol>
 * <li>Uploading Java source files as AST (abstract syntax trees)</li>
 * <li>Converting AST to text representation of PlantUML</li>
 * <li>Saving the generated content to an output file</li>
 * </ol>
 */
@RequiredArgsConstructor
public class PumlGenerator {

    private final CompilationUnitToPumlConverter converter;
    private final CompilationUnitLoader loader;
    private final OutputSaver saver;

    /**
     * The main method for generating class diagrams in the PlantUML format.
     * <p>
     * Performs a full cycle of chart generation:
     * <ol>
     * <li>Uploading Java source files as AST (abstract syntax trees)</li>
     * <li>Converting AST to text representation of PlantUML</li>
     * <li>Saving the generated content to an output file</li>
     * </ol>
     * <p>
     * The method is the entry point for the diagram generation process and coordinates the operation of all generator components.
     */
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

