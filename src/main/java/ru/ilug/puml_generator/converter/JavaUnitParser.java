package ru.ilug.puml_generator.converter;

import com.github.javaparser.ast.CompilationUnit;

/**
 * An interface for parsing a separate abstract syntax tree (AST) of a Java class
 * into a string representation for a PlantUML class diagram.
 * <p>
 * This interface defines a contract for the components responsible for converting a compilation
 * unit (a single Java file) into the corresponding fragment of PlantUML code.
 * <p>
 * The interface works with individual compilation units, while the
 * {@link ru.ilug.puml_generator.generator.CompilationUnitToPumlConverter CompilationUnitToPumlConverter} is responsible for combining the results and
 * building a complete diagram with relationships between classes.
 */
public interface JavaUnitParser {

    /**
     * Converts the abstract syntax tree (AST) of a Java class
     * into a string representation for a PlantUML class diagram.
     *
     * @param unit a CompilationUnit object representing a disassembled Java class
     * @return a string with a PlantUML representation of this class or interface
     */
    String parse(CompilationUnit unit);

}
