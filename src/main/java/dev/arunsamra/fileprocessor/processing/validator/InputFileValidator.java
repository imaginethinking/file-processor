package dev.arunsamra.fileprocessor.processing.validator;

import dev.arunsamra.fileprocessor.common.exception.BadRequestException;
import dev.arunsamra.fileprocessor.common.type.FileType;
import dev.arunsamra.fileprocessor.config.FileProcessorPathProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

@RequiredArgsConstructor
@Component
public class InputFileValidator {

    private final FileProcessorPathProperties properties;

    public Path validate(String fileName, FileType fileType) {

        Path inputDirectory = properties.inputDirectory()
                .toAbsolutePath()
                .normalize();

        Path inputFile = inputDirectory
                .resolve(fileName)
                .normalize();

        if (!inputFile.startsWith(inputDirectory)) {
            throw new BadRequestException("Invalid input file path: " + fileName);
        }

        if (!Files.isRegularFile(inputFile)) {
            throw new BadRequestException("Input file does not exist: " + fileName);
        }

        if (!fileName.toLowerCase(Locale.ROOT).endsWith(fileType.getExtension())) {
            throw new BadRequestException("Input file must be a " + fileType + " file: " + fileName);
        }

        return inputFile;
    }
}
