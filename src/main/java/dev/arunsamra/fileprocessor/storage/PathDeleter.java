package dev.arunsamra.fileprocessor.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;


@Slf4j
@Component
public class PathDeleter {

    public void delete(Path path) {

        if (path == null || !Files.exists(path)) {
            return;
        }

        try {
            if (Files.isDirectory(path)) {
                deleteDirectory(path);
            } else {
                Files.deleteIfExists(path);
            }
        } catch (IOException exception) {
            log.warn("Failed to delete path: {}", path, exception);
        }
    }

    private void deleteDirectory(Path directory) throws IOException {

        try (Stream<Path> paths = Files.walk(directory)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(this::deletePath);
        }
    }

    private void deletePath(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException exception) {
            log.warn("Failed to delete path: {}", path, exception);
        }
    }
}

