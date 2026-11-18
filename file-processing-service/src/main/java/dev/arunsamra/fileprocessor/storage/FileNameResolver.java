package dev.arunsamra.fileprocessor.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
@Component
public class FileNameResolver {

    public Path resolve(Path requestedPath) {

        if (!Files.exists(requestedPath)) {
            return requestedPath;
        }

        int counter = 1;
        Path candidate;

        do {
            candidate = addSuffix(requestedPath, counter++);
        } while (Files.exists(candidate));

        return candidate;
    }

    public List<Path> resolveAll(List<Path> requestedPaths) {

        if (requestedPaths.isEmpty()) {
            return List.of();
        }

        if (requestedPaths.stream().noneMatch(Files::exists)) {
            return requestedPaths;
        }

        int counter = 1;

        while (true) {
            int suffix = counter++;

            List<Path> candidates = requestedPaths.stream()
                    .map(path -> addSuffix(path, suffix))
                    .toList();

            if (candidates.stream().noneMatch(Files::exists)) {
                return candidates;
            }
        }
    }

    private Path addSuffix(Path path, int suffix) {

        String fileName = path.getFileName().toString();
        String baseName = getBaseName(fileName);
        String extension = getExtension(fileName);

        return path.getParent()
                .resolve(baseName + "_" + suffix + extension);
    }

    private String getBaseName(String fileName) {

        int index = fileName.lastIndexOf('.');

        return index > 0
                ? fileName.substring(0, index)
                : fileName;
    }

    public String getExtension(String fileName) {

        int index = fileName.lastIndexOf('.');

        return index > 0
                ? fileName.substring(index)
                : "";
    }
}
