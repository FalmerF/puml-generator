package ru.ilug.puml_generator.controller;

import com.github.javaparser.ast.CompilationUnit;

import java.util.List;

public interface JavaSrcLoader {

    List<CompilationUnit> load() throws Exception;
}
