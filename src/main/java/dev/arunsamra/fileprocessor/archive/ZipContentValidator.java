package dev.arunsamra.fileprocessor.archive;

import dev.arunsamra.fileprocessor.common.exception.ValidationException;
import dev.arunsamra.fileprocessor.common.type.FileType;
import dev.arunsamra.fileprocessor.config.AllowedFileTypesProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;

@Slf4j
@RequiredArgsConstructor
@Component
public class ZipContentValidator {

    private final AllowedFileTypesProperties allowedFileTypesProperties;

    public void validate(List<ZipEntry> entries) {
        List<String> subdirectories = entries.stream()
                .filter(this::isSubdirectory)
                .map(ZipEntry::getName)
                .toList();

        List<String> unsupportedFiles = entries.stream()
                .filter(entry -> !isSubdirectory(entry))
                .filter(entry -> !isSupportedFileType(entry))
                .map(ZipEntry::getName)
                .toList();

        if (!subdirectories.isEmpty() || !unsupportedFiles.isEmpty()) {
            Map<String, List<String>> errors = new HashMap<>();

            if (!subdirectories.isEmpty()) {
                errors.put("subdirectories", subdirectories);
            }

            if (!unsupportedFiles.isEmpty()) {
                errors.put("unsupportedFiles", unsupportedFiles);
            }

            log.warn("Invalid entries in zip file: {}", errors);
            throw new ValidationException("Invalid entries in zip file", errors);
        }
    }



    private boolean isSubdirectory(ZipEntry entry) {
        return entry.isDirectory() || entry.getName().contains("/");
    }

    private boolean isSupportedFileType(ZipEntry entry) {
        return FileType.fromFileName(entry.getName())
                .map(fileType -> allowedFileTypesProperties.allowedFileTypes().contains(fileType))
                .orElse(false);
    }
}
