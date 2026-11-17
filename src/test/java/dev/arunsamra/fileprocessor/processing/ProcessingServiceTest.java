package dev.arunsamra.fileprocessor.processing;

import dev.arunsamra.fileprocessor.archive.ZipContentValidator;
import dev.arunsamra.fileprocessor.archive.ZipCreator;
import dev.arunsamra.fileprocessor.archive.ZipExtractor;
import dev.arunsamra.fileprocessor.archive.ZipInspector;
import dev.arunsamra.fileprocessor.common.exception.ValidationException;
import dev.arunsamra.fileprocessor.common.type.FileType;
import dev.arunsamra.fileprocessor.config.AllowedFileTypesProperties;
import dev.arunsamra.fileprocessor.config.FileProcessorPathProperties;
import dev.arunsamra.fileprocessor.encoding.Base64Encoder;
import dev.arunsamra.fileprocessor.processing.dto.ProcessingRequest;
import dev.arunsamra.fileprocessor.processing.dto.ProcessingResultResponse;
import dev.arunsamra.fileprocessor.processing.dto.ProcessingStatus;
import dev.arunsamra.fileprocessor.processing.metadata.entity.ProcessedFileMetadata;
import dev.arunsamra.fileprocessor.processing.metadata.factory.ProcessedFileMetadataFactory;
import dev.arunsamra.fileprocessor.processing.service.ProcessingService;
import dev.arunsamra.fileprocessor.processing.validator.FileSignatureValidator;
import dev.arunsamra.fileprocessor.storage.FileValidator;
import dev.arunsamra.fileprocessor.storage.FileNameResolver;
import dev.arunsamra.fileprocessor.storage.FileRelocator;
import dev.arunsamra.fileprocessor.storage.PathDeleter;
import dev.arunsamra.fileprocessor.xml.XmlMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessingServiceTest {

    @TempDir
    Path tempDirectory;

    @Mock
    private AllowedFileTypesProperties allowedFileTypesProperties;

    @Mock
    private FileProcessorPathProperties pathProperties;

    @Mock
    private FileValidator inputFileValidator;

    @Mock
    private ZipInspector zipInspector;

    @Mock
    private ZipContentValidator zipContentValidator;

    @Mock
    private ZipExtractor zipExtractor;

    @Mock
    private FileSignatureValidator fileSignatureValidator;

    @Mock
    private ZipCreator zipCreator;

    @Mock
    private Base64Encoder base64Encoder;

    @Mock
    private FileNameResolver fileNameResolver;

    @Mock
    private FileRelocator fileRelocator;

    @Mock
    private PathDeleter pathDeleter;

    @Mock
    private ProcessingRequest request;

    @Mock
    private ProcessedFileMetadataFactory processedFileMetadataFactory;

    @Mock
    private XmlMapper xmlMapper;

    @Mock
    private ProcessedFileMetadata processedFileMetadata;

    private ProcessingService processingService;

    private Path inputFile;
    private Path processedDirectory;
    private Path encodedDirectory;
    private Path errorDirectory;

    @BeforeEach
    void setUp() throws IOException {
        inputFile = Files.createFile(tempDirectory.resolve("documents.zip"));

        processedDirectory = tempDirectory.resolve("Processed");
        encodedDirectory = tempDirectory.resolve("Encoded");
        errorDirectory = tempDirectory.resolve("Error");

        processingService = new ProcessingService(
                allowedFileTypesProperties,
                pathProperties,
                inputFileValidator,
                zipInspector,
                zipContentValidator,
                zipExtractor,
                fileSignatureValidator,
                zipCreator,
                base64Encoder,
                fileNameResolver,
                fileRelocator,
                pathDeleter,
                processedFileMetadataFactory,
                xmlMapper
        );
    }

    @Test
    void shouldReturnSuccessWhenProcessingCompletes() {
        ZipEntry pdfEntry = new ZipEntry("document.pdf");

        Path extractedPdf = tempDirectory.resolve("document.pdf");
        Path sourceDestination = processedDirectory.resolve("documents.zip");
        Path processedDestination = processedDirectory.resolve("documents_processed.zip");
        Path encodedDestination = encodedDirectory.resolve("documents_processed_base64.txt");

        when(request.getFileName()).thenReturn("documents.zip");
        when(request.getFileTypes()).thenReturn(Set.of(FileType.PDF));

        when(allowedFileTypesProperties.allowedFileTypes())
                .thenReturn(Set.of(FileType.PDF, FileType.JPG, FileType.JPEG));

        when(inputFileValidator.validate(pathProperties.inputDirectory(),"documents.zip", FileType.ZIP)).thenReturn(inputFile);
        when(zipInspector.inspect(inputFile)).thenReturn(List.of(pdfEntry));

        when(zipExtractor.extract(eq(inputFile), any(Path.class), eq(Set.of(FileType.PDF))))
                .thenReturn(List.of(extractedPdf));

        when(processedFileMetadataFactory.create("documents.zip", List.of(extractedPdf)))
                .thenReturn(processedFileMetadata);

        when(pathProperties.processedDirectory()).thenReturn(processedDirectory);
        when(pathProperties.encodedDirectory()).thenReturn(encodedDirectory);

        when(fileNameResolver.resolveAll(any()))
                .thenReturn(List.of(sourceDestination, processedDestination, encodedDestination));

        when(zipCreator.create(eq(processedDestination), anyList())).thenReturn(processedDestination);
        when(base64Encoder.encode(processedDestination, encodedDestination)).thenReturn(encodedDestination);
        when(fileRelocator.move(inputFile, sourceDestination)).thenReturn(sourceDestination);

        ProcessingResultResponse result = processingService.process(request);

        assertEquals(ProcessingStatus.SUCCESS, result.status());
        assertEquals("documents.zip", result.sourceFile());
        assertEquals("documents_processed.zip", result.processedFile());
        assertEquals("documents_processed_base64.txt", result.encodedFile());

        verify(zipContentValidator).validate(List.of(pdfEntry));
        verify(fileSignatureValidator).validate(List.of(extractedPdf));
        verify(processedFileMetadataFactory).create("documents.zip", List.of(extractedPdf));
        verify(xmlMapper).marshal(eq(processedFileMetadata), any(OutputStream.class));

        ArgumentCaptor<List<Path>> filesCaptor = ArgumentCaptor.forClass(List.class);
        verify(zipCreator).create(eq(processedDestination), filesCaptor.capture());

        List<Path> filesToZip = filesCaptor.getValue();

        assertTrue(filesToZip.contains(extractedPdf));
        assertTrue(filesToZip.stream()
                .anyMatch(path -> path.getFileName().toString().equals("metadata.xml")));

        verify(fileRelocator).move(inputFile, sourceDestination);
        verify(pathDeleter).delete(any(Path.class));
    }

    @Test
    void shouldMoveInputToErrorWhenZipValidationFails() {
        ZipEntry invalidEntry = new ZipEntry("notes.txt");

        ValidationException validationException = new ValidationException(
                "Invalid entries in zip file",
                Map.of("unsupportedFiles", List.of("notes.txt"))
        );

        Path requestedErrorPath = errorDirectory.resolve("documents.zip");
        Path resolvedErrorPath = errorDirectory.resolve("documents_1.zip");

        when(request.getFileName()).thenReturn("documents.zip");
        when(request.getFileTypes()).thenReturn(Set.of(FileType.PDF));
        when(allowedFileTypesProperties.allowedFileTypes()).thenReturn(Set.of(FileType.PDF));

        when(inputFileValidator.validate(pathProperties.inputDirectory(),"documents.zip", FileType.ZIP)).thenReturn(inputFile);
        when(zipInspector.inspect(inputFile)).thenReturn(List.of(invalidEntry));

        doThrow(validationException).when(zipContentValidator).validate(List.of(invalidEntry));

        when(pathProperties.errorDirectory()).thenReturn(errorDirectory);
        when(fileNameResolver.resolve(requestedErrorPath)).thenReturn(resolvedErrorPath);
        when(fileRelocator.move(inputFile, resolvedErrorPath)).thenReturn(resolvedErrorPath);

        ValidationException thrown = assertThrows(
                ValidationException.class,
                () -> processingService.process(request)
        );

        assertEquals(validationException, thrown);

        verify(fileRelocator).move(inputFile, resolvedErrorPath);
        verify(zipExtractor, never()).extract(any(), any(), any());
        verify(processedFileMetadataFactory, never()).create(any(), any());
        verifyNoInteractions(xmlMapper);
        verify(zipCreator, never()).create(any(), any());
    }

    @Test
    void shouldRollbackOutputsWhenEncodingFails() {
        ZipEntry pdfEntry = new ZipEntry("document.pdf");

        Path extractedPdf = tempDirectory.resolve("document.pdf");
        Path sourceDestination = processedDirectory.resolve("documents.zip");
        Path processedDestination = processedDirectory.resolve("documents_processed.zip");
        Path encodedDestination = encodedDirectory.resolve("documents_processed_base64.txt");
        Path errorDestination = errorDirectory.resolve("documents.zip");

        when(request.getFileName()).thenReturn("documents.zip");
        when(request.getFileTypes()).thenReturn(Set.of(FileType.PDF));
        when(allowedFileTypesProperties.allowedFileTypes()).thenReturn(Set.of(FileType.PDF));

        when(inputFileValidator.validate(pathProperties.inputDirectory(),"documents.zip", FileType.ZIP)).thenReturn(inputFile);
        when(zipInspector.inspect(inputFile)).thenReturn(List.of(pdfEntry));

        when(zipExtractor.extract(eq(inputFile), any(Path.class), eq(Set.of(FileType.PDF))))
                .thenReturn(List.of(extractedPdf));

        when(processedFileMetadataFactory.create("documents.zip", List.of(extractedPdf)))
                .thenReturn(processedFileMetadata);

        when(pathProperties.processedDirectory()).thenReturn(processedDirectory);
        when(pathProperties.encodedDirectory()).thenReturn(encodedDirectory);
        when(pathProperties.errorDirectory()).thenReturn(errorDirectory);

        when(fileNameResolver.resolveAll(any()))
                .thenReturn(List.of(sourceDestination, processedDestination, encodedDestination));

        when(zipCreator.create(eq(processedDestination), anyList())).thenReturn(processedDestination);

        when(base64Encoder.encode(processedDestination, encodedDestination))
                .thenThrow(new ValidationException("Encoding failed", Map.of()));

        when(fileNameResolver.resolve(errorDirectory.resolve("documents.zip"))).thenReturn(errorDestination);
        when(fileRelocator.move(inputFile, errorDestination)).thenReturn(errorDestination);

        assertThrows(ValidationException.class, () -> processingService.process(request));

        verify(processedFileMetadataFactory).create("documents.zip", List.of(extractedPdf));
        verify(xmlMapper).marshal(eq(processedFileMetadata), any(OutputStream.class));
        verify(pathDeleter).delete(encodedDestination);
        verify(pathDeleter).delete(processedDestination);
        verify(fileRelocator).move(inputFile, errorDestination);
        verify(fileRelocator, never()).move(inputFile, sourceDestination);
    }
}