package dev.arunsamra.fileprocessor.archive;

import dev.arunsamra.fileprocessor.common.exception.ValidationException;
import dev.arunsamra.fileprocessor.common.type.FileType;
import dev.arunsamra.fileprocessor.config.AllowedFileTypesProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;

import static org.junit.jupiter.api.Assertions.*;

public class ZipContentValidatorTest {

    private ZipContentValidator zipContentValidator;

    @BeforeEach
    void setup() {
        AllowedFileTypesProperties properties = new AllowedFileTypesProperties(Set.of(FileType.PDF, FileType.JPEG, FileType.JPG));
        zipContentValidator = new ZipContentValidator(properties);
    }

    @Test
    void shouldAcceptPdfFile() {
        ZipEntry entry = new ZipEntry("document.pdf");

        assertDoesNotThrow(() -> zipContentValidator.validate(List.of(entry)));
    }

    @Test
    void shouldAcceptJpegFiles() {
        List<ZipEntry> entries = List.of(
                new ZipEntry("image.jpg"),
                new ZipEntry("image.jpeg")
        );

        assertDoesNotThrow(() -> zipContentValidator.validate(entries));
    }

    @Test
    void shouldRejectUnsupportedFileType() {
        ZipEntry entry = new ZipEntry("notes.txt");


        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> zipContentValidator.validate(List.of(entry))
        );

        assertEquals(
                List.of("notes.txt"),
                exception.getErrors().get("unsupportedFiles")
        );
    }

    @Test
    void shouldRejectDirectory() {
        ZipEntry entry = new ZipEntry("documents/");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> zipContentValidator.validate(List.of(entry)));

        assertEquals(
                List.of("documents/"),
                exception.getErrors().get("subdirectories")
        );
    }

    @Test
    void shouldRejectFileInsideSubdirectory() {
        ZipEntry entry = new ZipEntry("documents/file.pdf");

        assertThrows(
                ValidationException.class,
                () -> zipContentValidator.validate(List.of(entry))
        );
    }

    @Test
    void shouldReportAllInvalidEntries() {
        List<ZipEntry> entries = List.of(
                new ZipEntry("valid.pdf"),
                new ZipEntry("notes.txt"),
                new ZipEntry("data.csv"),
                new ZipEntry("images/photo.jpg")
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> zipContentValidator.validate(entries)
        );

        assertEquals(
                List.of("notes.txt", "data.csv"),
                exception.getErrors().get("unsupportedFiles")
        );

        assertEquals(
                List.of("images/photo.jpg"),
                exception.getErrors().get("subdirectories")
        );
    }
}
