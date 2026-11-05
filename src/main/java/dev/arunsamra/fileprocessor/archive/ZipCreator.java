package dev.arunsamra.fileprocessor.archive;

import dev.arunsamra.fileprocessor.common.exception.ZipFileException;
import dev.arunsamra.fileprocessor.config.FileProcessorPathProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RequiredArgsConstructor
@Component
public class ZipCreator {
    private final FileProcessorPathProperties properties;

    public Path create(Path sourceZip, List<Path> files) {

        try {
            Files.createDirectories(properties.processedDirectory());

            Path outputZip = getAvailableOutputPath(sourceZip);

            try (OutputStream outputStream = Files.newOutputStream(outputZip);
                 ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {

                for (Path file : files) {
                    addFile(zipOutputStream, file);
                }
            }

            return outputZip;

        } catch (IOException exception) {
            throw new ZipFileException(
                    "Failed to create processed ZIP",
                    exception
            );
        }
    }

    private void addFile(ZipOutputStream zipOutputStream, Path file) throws IOException {

        ZipEntry entry = new ZipEntry(file.getFileName().toString());

        zipOutputStream.putNextEntry(entry);
        Files.copy(file, zipOutputStream);
        zipOutputStream.closeEntry();
    }

    private Path getAvailableOutputPath(Path sourceZip) {

        String fileName = sourceZip.getFileName().toString();

        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));

        Path output = properties.processedDirectory()
                .resolve(baseName + "_processed.zip");

        if (!Files.exists(output)) {
            return output;
        }

        int counter = 1;

        while (true) {
            Path candidate = properties.processedDirectory()
                    .resolve(baseName + "_processed_" + counter + ".zip");

            if (!Files.exists(candidate)) {
                return candidate;
            }

            counter++;
        }
    }
}
