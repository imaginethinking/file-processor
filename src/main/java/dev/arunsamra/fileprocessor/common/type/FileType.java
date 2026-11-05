package dev.arunsamra.fileprocessor.common.type;

import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

@Getter
public enum FileType {
    PDF(".pdf"),
    JPEG(".jpeg"),
    JPG(".jpg"),
    ZIP(".zip");

    private final String extension;

    FileType(String extension) {
        this.extension = extension;
    }

    public static Optional<FileType> fromFileName(String fileName) {
        String normalized = fileName.toLowerCase(Locale.ROOT);

        return Arrays.stream(values())
                .filter(type -> normalized.endsWith(type.getExtension()))
                .findFirst();
    }
}
