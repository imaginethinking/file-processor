package dev.arunsamra.fileprocessor.archive;

import dev.arunsamra.fileprocessor.common.type.FileType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class ZipExtractorTest {

    @TempDir
    Path tempDirectory;

    private final ZipExtractor zipExtractor = new ZipExtractor();

    @Test
    void shouldExtractRequestedFiles() throws IOException {
        Path zipFile = tempDirectory.resolve("documents.zip");

        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            addFile(zip, "document.pdf", "pdf content");
            addFile(zip, "image.jpg", "jpg content");
        }

        Path extractionDirectory = tempDirectory.resolve("extracted");

        List<Path> extractedFiles = zipExtractor.extract(
                zipFile,
                extractionDirectory,
                Set.of(FileType.PDF, FileType.JPG)
        );

        assertEquals(2, extractedFiles.size());

        assertTrue(Files.exists(extractionDirectory.resolve("document.pdf")));
        assertTrue(Files.exists(extractionDirectory.resolve("image.jpg")));
    }

    @Test
    void shouldExtractFilesWithRequestedType() throws IOException {
        Path zipFile = tempDirectory.resolve("documents.zip");

        try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            addFile(zip, "document_1.pdf", "pdf content");
            addFile(zip, "document_2.pdf", "pdf content");
            addFile(zip, "document_3.pdf", "pdf content");
            addFile(zip, "image.jpg", "jpg content");
        }

        Path extractionDirectory = tempDirectory.resolve("extracted");

        List<Path> extractedFiles = zipExtractor.extract(
                zipFile,
                extractionDirectory,
                Set.of(FileType.PDF)
        );

        assertEquals(3, extractedFiles.size());

        assertTrue(Files.exists(extractionDirectory.resolve("document_1.pdf")));
        assertTrue(Files.exists(extractionDirectory.resolve("document_2.pdf")));
        assertTrue(Files.exists(extractionDirectory.resolve("document_3.pdf")));
        assertFalse(Files.exists(extractionDirectory.resolve("image.jpg")));
    }

    private void addFile(ZipOutputStream zip, String fileName, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(fileName));
        zip.write(content.getBytes());
        zip.closeEntry();
    }
}
