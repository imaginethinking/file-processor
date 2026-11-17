package dev.arunsamra.fileprocessor.xml;

import dev.arunsamra.fileprocessor.common.exception.FileProcessingException;
import dev.arunsamra.fileprocessor.processing.metadata.entity.ProcessedFileEntry;
import dev.arunsamra.fileprocessor.processing.metadata.entity.ProcessedFileMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class XmlMapperTest {

    private XmlMapper xmlMapper;

    @BeforeEach
    void setUp() {
        xmlMapper = new XmlMapper();
    }

    @Test
    void shouldMarshalObjectToXml() {
        ProcessedFileEntry entry = new ProcessedFileEntry(
                "document.pdf",
                ".pdf",
                1024
        );

        ProcessedFileMetadata metadata = new ProcessedFileMetadata(
                "documents.zip",
                "2026-11-17T09:30:00",
                1,
                List.of(entry)
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        xmlMapper.marshal(metadata, outputStream);

        String xml = outputStream.toString(StandardCharsets.UTF_8);

        assertTrue(xml.contains("<originalFilename>documents.zip</originalFilename>"));
        assertTrue(xml.contains("<name>document.pdf</name>"));
        assertTrue(xml.contains("<size>1024</size>"));
        assertTrue(xml.contains("<processedAt>2026-11-17T09:30:00</processedAt>"));
        assertTrue(xml.contains("<count>1</count>"));
    }

    @Test
    void shouldMarshalAndUnmarshalObject() {
        ProcessedFileEntry entry = new ProcessedFileEntry(
                "document.pdf",
                ".pdf",
                1024
        );

        ProcessedFileMetadata metadata = new ProcessedFileMetadata(
                "documents.zip",
                "2026-11-17T09:30:00",
                1,
                List.of(entry)
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        xmlMapper.marshal(metadata, outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(
                outputStream.toByteArray()
        );

        ProcessedFileMetadata result = xmlMapper.unmarshal(
                inputStream,
                ProcessedFileMetadata.class
        );

        assertEquals(metadata, result);
    }

    @Test
    void shouldThrowFileProcessingExceptionWhenUnmarshallingInvalidXml() {
        String invalidXml = "<invalid>";

        ByteArrayInputStream inputStream = new ByteArrayInputStream(
                invalidXml.getBytes(StandardCharsets.UTF_8)
        );

        assertThrows(
                FileProcessingException.class,
                () -> xmlMapper.unmarshal(inputStream, ProcessedFileMetadata.class));
    }
}
