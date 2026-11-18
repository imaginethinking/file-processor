package dev.arunsamra.fileprocessor.storage;

import dev.arunsamra.fileprocessor.common.exception.FileProcessingException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FileRelocator {
    public Path move(Path source, Path destination) {

        try {
            Files.createDirectories(destination.getParent());

            return Files.move(source, destination);

        } catch (IOException exception) {
            throw new FileProcessingException(
                    "Failed to move file: " + source.getFileName(),
                    exception
            );
        }
    }
}
