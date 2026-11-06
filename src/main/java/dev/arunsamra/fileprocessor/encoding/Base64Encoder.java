package dev.arunsamra.fileprocessor.encoding;

import dev.arunsamra.fileprocessor.common.exception.FileProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@RequiredArgsConstructor
@Component
public class Base64Encoder {
    public Path encode(Path inputFile, Path outputFile) {

        try {
            Files.createDirectories(outputFile.getParent());

            try (
                    InputStream inputStream = Files.newInputStream(inputFile);
                    OutputStream outputStream = Files.newOutputStream(outputFile);
                    OutputStream base64Stream = Base64.getEncoder().wrap(outputStream)
            ) {
                inputStream.transferTo(base64Stream);
            }

            return outputFile;

        } catch (IOException exception) {
            throw new FileProcessingException(
                    "Failed to Base64 encode file",
                    exception
            );
        }
    }
}
