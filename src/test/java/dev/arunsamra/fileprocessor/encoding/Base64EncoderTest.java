package dev.arunsamra.fileprocessor.encoding;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class Base64EncoderTest {

    @TempDir
    Path tempDirectory;

    private final Base64Encoder base64Encoder = new Base64Encoder();

    @Test
    void shouldBase64EncodeFile() throws IOException {
        Path inputFile = tempDirectory.resolve("documents_processed.zip");
        Path outputFile = tempDirectory.resolve("documents_processed_base64.txt");

        byte[] originalContent = "test zip content".getBytes();
        Files.write(inputFile, originalContent);

        Path encodedFile = base64Encoder.encode(inputFile, outputFile);

        assertEquals(outputFile, encodedFile);
        assertTrue(Files.exists(encodedFile));

        byte[] encodedContent = Files.readAllBytes(encodedFile);
        byte[] decodedContent = Base64.getDecoder().decode(encodedContent);

        assertArrayEquals(originalContent, decodedContent);
    }

}
