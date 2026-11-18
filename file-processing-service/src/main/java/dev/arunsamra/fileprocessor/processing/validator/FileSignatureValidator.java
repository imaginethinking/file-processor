package dev.arunsamra.fileprocessor.processing.validator;

import dev.arunsamra.fileprocessor.common.exception.FileProcessingException;
import dev.arunsamra.fileprocessor.common.exception.ValidationException;
import dev.arunsamra.fileprocessor.common.type.FileType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class FileSignatureValidator {
    public void validate(List<Path> files) {

        List<String> invalidFiles = files.stream()
                .filter(file -> !hasValidSignature(file))
                .map(file -> file.getFileName().toString())
                .toList();

        if (!invalidFiles.isEmpty()) {
            throw new ValidationException(
                    "Invalid file signatures",
                    Map.of("invalidFileSignatures", invalidFiles)
            );
        }
    }

    private boolean hasValidSignature(Path file) {

        FileType fileType = FileType.fromFileName(
                file.getFileName().toString()
        ).orElse(null);

        if (fileType == null) {
            return false;
        }

        List<byte[]> signatures = fileType.getSignatures();

        // No defined signature means no signature validation available
        if (signatures.isEmpty()) {
            return true;
        }

        int maxSignatureLength = signatures.stream()
                .mapToInt(signature -> signature.length)
                .max()
                .orElse(0);

        try (InputStream inputStream = Files.newInputStream(file)) {

            byte[] header = inputStream.readNBytes(maxSignatureLength);

            return signatures.stream()
                    .anyMatch(signature -> startsWith(header, signature));

        } catch (IOException exception) {
            throw new FileProcessingException(
                    "Failed to read file for signature validation",
                    exception
            );
        }
    }

    private boolean startsWith(byte[] content, byte[] signature) {

        if (content.length < signature.length) {
            return false;
        }

        return Arrays.equals(
                Arrays.copyOf(content, signature.length),
                signature
        );
    }
}
