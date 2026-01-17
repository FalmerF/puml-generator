package ru.ilug.puml_generator.generator;

import com.github.javaparser.ast.CompilationUnit;

import java.util.List;

public interface CompilationUnitToPumlConverter {

    String convert(List<CompilationUnit> units);

}
