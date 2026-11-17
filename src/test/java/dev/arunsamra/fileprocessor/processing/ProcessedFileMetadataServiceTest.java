package dev.arunsamra.fileprocessor.processing;

import dev.arunsamra.fileprocessor.archive.ZipEntryReader;
import dev.arunsamra.fileprocessor.config.FileProcessorPathProperties;
import dev.arunsamra.fileprocessor.processing.metadata.dto.ProcessedFileMetadataResponse;
import dev.arunsamra.fileprocessor.processing.metadata.entity.ProcessedFileMetadata;
import dev.arunsamra.fileprocessor.processing.service.ProcessedFileMetadataService;
import dev.arunsamra.fileprocessor.xml.XmlMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProcessedFileMetadataServiceTest {

    @TempDir
    Path tempDirectory;

    @Mock
    private FileProcessorPathProperties pathProperties;

    @Mock
    private ZipEntryReader zipEntryReader;

    @Mock
    private XmlMapper xmlMapper;

    @Mock
    private ProcessedFileMetadata metadata;

    private ProcessedFileMetadataService processedFileMetadataService;

    @BeforeEach
    void setUp() {
        processedFileMetadataService = new ProcessedFileMetadataService(
                pathProperties,
                zipEntryReader,
                xmlMapper
        );
    }

    @Test
    void shouldReturnMetadataFromProcessedFile() {
        Path processedDirectory = tempDirectory.resolve("Processed");
        Path processedZip = processedDirectory.resolve("documents_processed.zip");

        when(pathProperties.processedDirectory()).thenReturn(processedDirectory);

        when(zipEntryReader.read(
                eq(processedZip),
                eq("metadata.xml"),
                any()
        )).thenReturn(metadata);

        ProcessedFileMetadataResponse result = processedFileMetadataService.getMetadata("documents_processed.zip");

        assertEquals(metadata.getOriginalFilename(), result.originalFilename());
        assertEquals(metadata.getProcessedAt(), result.processedAt());
        assertEquals(metadata.getCount(), result.count());
        assertEquals(metadata.getFiles().size(), result.files().size());

        verify(zipEntryReader).read(
                eq(processedZip),
                eq("metadata.xml"),
                any()
        );
    }
}
