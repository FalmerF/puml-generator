package ru.ilug.puml_generator.controller;

import com.github.javaparser.ast.CompilationUnit;

import java.util.List;

public interface CompilationUnitToPumlConverter {

    String convert(List<CompilationUnit> units);

}
