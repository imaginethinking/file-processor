package dev.arunsamra.fileprocessor.metadata;

import dev.arunsamra.fileprocessor.common.exception.FileProcessingException;
import dev.arunsamra.fileprocessor.metadata.dto.ProcessedFileEntryRequest;
import dev.arunsamra.fileprocessor.metadata.dto.ProcessedFileMetadataRequest;
import dev.arunsamra.fileprocessor.storage.FileNameResolver;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProcessedFileMetadataRequestFactory {

  private final FileNameResolver fileNameResolver;

  public ProcessedFileMetadataRequest create(String sourceFileName, List<Path> files) {
    List<ProcessedFileEntryRequest> entries = files.stream().map(this::createEntry).toList();

    return new ProcessedFileMetadataRequest(sourceFileName, entries);
  }

  private ProcessedFileEntryRequest createEntry(Path path) {
    String fileName = path.getFileName().toString();

    try {
      return new ProcessedFileEntryRequest(
          fileName, fileNameResolver.getExtension(fileName), Files.size(path));
    } catch (IOException exception) {
      throw new FileProcessingException("Failed to read metadata for file: " + fileName, exception);
    }
  }
}
