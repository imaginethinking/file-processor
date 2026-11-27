package dev.arunsamra.fileprocessor.metadata;

import dev.arunsamra.fileprocessor.archive.ZipEntryReader;
import dev.arunsamra.fileprocessor.common.exception.FileProcessingException;
import dev.arunsamra.fileprocessor.common.exception.MetadataServiceException;
import dev.arunsamra.fileprocessor.common.type.FileType;
import dev.arunsamra.fileprocessor.config.FileProcessorPathProperties;
import dev.arunsamra.fileprocessor.metadata.dto.ProcessedFileMetadataResponse;
import dev.arunsamra.fileprocessor.storage.FileValidator;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@RequiredArgsConstructor
@Slf4j
@Component
public class MetadataFileReader {

  private static final String METADATA_FILE_NAME = "metadata.xml";

  private final FileProcessorPathProperties pathProperties;
  private final FileValidator fileValidator;
  private final ZipEntryReader zipEntryReader;
  private final MetadataClient metadataClient;

  public ProcessedFileMetadataResponse read(String processedFileName) {
    Path processedZip =
        fileValidator.validate(
            pathProperties.processedDirectory(), processedFileName, FileType.ZIP);

    byte[] xml = zipEntryReader.read(processedZip, METADATA_FILE_NAME, this::readBytes);

    ProcessedFileMetadataResponse response;

    try {
      response = metadataClient.readMetadata(xml);
    } catch (CallNotPermittedException exception) {
      log.warn(
          "Metadata service request rejected because the circuit breaker is open for file: {}",
          processedFileName);
      throw new MetadataServiceException("Metadata service is temporarily unavailable", exception);
    } catch (RestClientException exception) {
      log.error(
          "Metadata service request failed for file: {} ({})",
          processedFileName,
          exception.getMessage());
      log.debug("Metadata service failure detail", exception);
      throw new MetadataServiceException("Metadata service request failed", exception);
    }

    if (response == null) {
      throw new MetadataServiceException("Metadata service returned an empty response", null);
    }

    return response;
  }

  private byte[] readBytes(InputStream inputStream) {
    try {
      return inputStream.readAllBytes();
    } catch (IOException exception) {
      throw new FileProcessingException("Failed to read metadata file", exception);
    }
  }
}
