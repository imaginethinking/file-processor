package dev.arunsamra.fileprocessor.metadata;

import dev.arunsamra.fileprocessor.metadata.dto.ProcessedFileMetadataRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/internal/v1/metadata/processed-files")
public interface MetadataClient {

    @PostExchange(
            contentType = MediaType.APPLICATION_JSON_VALUE,
    accept = MediaType.APPLICATION_XML_VALUE)
    byte[] createMetadata(@RequestBody ProcessedFileMetadataRequest request);
}
