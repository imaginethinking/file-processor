package dev.arunsamra.fileprocessor.processing.metadata.factory;

import dev.arunsamra.fileprocessor.common.exception.FileProcessingException;
import dev.arunsamra.fileprocessor.processing.metadata.entity.ProcessedFileEntry;
import dev.arunsamra.fileprocessor.processing.metadata.entity.ProcessedFileMetadata;
import dev.arunsamra.fileprocessor.storage.FileNameResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProcessedFileMetadataFactory {

    private final FileNameResolver fileNameResolver;

    public ProcessedFileMetadata create(String sourceFileName, List<Path> files) {
        List<ProcessedFileEntry> entries = files.stream()
                .map(this::createEntry)
                .toList();

        return new ProcessedFileMetadata(
                sourceFileName,
                LocalDateTime.now(ZoneOffset.UTC).toString(),
                entries.size(),
                entries
        );
    }

    private ProcessedFileEntry createEntry(Path path) {
        try {
            return new ProcessedFileEntry(
                    path.getFileName().toString(),
                    fileNameResolver.getExtension(path.getFileName().toString()),
                    Files.size(path)
            );
        } catch (IOException exception) {
            throw new FileProcessingException("Failed to read metadata for file: " + path.getFileName(), exception);
        }
    }
}
