package dev.arunsamra.fileprocessor.archive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.arunsamra.common.rest.exception.NotFoundException;
import dev.arunsamra.fileprocessor.common.exception.FileProcessingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ZipEntryReaderTest {

  @TempDir Path tempDirectory;

  private final ZipEntryReader reader = new ZipEntryReader();

  @Test
  void shouldReadNamedEntry() throws IOException {
    Path zip = createZip("test.zip", "metadata.xml", "<xml/>");

    String content =
        reader.read(
            zip,
            "metadata.xml",
            inputStream -> new String(readAll(inputStream), StandardCharsets.UTF_8));

    assertEquals("<xml/>", content);
  }

  @Test
  void shouldThrowNotFoundWhenEntryIsMissing() throws IOException {
    Path zip = createZip("test.zip", "other.txt", "hi");

    assertThrows(
        NotFoundException.class, () -> reader.read(zip, "metadata.xml", inputStream -> "x"));
  }

  @Test
  void shouldThrowFileProcessingExceptionWhenZipIsCorrupt() throws IOException {
    Path notAZip = Files.writeString(tempDirectory.resolve("broken.zip"), "not a zip");

    assertThrows(
        FileProcessingException.class,
        () -> reader.read(notAZip, "metadata.xml", inputStream -> "x"));
  }

  private Path createZip(String name, String entryName, String content) throws IOException {
    Path zip = tempDirectory.resolve(name);

    try (OutputStream outputStream = Files.newOutputStream(zip);
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
      zipOutputStream.putNextEntry(new ZipEntry(entryName));
      zipOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
      zipOutputStream.closeEntry();
    }

    return zip;
  }

  private static byte[] readAll(InputStream inputStream) {
    try {
      return inputStream.readAllBytes();
    } catch (IOException exception) {
      throw new IllegalStateException(exception);
    }
  }
}
