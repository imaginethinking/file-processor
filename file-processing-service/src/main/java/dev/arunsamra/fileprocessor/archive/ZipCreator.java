package dev.arunsamra.fileprocessor.archive;

import dev.arunsamra.fileprocessor.common.exception.FileProcessingException;
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

    public Path create(Path outputZip, List<Path> files) {

        try {
            Files.createDirectories(outputZip.getParent());

            try (
                    OutputStream outputStream = Files.newOutputStream(outputZip);
                    ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)
            ) {
                for (Path file : files) {
                    addFile(zipOutputStream, file);
                }
            }

            return outputZip;

        } catch (IOException exception) {
            throw new FileProcessingException(
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
}
