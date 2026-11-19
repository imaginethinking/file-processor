package dev.arunsamra.metadata.processedfile.service;

import dev.arunsamra.metadata.processedfile.dto.ProcessedFileEntryRequest;
import dev.arunsamra.metadata.processedfile.dto.ProcessedFileMetadataRequest;
import dev.arunsamra.metadata.processedfile.dto.ProcessedFileMetadataResponse;
import dev.arunsamra.metadata.processedfile.model.ProcessedFileEntry;
import dev.arunsamra.metadata.processedfile.model.ProcessedFileMetadata;
import dev.arunsamra.metadata.xml.service.MarshallingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessedFileMetadataServiceTest {

    @Mock
    private MarshallingService marshallingService;

    @InjectMocks
    private ProcessedFileMetadataService service;

    @Test
    void shouldMapRequestToMetadataAndReturnMarshalledBytes() throws Exception {
        byte[] marshalled = "<xml/>".getBytes(StandardCharsets.UTF_8);
        ArgumentCaptor<ProcessedFileMetadata> captor = ArgumentCaptor.forClass(ProcessedFileMetadata.class);
        doAnswer(invocation -> {
            OutputStream outputStream = invocation.getArgument(1);
            outputStream.write(marshalled);
            return null;
        }).when(marshallingService).marshal(captor.capture(), any(OutputStream.class));

        ProcessedFileMetadataRequest request = new ProcessedFileMetadataRequest(
                "test.zip",
                List.of(
                        new ProcessedFileEntryRequest("a.pdf", "PDF", 1024),
                        new ProcessedFileEntryRequest("b.jpg", "JPG", 2048)
                )
        );

        byte[] result = service.create(request);

        ProcessedFileMetadata metadata = captor.getValue();
        assertArrayEquals(marshalled, result);
        assertEquals("test.zip", metadata.getOriginalFilename());
        assertEquals(2, metadata.getCount());
        assertNotNull(metadata.getProcessedAt());
        assertEquals(2, metadata.getFiles().size());
        assertEquals("a.pdf", metadata.getFiles().get(0).getName());
        assertEquals("PDF", metadata.getFiles().get(0).getType());
        assertEquals(1024, metadata.getFiles().get(0).getSize());
        assertEquals("b.jpg", metadata.getFiles().get(1).getName());
    }

    @Test
    void shouldMapUnmarshalledMetadataToResponse() {
        ProcessedFileMetadata metadata = new ProcessedFileMetadata(
                "test.zip",
                "2026-11-19T10:01:30",
                2,
                List.of(
                        new ProcessedFileEntry("a.pdf", "PDF", 1024),
                        new ProcessedFileEntry("b.jpg", "JPG", 2048)
                )
        );
        when(marshallingService.unmarshal(any(InputStream.class), eq(ProcessedFileMetadata.class)))
                .thenReturn(metadata);

        ProcessedFileMetadataResponse response = service.read("<xml/>".getBytes(StandardCharsets.UTF_8));

        assertEquals("test.zip", response.originalFilename());
        assertEquals("2026-11-19T10:01:30", response.processedAt());
        assertEquals(2, response.count());
        assertEquals(2, response.files().size());
        assertEquals("b.jpg", response.files().get(1).name());
        assertEquals("JPG", response.files().get(1).type());
        assertEquals(2048, response.files().get(1).size());
    }
}