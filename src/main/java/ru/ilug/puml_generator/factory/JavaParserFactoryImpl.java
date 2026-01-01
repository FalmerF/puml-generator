package ru.ilug.puml_generator.factory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import lombok.RequiredArgsConstructor;
import ru.ilug.puml_generator.config.Config;

import java.io.IOException;
import java.nio.file.Path;

@RequiredArgsConstructor
public class JavaParserFactoryImpl implements JavaParserFactory {

    private final Config config;

    @Override
    public JavaParser create() throws IOException {
        CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
        combinedSolver.add(new JavaParserTypeSolver(config.getSrcPath()));

        for (Path jarPath : config.getDependencies()) {
            combinedSolver.add(new JarTypeSolver(jarPath));
        }

        combinedSolver.add(new ReflectionTypeSolver());

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);

        ParserConfiguration parserConfiguration = new ParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21)
                .setSymbolResolver(symbolSolver);

        return new JavaParser(parserConfiguration);
    }
}
