package dev.arunsamra.fileprocessor.metadata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.arunsamra.fileprocessor.common.exception.FileProcessingException;
import dev.arunsamra.fileprocessor.metadata.dto.ProcessedFileMetadataRequest;
import dev.arunsamra.fileprocessor.storage.FileNameResolver;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ProcessedFileMetadataRequestFactoryTest {

  @TempDir Path tempDirectory;

  private final ProcessedFileMetadataRequestFactory factory =
      new ProcessedFileMetadataRequestFactory(new FileNameResolver());

  @Test
  void shouldBuildRequestFromFiles() throws IOException {
    Path pdf = Files.write(tempDirectory.resolve("a.pdf"), new byte[5]);
    Path jpg = Files.write(tempDirectory.resolve("b.jpg"), new byte[12]);

    ProcessedFileMetadataRequest request = factory.create("documents.zip", List.of(pdf, jpg));

    assertEquals("documents.zip", request.originalFilename());
    assertEquals(2, request.files().size());
    assertEquals("a.pdf", request.files().get(0).name());
    assertEquals(".pdf", request.files().get(0).type());
    assertEquals(5, request.files().get(0).size());
    assertEquals(".jpg", request.files().get(1).type());
    assertEquals(12, request.files().get(1).size());
  }

  @Test
  void shouldThrowWhenFileCannotBeRead() {
    List<Path> files = List.of(tempDirectory.resolve("missing.pdf"));

    assertThrows(FileProcessingException.class, () -> factory.create("documents.zip", files));
  }
}
