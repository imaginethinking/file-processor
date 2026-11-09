package dev.arunsamra.fileprocessor.processing;

import dev.arunsamra.fileprocessor.archive.ZipContentValidator;
import dev.arunsamra.fileprocessor.archive.ZipCreator;
import dev.arunsamra.fileprocessor.archive.ZipExtractor;
import dev.arunsamra.fileprocessor.archive.ZipInspector;
import dev.arunsamra.fileprocessor.common.exception.FileProcessingException;
import dev.arunsamra.fileprocessor.common.exception.RestException;
import dev.arunsamra.fileprocessor.common.exception.ValidationException;
import dev.arunsamra.fileprocessor.common.type.FileType;
import dev.arunsamra.fileprocessor.config.AllowedFileTypesProperties;
import dev.arunsamra.fileprocessor.config.FileProcessorPathProperties;
import dev.arunsamra.fileprocessor.encoding.Base64Encoder;
import dev.arunsamra.fileprocessor.processing.dto.InputFilesQueryParams;
import dev.arunsamra.fileprocessor.processing.dto.InputFilesResponse;
import dev.arunsamra.fileprocessor.processing.dto.ProcessingRequest;
import dev.arunsamra.fileprocessor.processing.dto.ProcessingResultResponse;
import dev.arunsamra.fileprocessor.storage.FileNameResolver;
import dev.arunsamra.fileprocessor.storage.FileRelocator;
import dev.arunsamra.fileprocessor.storage.PathDeleter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProcessingService {

    private final AllowedFileTypesProperties allowedFileTypesProperties;
    private final FileProcessorPathProperties pathProperties;

    private final InputFileValidator inputFileValidator;
    private final ZipInspector zipInspector;
    private final ZipContentValidator zipContentValidator;
    private final ZipExtractor zipExtractor;
    private final FileSignatureValidator fileSignatureValidator;
    private final ZipCreator zipCreator;
    private final Base64Encoder base64Encoder;

    private final FileNameResolver fileNameResolver;
    private final FileRelocator fileRelocator;
    private final PathDeleter pathDeleter;

    public ProcessingResultResponse process(ProcessingRequest request) {

        log.info("Starting processing for file: {}", request.getFileName());

        validateRequest(request);

        Path inputFile = inputFileValidator.validate(request.getFileName(), FileType.ZIP);

        Path tempDirectory = null;
        Path processedDestination = null;
        Path encodedDestination = null;

        try {
            List<ZipEntry> entries = zipInspector.inspect(inputFile);
            zipContentValidator.validate(entries);

            tempDirectory = Files.createTempDirectory("file-processor-");

            Set<FileType> includedFileTypes = request.getFileTypes() == null
                            ? allowedFileTypesProperties.allowedFileTypes()
                            : request.getFileTypes();

            List<Path> extractedFiles = zipExtractor.extract(
                    inputFile,
                    tempDirectory,
                    includedFileTypes
            );

            fileSignatureValidator.validate(extractedFiles);

            String sourceFileName = inputFile.getFileName().toString();

            String baseName = getBaseName(sourceFileName);

            List<Path> outputPaths = fileNameResolver.resolveAll(
                    List.of(pathProperties.processedDirectory().resolve(baseName + FileType.ZIP.getExtension()),
                            pathProperties.processedDirectory().resolve(baseName + "_processed" + FileType.ZIP.getExtension()),
                            pathProperties.encodedDirectory().resolve(baseName + "_processed_base64" + FileType.TXT.getExtension()))
            );

            Path sourceDestination = outputPaths.get(0);
            processedDestination = outputPaths.get(1);
            encodedDestination = outputPaths.get(2);

            Path processedZip = zipCreator.create(
                    processedDestination,
                    extractedFiles
            );

            Path encodedFile = base64Encoder.encode(
                    processedZip,
                    encodedDestination
            );

            Path movedSource = fileRelocator.move(
                    inputFile,
                    sourceDestination
            );

            log.info("Completed processing for file: {}", sourceFileName);

            return new ProcessingResultResponse(
                    ProcessingStatus.SUCCESS,
                    movedSource.getFileName().toString(),
                    processedZip.getFileName().toString(),
                    encodedFile.getFileName().toString()
            );

        } catch (IOException exception) {

            rollback(processedDestination, encodedDestination);
            moveToError(inputFile);

            throw new FileProcessingException(
                    "Failed to create temporary directory",
                    exception
            );

        } catch (RestException exception) {

            rollback(processedDestination, encodedDestination);
            moveToError(inputFile);

            throw exception;

        } catch (RuntimeException exception) {

            rollback(processedDestination, encodedDestination);
            moveToError(inputFile);

            throw new FileProcessingException(
                    "Unexpected file processing failure",
                    exception
            );

        } finally {
            pathDeleter.delete(tempDirectory);
        }
    }

    public InputFilesResponse getInputFiles(InputFilesQueryParams params) {
        try (Stream<Path> paths = Files.list(pathProperties.inputDirectory())) {
            List<String> files = paths
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(fileName -> params.fileType() != null
                            ? fileName.contains(params.fileType().getExtension())
                            : true)
                    .toList();

            return new InputFilesResponse(files);

        } catch (IOException exception) {
            throw new FileProcessingException("Failed to read input file directory", exception);
        }
    }

    private void rollback(Path processedDestination, Path encodedDestination) {
        pathDeleter.delete(encodedDestination);
        pathDeleter.delete(processedDestination);
    }

    private void moveToError(Path inputFile) {

        if (!Files.exists(inputFile)) {
            return;
        }

        Path requestedDestination = pathProperties.errorDirectory().resolve(inputFile.getFileName());

        Path errorDestination = fileNameResolver.resolve(requestedDestination);

        try {
            fileRelocator.move(
                    inputFile,
                    errorDestination
            );

            log.info("Moved failed input file to Error: {}", errorDestination.getFileName());

        } catch (RuntimeException exception) {
            log.error("Failed to move input file to Error: {}", inputFile.getFileName(), exception);
        }
    }

    private void validateRequest(ProcessingRequest request) {

        if (request.getFileTypes() == null) {
            return;
        }

        List<String> invalidFileTypes = request.getFileTypes().stream()
                .filter(fileType -> !allowedFileTypesProperties.allowedFileTypes().contains(fileType))
                .map(FileType::getExtension)
                .toList();

        if (!invalidFileTypes.isEmpty()) {
            throw new ValidationException(
                    "Invalid file types in request",
                    Map.of("invalidFileTypes", invalidFileTypes)
            );
        }
    }

    private String getBaseName(String fileName) {
        int extensionIndex = fileName.lastIndexOf('.');

        return extensionIndex > 0
                ? fileName.substring(0, extensionIndex)
                : fileName;
    }
}
