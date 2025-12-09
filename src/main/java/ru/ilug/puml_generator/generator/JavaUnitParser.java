package ru.ilug.puml_generator.generator;

import com.github.javaparser.ast.CompilationUnit;

import java.util.function.Consumer;

public interface JavaUnitParser {

    String parse(CompilationUnit unit);

}
