package dev.arunsamra.fileprocessor.metadata;

import dev.arunsamra.fileprocessor.common.exception.FileProcessingException;
import dev.arunsamra.fileprocessor.common.exception.MetadataServiceException;
import dev.arunsamra.fileprocessor.metadata.dto.ProcessedFileMetadataRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class MetadataFileCreator {
    private static final String METADATA_FILE_NAME = "metadata.xml";

    private final MetadataClient metadataClient;
    private final ProcessedFileMetadataRequestFactory requestFactory;

    public Path create(String sourceFileName, List<Path> files, Path directory) {
        ProcessedFileMetadataRequest request = requestFactory.create(sourceFileName, files);

        byte[] xml = requestMetadata(sourceFileName, request);
        Path metadataFile = directory.resolve(METADATA_FILE_NAME);

        try {
            Files.write(metadataFile, xml);
        } catch (IOException exception) {
            throw new FileProcessingException("Failed to write metadata file", exception);
        }

        return metadataFile;
    }

    private byte[] requestMetadata(String sourceFileName, ProcessedFileMetadataRequest request) {
        byte[] xml;

        try {
            xml = metadataClient.createMetadata(request);
        } catch (RestClientException exception) {
            log.error("Metadata service request failed for file: {} ({})", sourceFileName, exception.getMessage());
            log.debug("Metadata service failure detail", exception);
            throw new MetadataServiceException("Metadata service request failed", exception);
        }

        if (xml == null || xml.length == 0) {
            throw new MetadataServiceException("Metadata service returned an empty response", null);
        }

        return xml;
    }
}
