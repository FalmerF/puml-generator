package ru.ilug.puml_generator;

import com.fasterxml.jackson.databind.json.JsonMapper;
import ru.ilug.puml_generator.config.Config;

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
        PumlGenerator pumlGenerator = new PumlGenerator(config);
        pumlGenerator.run();
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
