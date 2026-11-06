package dev.arunsamra.fileprocessor.archive;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.*;

public class ZipCreatorTest {

    @TempDir
    Path tempDirectory;

    private final ZipCreator zipCreator = new ZipCreator();

    @Test
    void shouldCreateZipContainingFiles() throws IOException {
        Path pdf = tempDirectory.resolve("document.pdf");
        Path jpeg = tempDirectory.resolve("image.jpeg");

        Files.writeString(pdf, "pdf content");
        Files.writeString(jpeg, "jpeg content");

        Path outputZip = tempDirectory.resolve("documents_processed.zip");

        Path result = zipCreator.create(
                outputZip,
                List.of(pdf, jpeg)
        );

        assertEquals(outputZip, result);
        assertTrue(Files.exists(result));

        try (ZipFile zipFile = new ZipFile(result.toFile())) {
            assertNotNull(zipFile.getEntry("document.pdf"));
            assertNotNull(zipFile.getEntry("image.jpeg"));

            assertEquals(2, zipFile.size());
        }
    }
}
