package dev.arunsamra.metadata.xml.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.arunsamra.metadata.processedfile.model.ProcessedFileEntry;
import dev.arunsamra.metadata.processedfile.model.ProcessedFileMetadata;
import dev.arunsamra.metadata.xml.exception.InvalidXmlException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class MarshallingServiceTest {

  private final MarshallingService marshallingService = new MarshallingService();

  @Test
  void shouldMarshalAndUnmarshalMetadata() {
    ProcessedFileMetadata metadata =
        new ProcessedFileMetadata(
            "documents.zip",
            "2026-11-19T10:00:00",
            1,
            List.of(new ProcessedFileEntry("file.pdf", "PDF", 1024)));
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    marshallingService.marshal(metadata, outputStream);

    ProcessedFileMetadata result =
        marshallingService.unmarshal(
            new ByteArrayInputStream(outputStream.toByteArray()), ProcessedFileMetadata.class);

    assertEquals("documents.zip", result.getOriginalFilename());
    assertEquals(1, result.getCount());
    assertEquals(1, result.getFiles().size());
    assertEquals("file.pdf", result.getFiles().getFirst().getName());
    assertEquals(1024, result.getFiles().getFirst().getSize());
  }

  @Test
  void shouldThrowInvalidXmlExceptionForMalformedXml() {
    ByteArrayInputStream xml = toStream("<processedFileMetadata><unclosed>");

    assertThrows(
        InvalidXmlException.class,
        () -> marshallingService.unmarshal(xml, ProcessedFileMetadata.class));
  }

  @Test
  void shouldRejectExternalEntities() {
    ByteArrayInputStream xml =
        toStream(
            """
            <?xml version="1.0"?>
            <!DOCTYPE processedFileMetadata [<!ENTITY x SYSTEM "file:///etc/hostname">]>
            <processedFileMetadata><originalFilename>&x;</originalFilename></processedFileMetadata>
            """);

    assertThrows(
        InvalidXmlException.class,
        () -> marshallingService.unmarshal(xml, ProcessedFileMetadata.class));
  }

  private static ByteArrayInputStream toStream(String xml) {
    return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
  }
}
