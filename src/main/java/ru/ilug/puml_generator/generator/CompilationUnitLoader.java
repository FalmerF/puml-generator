package ru.ilug.puml_generator.generator;

import com.github.javaparser.ast.CompilationUnit;

import java.util.List;

/**
 * An interface for loading and parsing Java source files into abstract syntax trees (AST).
 * <p>
 * The interface defines a contract for the components responsible for reading Java sources and converting them
 * into CompilationUnit objects from the JavaParser library.
 * <p>
 * Classes implementing this interface should provide:
 * <lu>
 * <li>Search for Java files in the specified directory or paths</li>
 * <li>Parsing found files in AST</li>
 * </lu>
 * <p>
 * The interface implementation can support various download sources:
 * local files, JAR archives, remote repositories, etc.
 */
public interface CompilationUnitLoader {

    /**
     * Downloads and parses Java source files, returning a list of abstract syntax trees (AST).
     *
     * @return list of CompilationUnit objects representing disassembled Java classes
     */
    List<CompilationUnit> load() throws Exception;
    
}
