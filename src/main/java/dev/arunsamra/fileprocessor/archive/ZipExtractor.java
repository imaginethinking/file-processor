package dev.arunsamra.fileprocessor.archive;

import dev.arunsamra.fileprocessor.common.exception.ZipFileException;
import dev.arunsamra.fileprocessor.common.type.FileType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
public class ZipExtractor {
    public List<Path> extract(
            Path zipPath,
            Path tempDirectory,
            Set<FileType> includeFileTypes) {

        try {
            Files.createDirectories(tempDirectory);

            try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {

                return zipFile.stream()
                        .filter(entry -> shouldExtract(entry, includeFileTypes))
                        .map(entry -> extractEntry(
                                zipFile,
                                entry,
                                tempDirectory))
                        .toList();
            }

        } catch (IOException e) {
            throw new ZipFileException(
                    "Failed to extract ZIP: " + zipPath.getFileName(),
                    e);
        }
    }

    private boolean shouldExtract(ZipEntry entry, Set<FileType> includeFileTypes) {

        if (entry.isDirectory()) {
            return false;
        }

        return FileType.fromFileName(entry.getName())
                .map(includeFileTypes::contains)
                .orElse(false);
    }

    private Path extractEntry(
            ZipFile zipFile,
            ZipEntry entry,
            Path tempDirectory) {

        Path outputPath = tempDirectory
                .resolve(entry.getName())
                .normalize();

        try (InputStream inputStream = zipFile.getInputStream(entry)) {

            Files.copy(inputStream, outputPath);

            return outputPath;

        } catch (IOException e) {
            throw new ZipFileException(
                    "Failed to extract file: " + entry.getName(),
                    e);
        }
    }
}
