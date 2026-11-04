package dev.arunsamra.fileprocessor.archive;

import dev.arunsamra.fileprocessor.common.exception.InvalidZipContentException;
import dev.arunsamra.fileprocessor.common.type.FileType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;

@Component
public class ZipContentValidator {

    private static final Set<FileType> ALLOWED_FILE_TYPES = Set.of(
            FileType.PDF,
            FileType.JPG,
            FileType.JPEG
    );

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

            throw new InvalidZipContentException("", errors);
        }
    }



    private boolean isSubdirectory(ZipEntry entry) {
        return entry.isDirectory() || entry.getName().contains("/");
    }

    private boolean isSupportedFileType(ZipEntry entry) {
        return FileType.fromFileName(entry.getName())
                .map(ALLOWED_FILE_TYPES::contains)
                .orElse(false);
    }
}
