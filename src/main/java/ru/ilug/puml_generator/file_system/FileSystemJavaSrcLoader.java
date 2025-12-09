package ru.ilug.puml_generator.file_system;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import lombok.RequiredArgsConstructor;
import ru.ilug.puml_generator.controller.JavaSrcLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class FileSystemJavaSrcLoader implements JavaSrcLoader {

    private final Path srcPath;

    @Override
    public List<CompilationUnit> load() throws IOException {
        List<Path> javaFiles = findAllJavaSourceFiles();
        List<CompilationUnit> units = new ArrayList<>();

        for(Path path : javaFiles) {
            CompilationUnit unit = parseFile(path);
            units.add(unit);
        }

        return units;
    }

    private List<Path> findAllJavaSourceFiles() throws IOException {
        try (Stream<Path> pathStream = Files.walk(srcPath)) {
            return pathStream.filter(this::isPathMatched).toList();
        }
    }

    private boolean isPathMatched(Path path) {
        File file = path.toFile();
        return file.isFile() && file.getName().endsWith(".java");
    }

    private CompilationUnit parseFile(Path filePath) {
        try {
            return StaticJavaParser.parse(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Error on parse file: " + filePath.toString(), e);
        }
    }
}
