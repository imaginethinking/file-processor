package dev.arunsamra.fileprocessor.encoding;

import dev.arunsamra.fileprocessor.common.exception.ZipFileException;
import dev.arunsamra.fileprocessor.config.FileProcessorPathProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@RequiredArgsConstructor
@Component
public class Base64Encoder {
    private final FileProcessorPathProperties properties;

    public Path encode(Path zipFile) {

        try {
            Files.createDirectories(properties.encodedDirectory());

            byte[] zipBytes = Files.readAllBytes(zipFile);
            byte[] encodedBytes = Base64.getEncoder().encode(zipBytes);

            Path outputFile = getOutputPath(zipFile);

            Files.write(outputFile, encodedBytes);

            return outputFile;

        } catch (IOException exception) {
            throw new ZipFileException(
                    "Failed to Base64 encode processed ZIP",
                    exception
            );
        }
    }

    private Path getOutputPath(Path zipFile) {

        String fileName = zipFile.getFileName().toString();

        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));

        return properties.encodedDirectory()
                .resolve(baseName + "_base64.txt");
    }
}
