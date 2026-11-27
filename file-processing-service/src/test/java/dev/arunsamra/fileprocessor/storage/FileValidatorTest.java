package dev.arunsamra.fileprocessor.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.arunsamra.common.rest.exception.BadRequestException;
import dev.arunsamra.fileprocessor.common.type.FileType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileValidatorTest {

  @TempDir Path directory;

  private final FileValidator validator = new FileValidator();

  @Test
  void shouldReturnFileWhenValid() throws IOException {
    Files.createFile(directory.resolve("documents.zip"));

    Path result = validator.validate(directory, "documents.zip", FileType.ZIP);

    assertEquals(directory.toAbsolutePath().normalize().resolve("documents.zip"), result);
  }

  @Test
  void shouldAcceptUpperCaseExtension() throws IOException {
    Files.createFile(directory.resolve("DOCUMENTS.ZIP"));

    Path result = validator.validate(directory, "DOCUMENTS.ZIP", FileType.ZIP);

    assertEquals(directory.toAbsolutePath().normalize().resolve("DOCUMENTS.ZIP"), result);
  }

  @Test
  void shouldRejectPathTraversal() {
    assertThrows(
        BadRequestException.class,
        () -> validator.validate(directory, "../secret.zip", FileType.ZIP));
  }

  @Test
  void shouldRejectMissingFile() {
    assertThrows(
        BadRequestException.class,
        () -> validator.validate(directory, "missing.zip", FileType.ZIP));
  }

  @Test
  void shouldRejectWrongFileType() throws IOException {
    Files.createFile(directory.resolve("notes.txt"));

    assertThrows(
        BadRequestException.class, () -> validator.validate(directory, "notes.txt", FileType.ZIP));
  }
}
