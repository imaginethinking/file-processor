package dev.arunsamra.fileprocessor.metadata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import dev.arunsamra.common.rest.exception.BadRequestException;
import dev.arunsamra.fileprocessor.archive.ZipEntryReader;
import dev.arunsamra.fileprocessor.common.exception.MetadataServiceException;
import dev.arunsamra.fileprocessor.common.type.FileType;
import dev.arunsamra.fileprocessor.config.FileProcessorPathProperties;
import dev.arunsamra.fileprocessor.metadata.dto.ProcessedFileEntryResponse;
import dev.arunsamra.fileprocessor.metadata.dto.ProcessedFileMetadataResponse;
import dev.arunsamra.fileprocessor.storage.FileValidator;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

@ExtendWith(MockitoExtension.class)
class MetadataFileReaderTest {

  private static final String FILE_NAME = "documents_processed.zip";

  @Mock private FileProcessorPathProperties pathProperties;

  @Mock private FileValidator fileValidator;

  @Mock private ZipEntryReader zipEntryReader;

  @Mock private MetadataClient metadataClient;

  @InjectMocks private MetadataFileReader reader;

  private final Path processedDirectory = Path.of("processed");
  private final Path zip = processedDirectory.resolve(FILE_NAME);
  private final byte[] xml = "<processedFileMetadata/>".getBytes(StandardCharsets.UTF_8);

  @BeforeEach
  void setUp() {
    when(pathProperties.processedDirectory()).thenReturn(processedDirectory);
  }

  @Test
  void shouldReadMetadataFromProcessedZip() {
    ProcessedFileMetadataResponse expected =
        new ProcessedFileMetadataResponse(
            "documents.zip",
            "2026-11-19T10:00:00",
            1,
            List.of(new ProcessedFileEntryResponse("a.pdf", ".pdf", 1024)));
    when(fileValidator.validate(processedDirectory, FILE_NAME, FileType.ZIP)).thenReturn(zip);
    when(zipEntryReader.read(eq(zip), eq("metadata.xml"), any())).thenReturn(xml);
    when(metadataClient.readMetadata(xml)).thenReturn(expected);

    ProcessedFileMetadataResponse result = reader.read(FILE_NAME);

    assertEquals(expected, result);
  }

  @Test
  void shouldPropagateValidationFailure() {
    when(fileValidator.validate(processedDirectory, FILE_NAME, FileType.ZIP))
        .thenThrow(new BadRequestException("File does not exist: " + FILE_NAME));

    assertThrows(BadRequestException.class, () -> reader.read(FILE_NAME));

    verifyNoInteractions(zipEntryReader, metadataClient);
  }

  @Test
  void shouldThrowMetadataServiceExceptionWhenClientFails() {
    when(fileValidator.validate(processedDirectory, FILE_NAME, FileType.ZIP)).thenReturn(zip);
    when(zipEntryReader.read(eq(zip), eq("metadata.xml"), any())).thenReturn(xml);
    when(metadataClient.readMetadata(xml)).thenThrow(new RestClientException("down"));

    assertThrows(MetadataServiceException.class, () -> reader.read(FILE_NAME));
  }
}
