package ru.ilug.puml_generator;

import ru.ilug.puml_generator.config.Config;
import ru.ilug.puml_generator.config.ConfigLoader;
import ru.ilug.puml_generator.controller.PumlGenerator;
import ru.ilug.puml_generator.factory.*;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Config config = ConfigLoader.getConfigFromArgs(args);

        PumlGeneratorFactory pumlGeneratorFactory = new PumlGeneratorFactory(config);
        PumlGenerator pumlGenerator = pumlGeneratorFactory.create();
        pumlGenerator.generate();
    }

}
