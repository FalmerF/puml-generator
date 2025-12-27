package ru.ilug.puml_generator;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import ru.ilug.puml_generator.config.Config;
import ru.ilug.puml_generator.controller.CompilationUnitToPumlConverter;
import ru.ilug.puml_generator.controller.PumlGenerateController;
import ru.ilug.puml_generator.converter.CompilationUnitToPumlConverterImpl;
import ru.ilug.puml_generator.converter.JavaUnitParser;
import ru.ilug.puml_generator.file_system.FileSystemJavaSrcLoader;
import ru.ilug.puml_generator.file_system.FileSystemOutputSaver;
import ru.ilug.puml_generator.parser.JavaUnitParserImpl;
import ru.ilug.puml_generator.parser.printer.DefaultPrinters;
import ru.ilug.puml_generator.parser.printer.UnitPrinter;

import java.io.IOException;
import java.nio.file.Path;

public class PumlGenerator implements Runnable {

    private final PumlGenerateController generateController;

    public PumlGenerator(Config config) throws IOException {
        this(new FileSystemJavaSrcLoader(config.getSrcPath()), new FileSystemOutputSaver(config.getOutputFile()), config);
    }

    public PumlGenerator(FileSystemJavaSrcLoader loader, FileSystemOutputSaver saver, Config config) throws IOException {
        this(loader, saver, DefaultPrinters.createUnitPrinter(config), config);
    }

    public PumlGenerator(FileSystemJavaSrcLoader loader, FileSystemOutputSaver saver,
                         UnitPrinter printer , Config config) throws IOException {
        this(loader, saver, new JavaUnitParserImpl(printer), config);
    }

    public PumlGenerator(FileSystemJavaSrcLoader loader, FileSystemOutputSaver saver,
                         JavaUnitParser parser, Config config) throws IOException {
        this(loader, saver, new CompilationUnitToPumlConverterImpl(parser), config);
    }

    public PumlGenerator(FileSystemJavaSrcLoader loader, FileSystemOutputSaver saver,
                         CompilationUnitToPumlConverter compilationUnitToPumlConverter, Config config) throws IOException {
        this(new PumlGenerateController(compilationUnitToPumlConverter, loader, saver), config);
    }

    public PumlGenerator(PumlGenerateController generateController, Config config) throws IOException {
        this.generateController = generateController;
        setupStaticJavaParser(config);
    }

    private void setupStaticJavaParser(Config config) throws IOException {
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

    public void run() {
        generateController.generate();
    }
}
