package ru.ilug.puml_generator.factory;

import com.github.javaparser.JavaParser;
import ru.ilug.puml_generator.config.Config;
import ru.ilug.puml_generator.generator.CompilationUnitLoader;
import ru.ilug.puml_generator.generator.CompilationUnitToPumlConverter;
import ru.ilug.puml_generator.generator.OutputSaver;
import ru.ilug.puml_generator.generator.PumlGenerator;
import ru.ilug.puml_generator.converter.CompilationUnitToPumlConverterImpl;
import ru.ilug.puml_generator.converter.JavaUnitParser;
import ru.ilug.puml_generator.file_system.FileSystemCompilationUnitLoader;
import ru.ilug.puml_generator.file_system.FileSystemOutputSaver;
import ru.ilug.puml_generator.parser.JavaUnitParserImpl;
import ru.ilug.puml_generator.parser.printer.Printer;

import java.io.IOException;

public class PumlGeneratorFactory {

    private final Config config;
    private final JavaParser javaParser;

    public PumlGeneratorFactory(Config config) throws IOException {
        this(config, new JavaParserFactoryImpl(config));
    }

    public PumlGeneratorFactory(Config config, JavaParserFactory javaParserFactory) throws IOException {
        this.config = config;
        this.javaParser = javaParserFactory.create();
    }

    public PumlGenerator create() {
        CompilationUnitLoader compilationUnitLoader = createCompilationUnitLoader();
        OutputSaver outputSaver = createOutputSaver();

        PrinterFactory printerFactory = createPrinterFactory();
        Printer basePrinter = printerFactory.createBasePrinter();
        JavaUnitParser javaUnitParser = createJavaUnitParser(basePrinter);
        CompilationUnitToPumlConverter converter = createCompilationUnitToPumlConverter(javaUnitParser);

        return new PumlGenerator(converter, compilationUnitLoader, outputSaver);
    }

    protected CompilationUnitLoader createCompilationUnitLoader() {
        return new FileSystemCompilationUnitLoader(config.getSrcPath(), javaParser);
    }

    protected OutputSaver createOutputSaver() {
        return new FileSystemOutputSaver(config.getOutputFile());
    }

    protected PrinterFactory createPrinterFactory() {
        return new UnitPrinterFactory(config, javaParser);
    }

    protected JavaUnitParser createJavaUnitParser(Printer basePrinter) {
        return new JavaUnitParserImpl(basePrinter);
    }

    protected CompilationUnitToPumlConverter createCompilationUnitToPumlConverter(JavaUnitParser javaUnitParser) {
        return new CompilationUnitToPumlConverterImpl(javaUnitParser);
    }
}
