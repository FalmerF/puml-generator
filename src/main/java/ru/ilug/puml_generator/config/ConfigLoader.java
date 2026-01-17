package ru.ilug.puml_generator.config;

import ru.ilug.puml_generator.error.PumlGeneratorConfigFormatException;
import ru.ilug.puml_generator.error.PumlGeneratorConfigLoadException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConfigLoader {

    public static final String DEFAULT_CONFIG_NAME = "pumlg-config";
    public static final Collection<ConfigParser> CONFIG_PARSERS = new LinkedList<>(List.of(
            new JsonConfigParser(),
            new YamlConfigParser()
    ));

    public static Config getConfigFromArgs(String[] args) {
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

    public static Config loadConfig(String path) {
        try {
            if (path == null) {
                return loadDefaultConfig();
            }

            for (ConfigParser parser : CONFIG_PARSERS) {
                if (parser.isMatchedFile(path)) {
                    return parser.load(Path.of(path));
                }
            }

            throw new PumlGeneratorConfigFormatException(path);
        } catch (IOException e) {
            throw new PumlGeneratorConfigLoadException(path, e);
        }
    }

    public static Config loadDefaultConfig() throws IOException {
        for (ConfigParser parser : CONFIG_PARSERS) {
            String[] files = parser.getDefaultFiles(DEFAULT_CONFIG_NAME);

            for (String fileName : files) {
                Path path = Path.of("./" + fileName);
                if (Files.exists(path)) {
                    return parser.load(path);
                }
            }
        }

        return new Config();
    }

}
