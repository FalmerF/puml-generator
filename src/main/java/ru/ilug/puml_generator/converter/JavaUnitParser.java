package ru.ilug.puml_generator.converter;

import com.github.javaparser.ast.CompilationUnit;

public interface JavaUnitParser {

    String parse(CompilationUnit unit);

}
