package ru.ilug.puml_generator;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import ru.ilug.puml_generator.config.Config;
import ru.ilug.puml_generator.config.PackagesConfig;
import ru.ilug.puml_generator.controller.PumlGenerateController;
import ru.ilug.puml_generator.controller.PumlGenerator;
import ru.ilug.puml_generator.file_system.FileSystemJavaSrcLoader;
import ru.ilug.puml_generator.file_system.FileSystemOutputSaver;
import ru.ilug.puml_generator.generator.JavaUnitParser;
import ru.ilug.puml_generator.generator.PumlGeneratorImpl;
import ru.ilug.puml_generator.parser.ClassFilter;
import ru.ilug.puml_generator.parser.JavaUnitParserImpl;
import ru.ilug.puml_generator.parser.printer.Printer;
import ru.ilug.puml_generator.parser.printer.UnitPrinter;
import ru.ilug.puml_generator.parser.printer.clazz.*;
import ru.ilug.puml_generator.parser.printer.clazz.body.ClassBodyPrinter;
import ru.ilug.puml_generator.parser.printer.clazz.body.field.FieldNamePrinter;
import ru.ilug.puml_generator.parser.printer.clazz.body.field.FieldStaticModifierPrinter;
import ru.ilug.puml_generator.parser.printer.clazz.body.field.FieldTypePrinter;
import ru.ilug.puml_generator.parser.printer.clazz.body.field.FieldVisibilityPrinter;
import ru.ilug.puml_generator.parser.printer.clazz.body.method.*;
import ru.ilug.puml_generator.parser.printer.clazz.body.method.parameter.ParameterNamePrinter;
import ru.ilug.puml_generator.parser.printer.clazz.body.method.parameter.ParameterTypePrinter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        Config config = loadConfig(args);

        setupStaticJavaParser(config);

        FileSystemJavaSrcLoader loader = new FileSystemJavaSrcLoader(config.getSrcPath());
        FileSystemOutputSaver saver = new FileSystemOutputSaver(config.getOutputFile());

        UnitPrinter printer = createUnitPrinter(config);
        JavaUnitParser parser = new JavaUnitParserImpl(printer);
        PumlGenerator pumlGenerator = new PumlGeneratorImpl(parser);

        PumlGenerateController generateController = new PumlGenerateController(pumlGenerator, loader, saver);

        generateController.generate();
    }

    private static Config loadConfig(String[] args) throws IOException {
        JsonMapper jsonMapper = new JsonMapper();
        Path configPath = Path.of("./pumlg-config.json");

        if (args.length != 0) {
            configPath = Path.of(args[0]);
        }

        Config config;

        if (Files.exists(configPath)) {
            config = jsonMapper.readValue(configPath.toFile(), Config.class);
        } else {
            config = new Config();
        }

        return config;
    }

    private static void setupStaticJavaParser(Config config) throws IOException {
        CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
        combinedSolver.add(new JavaParserTypeSolver(config.getSrcPath()));

        for (Path jarPath : config.getDependencies()) {
            combinedSolver.add(new JarTypeSolver(jarPath));
        }

        combinedSolver.add(new ReflectionTypeSolver());

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);

        StaticJavaParser.getParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21)
                .setSymbolResolver(symbolSolver);
    }

    private static UnitPrinter createUnitPrinter(Config config) {
        PackagesConfig packagesConfig = config.getPackages();
        ClassFilter classFilter = new ClassFilter(
                packagesConfig.include(), packagesConfig.exclude(),
                config.isInterfaces(), config.isAbstractClasses(), config.isSubClasses()
        );

        List<Printer> printers = new ArrayList<>();
        printers.add(new ClassTypePrinter());
        printers.add(new ClassNamePrinter());

        if (config.isGenerics()) {
            printers.add(new ClassGenericsPrinter());
        }

        printers.add(createClassBodyPrinter());
        printers.add(new ClassDependenciesPrinter(classFilter));
        printers.add(new ClassRelationsPrinter(classFilter));

        return new UnitPrinter(classFilter, printers);
    }

    private static ClassBodyPrinter createClassBodyPrinter() {
        return new ClassBodyPrinter(List.of(
                new FieldVisibilityPrinter(),
                new FieldStaticModifierPrinter(),
                new FieldTypePrinter(),
                new FieldNamePrinter()
        ), List.of(
                new MethodVisibilityPrinter(),
                new MethodModifierPrinter(),
                new MethodTypePrinter(),
                new MethodNamePrinter(),
                new MethodArgumentsPrinter(List.of(
                        new ParameterTypePrinter(),
                        new ParameterNamePrinter()
                ))
        ));
    }

}
