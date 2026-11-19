package dev.arunsamra.fileprocessor.metadata;

import dev.arunsamra.fileprocessor.common.exception.MetadataServiceException;
import dev.arunsamra.fileprocessor.metadata.dto.ProcessedFileEntryRequest;
import dev.arunsamra.fileprocessor.metadata.dto.ProcessedFileMetadataRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetadataFileCreatorTest {

    @TempDir
    Path tempDirectory;

    @Mock
    private MetadataClient metadataClient;

    @Mock
    private ProcessedFileMetadataRequestFactory requestFactory;

    @InjectMocks
    private MetadataFileCreator creator;

    private final List<Path> files = List.of(Path.of("a.pdf"));
    private ProcessedFileMetadataRequest request;

    @BeforeEach
    void setUp() {
        request = new ProcessedFileMetadataRequest(
                "documents.zip",
                List.of(new ProcessedFileEntryRequest("a.pdf", ".pdf", 5))
        );
        when(requestFactory.create("documents.zip", files)).thenReturn(request);
    }

    @Test
    void shouldWriteMetadataXmlToDirectory() throws IOException {
        byte[] xml = "<processedFileMetadata/>".getBytes(StandardCharsets.UTF_8);
        when(metadataClient.createMetadata(request)).thenReturn(xml);

        Path result = creator.create("documents.zip", files, tempDirectory);

        assertEquals(tempDirectory.resolve("metadata.xml"), result);
        assertArrayEquals(xml, Files.readAllBytes(result));
    }

    @Test
    void shouldThrowMetadataServiceExceptionWhenClientFails() {
        when(metadataClient.createMetadata(request)).thenThrow(new RestClientException("down"));

        assertThrows(MetadataServiceException.class,
                () -> creator.create("documents.zip", files, tempDirectory));
    }

    @Test
    void shouldThrowMetadataServiceExceptionWhenResponseIsEmpty() {
        when(metadataClient.createMetadata(request)).thenReturn(new byte[0]);

        assertThrows(MetadataServiceException.class,
                () -> creator.create("documents.zip", files, tempDirectory));
    }
}