package ru.ilug.puml_generator.file_system;

import lombok.RequiredArgsConstructor;
import ru.ilug.puml_generator.controller.OutputSaver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public class FileSystemOutputSaver implements OutputSaver {

    private final Path outputPath;

    @Override
    public void save(String pumlContent) throws IOException {
        deleteOutputFileIfExists();

        String fileFormatedContent = "@startuml\n%s\n@enduml".formatted(pumlContent);

        Files.writeString(outputPath, fileFormatedContent);
    }

    private void deleteOutputFileIfExists() {
        try {
            Files.deleteIfExists(outputPath);
        } catch (Exception e) {
            throw new RuntimeException("Error on delete existing output file: " + outputPath.toString(), e);
        }
    }
}
