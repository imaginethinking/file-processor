package dev.arunsamra.fileprocessor.processing;

import dev.arunsamra.fileprocessor.archive.ZipContentValidator;
import dev.arunsamra.fileprocessor.archive.ZipCreator;
import dev.arunsamra.fileprocessor.archive.ZipExtractor;
import dev.arunsamra.fileprocessor.archive.ZipInspector;
import dev.arunsamra.fileprocessor.common.exception.ServiceValidationException;
import dev.arunsamra.fileprocessor.common.exception.ZipFileException;
import dev.arunsamra.fileprocessor.common.type.FileType;
import dev.arunsamra.fileprocessor.config.AllowedFileTypesProperties;
import dev.arunsamra.fileprocessor.encoding.Base64Encoder;
import dev.arunsamra.fileprocessor.processing.dto.ProcessingRequest;
import dev.arunsamra.fileprocessor.processing.dto.ProcessingResultResponse;
import dev.arunsamra.fileprocessor.storage.TempDirectoryCleaner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProcessingService {

    private final AllowedFileTypesProperties allowedFileTypesProperties;

    private final InputFileValidator inputFileValidator;
    private final ZipInspector zipInspector;
    private final ZipContentValidator zipContentValidator;
    private final ZipExtractor zipExtractor;
    private final ZipCreator zipCreator;
    private final TempDirectoryCleaner tempDirectoryCleaner;
    private final Base64Encoder base64Encoder;

    public ProcessingResultResponse process(ProcessingRequest request) {

        log.info("Starting processing for file: {}", request.getFileName());

        // Validate request
        validateRequest(request);
        Path inputFile = inputFileValidator.validate(request.getFileName());

        // Validate zip contents
        List<ZipEntry> entries = zipInspector.inspect(inputFile);
        zipContentValidator.validate(entries);


        Path tempDirectory = null;

        try {
            tempDirectory = Files.createTempDirectory("file-processor-");

            Set<FileType> includedFileTypes =
                    request.getIncludeFileTypes() == null
                            ? allowedFileTypesProperties.allowedFileTypes()
                            : request.getIncludeFileTypes();

            List<Path> extractedFiles = zipExtractor.extract(
                    inputFile,
                    tempDirectory,
                    includedFileTypes
            );

            Path processedZip = zipCreator.create(
                    inputFile,
                    extractedFiles
            );

            Path encodedFile = base64Encoder.encode(processedZip);

            log.info("Created processed ZIP: {}", processedZip);

        } catch (IOException e) {
            throw new ZipFileException("Failed to create temporary directory", e);

        } finally {
            tempDirectoryCleaner.delete(tempDirectory);
        }



        return null;
    }

    private void validateRequest(ProcessingRequest request) {

        if (request.getIncludeFileTypes() == null) {
            return;
        }

        List<String> invalidFileTypes = request.getIncludeFileTypes().stream()
                .filter(fileType -> !allowedFileTypesProperties.allowedFileTypes().contains(fileType))
                .map(FileType::getExtension)
                .toList();

        if (!invalidFileTypes.isEmpty()) {
            Map<String, List<String>> errors = new HashMap<>();
            errors.put("invalidFileTypes", invalidFileTypes);
            throw new ServiceValidationException("Invalid file types in request", errors);
        }
    }
}
