package ru.ilug.puml_generator.config;

import lombok.Data;

import java.nio.file.Path;

@Data
public final class Config {

    private Path srcPath = Path.of("./src/main/java/");
    private Path outputFile = Path.of("./main.puml");
    private Path[] dependencies = new Path[0];
    private String[] packagesInclude = new String[0];
    private String[] packagesExclude = new String[0];

    private boolean interfaces = true;
    private boolean abstractClasses = true;
    private boolean subClasses = true;
    private boolean generics = true;
    private boolean javadoc = false;

    private boolean fields = true;
    private boolean fieldVisibility = true;
    private boolean fieldType = true;
    private boolean fieldName = true;
    private boolean publicFields = true;
    private boolean privateFields = true;
    private boolean protectedFields = true;
    private boolean staticFields = true;

    private boolean methods = true;
    private boolean methodVisibility = true;
    private boolean methodType = true;
    private boolean methodName = true;
    private boolean methodArgs = true;
    private boolean methodArgsType = true;
    private boolean methodArgsName = true;
    private boolean publicMethods = true;
    private boolean privateMethods = true;
    private boolean protectedMethods = true;
    private boolean staticMethods = true;
    private boolean abstractMethods = true;

}
