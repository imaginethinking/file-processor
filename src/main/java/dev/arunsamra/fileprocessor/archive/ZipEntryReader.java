package dev.arunsamra.fileprocessor.archive;

import dev.arunsamra.fileprocessor.common.exception.FileProcessingException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
public class ZipEntryReader {

    public <T> T read(
            Path zipPath,
            String entryName,
            Function<InputStream, T> reader) {

        try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {

            ZipEntry entry = zipFile.getEntry(entryName);

            if (entry == null || entry.isDirectory()) {
                throw new FileProcessingException(
                        "ZIP entry not found: " + entryName,
                        null
                );
            }

            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                return reader.apply(inputStream);
            }

        } catch (IOException exception) {
            throw new FileProcessingException(
                    "Failed to read ZIP: " + zipPath.getFileName(),
                    exception
            );
        }
    }
}
