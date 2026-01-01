package ru.ilug.puml_generator;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.javaparser.JavaParser;
import ru.ilug.puml_generator.config.Config;
import ru.ilug.puml_generator.controller.CompilationUnitLoader;
import ru.ilug.puml_generator.controller.CompilationUnitToPumlConverter;
import ru.ilug.puml_generator.controller.OutputSaver;
import ru.ilug.puml_generator.controller.PumlGenerator;
import ru.ilug.puml_generator.converter.CompilationUnitToPumlConverterImpl;
import ru.ilug.puml_generator.converter.JavaUnitParser;
import ru.ilug.puml_generator.factory.*;
import ru.ilug.puml_generator.file_system.FileSystemCompilationUnitLoader;
import ru.ilug.puml_generator.file_system.FileSystemOutputSaver;
import ru.ilug.puml_generator.parser.JavaUnitParserImpl;
import ru.ilug.puml_generator.parser.printer.Printer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) throws IOException {
        // TODO: Move config parsing to special class
        Config config = parseArgs(args);

        JavaParserFactory javaParserFactory = new JavaParserFactoryImpl(config);
        JavaParser javaParser = javaParserFactory.create();

        PumlGenerator pumlGenerator = createPumlGenerator(config, javaParser);
        pumlGenerator.generate();
    }

    private static PumlGenerator createPumlGenerator(Config config, JavaParser javaParser) {
        CompilationUnitLoader compilationUnitLoader = new FileSystemCompilationUnitLoader(config.getSrcPath(), javaParser);
        OutputSaver outputSaver = new FileSystemOutputSaver(config.getOutputFile());

        PrinterFactory printerFactory = new UnitPrinterFactory(config, javaParser);
        Printer basePrinter = printerFactory.createBasePrinter();
        JavaUnitParser javaUnitParser = new JavaUnitParserImpl(basePrinter);
        CompilationUnitToPumlConverter converter = new CompilationUnitToPumlConverterImpl(javaUnitParser);

        return new PumlGenerator(converter, compilationUnitLoader, outputSaver);
    }

    private static Config parseArgs(String[] args) throws IOException {
        Map<String, String> argsMap = IntStream.range(0, args.length / 2)
                .boxed()
                .collect(Collectors.toMap(
                        i -> parseParameterName(args[2 * i]),
                        i -> args[2 * i + 1]
                ));

        String configPath = argsMap.get("config");

        return loadConfig(configPath);
    }

    private static String parseParameterName(String input) {
        if ("-config".equals(input) || "-c".equals(input)) {
            return "config";
        }

        return input;
    }

    private static Config loadConfig(String path) throws IOException {
        JsonMapper jsonMapper = new JsonMapper();
        Path configPath = path == null ? Path.of("./pumlg-config.json") : Path.of(path);

        Config config;

        if (Files.exists(configPath)) {
            config = jsonMapper.readValue(configPath.toFile(), Config.class);
        } else {
            config = new Config();
        }

        return config;
    }

}
