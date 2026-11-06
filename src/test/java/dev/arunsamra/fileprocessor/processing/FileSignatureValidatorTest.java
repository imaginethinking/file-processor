package dev.arunsamra.fileprocessor.processing;

import dev.arunsamra.fileprocessor.common.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileSignatureValidatorTest {

    @TempDir
    Path tempDirectory;

    private FileSignatureValidator validator;

    @BeforeEach()
    void setUp() {
        validator = new FileSignatureValidator();
    }

    @Test
    void shouldAcceptValidPdfSignature() throws IOException {

        Path file = tempDirectory.resolve("document.pdf");

        Files.write(
                file,
                new byte[]{0x25, 0x50, 0x44, 0x46, 0x2D}
        );

        assertDoesNotThrow(() -> validator.validate(List.of(file)));
    }

    @Test
    void shouldRejectInvalidPdfSignature() throws IOException {
        Path file = tempDirectory.resolve("document.pdf");

        Files.writeString(file, "This is not a PDF");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.validate(List.of(file))
        );

        assertEquals(
                List.of("document.pdf"),
                exception.getErrors().get("invalidFileSignatures")
        );
    }

    @Test
    void shouldAcceptValidJpegSignature() throws IOException {

        Path file = tempDirectory.resolve("image.jpeg");

        Files.write(
                file,
                new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}
        );

        assertDoesNotThrow(() -> validator.validate(List.of(file)));
    }

    @Test
    void shouldRejectInvalidJpegSignature() throws IOException {
        Path file = tempDirectory.resolve("image.jpeg");

        Files.write(
                file,
                new byte[]{(byte) 0xFA, (byte) 0xD1, (byte) 0x1B}
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.validate(List.of(file))
        );

        assertEquals(
                List.of("image.jpeg"),
                exception.getErrors().get("invalidFileSignatures")
        );
    }
}
