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

        printers.add(createClassBodyPrinter(config));
        printers.add(new ClassDependenciesPrinter(classFilter));
        printers.add(new ClassRelationsPrinter(classFilter));

        return new UnitPrinter(classFilter, printers);
    }

    private static ClassBodyPrinter createClassBodyPrinter(Config config) {
        return new ClassBodyPrinter(
                config.isFields(), config.isPublicFields(), config.isPrivateFields(), config.isProtectedFields(), config.isStaticFields(),
                config.isMethods(), config.isPublicMethods(), config.isPrivateMethods(), config.isProtectedMethods(), config.isStaticMethods(), config.isAbstractMethods(),
                createFieldPrinters(config), createMethodPrinters(config)
        );
    }

    private static List<Printer> createFieldPrinters(Config config) {
        List<Printer> printers = new ArrayList<>();

        if (config.isFieldVisibility()) {
            printers.add(new FieldVisibilityPrinter());
        }

        printers.add(new FieldStaticModifierPrinter());

        if (config.isFieldType()) {
            printers.add(new FieldTypePrinter());
        }

        if (config.isFieldName()) {
            printers.add(new FieldNamePrinter());
        }

        return printers;
    }

    private static List<Printer> createMethodPrinters(Config config) {
        List<Printer> printers = new ArrayList<>();

        if (config.isMethodVisibility()) {
            printers.add(new MethodVisibilityPrinter());
        }
        printers.add(new MethodModifierPrinter());

        if (config.isMethodType()) {
            printers.add(new MethodTypePrinter());
        }

        if (config.isMethodName()) {
            printers.add(new MethodNamePrinter());
        }

        List<Printer> methodArgPrinters = new ArrayList<>();

        if (config.isMethodArgs()) {
            if (config.isMethodArgsType()) {
                methodArgPrinters.add(new ParameterTypePrinter());
            }

            if (config.isMethodArgsName()) {
                methodArgPrinters.add(new ParameterNamePrinter());
            }
        }

        printers.add(new MethodArgumentsPrinter(methodArgPrinters));

        return printers;
    }

}
