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
public class TempDirectoryCleaner {

    public void delete(Path tempDirectory) {

        if (tempDirectory == null || !Files.exists(tempDirectory)) {
            return;
        }

        try (Stream<Path> paths = Files.walk(tempDirectory)) {

            paths.sorted(Comparator.reverseOrder())
                    .forEach(this::deletePath);

        } catch (IOException exception) {
            log.warn(
                    "Failed to clean temporary directory: {}",
                    tempDirectory,
                    exception
            );
        }
    }

    private void deletePath(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException exception) {
            log.warn(
                    "Failed to delete temporary path: {}",
                    path,
                    exception
            );
        }
    }
}

