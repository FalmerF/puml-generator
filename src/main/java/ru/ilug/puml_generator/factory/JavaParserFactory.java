package ru.ilug.puml_generator.factory;

import com.github.javaparser.JavaParser;

import java.io.IOException;

public interface JavaParserFactory {

    JavaParser create() throws IOException;

}
