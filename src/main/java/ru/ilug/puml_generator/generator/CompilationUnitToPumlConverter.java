package ru.ilug.puml_generator.generator;

import com.github.javaparser.ast.CompilationUnit;

import java.util.List;

/**
 * An interface for converting abstract syntax trees (AST) of Java classes
 * into a text representation of class diagrams in the PlantUML format.
 * <p>
 * This interface defines a contract for the components responsible for converting the Java code
 * structure, represented as an AST, into the corresponding PlantUML syntax.
 */
public interface CompilationUnitToPumlConverter {

    /**
     * Converts a list of abstract syntax trees (AST) of Java classes
     * into a text representation of a class diagram in the PlantUML format.
     *
     * @param units list of CompilationUnit objects representing disassembled Java classes
     * @return a row with chart contents in PlantUML format
     */
    String convert(List<CompilationUnit> units);

}
