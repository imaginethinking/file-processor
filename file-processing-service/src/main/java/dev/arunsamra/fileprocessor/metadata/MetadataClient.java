package dev.arunsamra.fileprocessor.metadata;

import dev.arunsamra.fileprocessor.metadata.dto.ProcessedFileMetadataRequest;
import dev.arunsamra.fileprocessor.metadata.dto.ProcessedFileMetadataResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/internal/v1/metadata/processed-files")
public interface MetadataClient {

    @CircuitBreaker(name = "metadata-service")
    @Retry(name = "metadata-service")
    @PostExchange(
            contentType = MediaType.APPLICATION_JSON_VALUE,
    accept = MediaType.APPLICATION_XML_VALUE)
    byte[] createMetadata(@RequestBody ProcessedFileMetadataRequest request);

    @CircuitBreaker(name = "metadata-service")
    @Retry(name = "metadata-service")
    @PostExchange(
            url = "/unmarshal",
            contentType = MediaType.APPLICATION_XML_VALUE,
            accept = MediaType.APPLICATION_JSON_VALUE
    )
    ProcessedFileMetadataResponse readMetadata(@RequestBody byte[] xml);
}
