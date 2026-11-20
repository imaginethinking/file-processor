package dev.arunsamra.fileprocessor.storage;

import dev.arunsamra.common.rest.exception.BadRequestException;
import dev.arunsamra.fileprocessor.common.type.FileType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

@RequiredArgsConstructor
@Component
public class FileValidator {

    public Path validate(
            Path directory,
            String fileName,
            FileType fileType) {

        Path normalizedDirectory = directory.toAbsolutePath().normalize();
        Path file = normalizedDirectory.resolve(fileName).normalize();

        if (!file.startsWith(normalizedDirectory)) {
            throw new BadRequestException("Invalid file path: " + fileName);
        }

        if (!Files.isRegularFile(file)) {
            throw new BadRequestException("File does not exist: " + fileName);
        }

        if (!fileName.toLowerCase(Locale.ROOT).endsWith(fileType.getExtension())) {
            throw new BadRequestException("File must be a " + fileType + " file: " + fileName);
        }

        return file;
    }
}
