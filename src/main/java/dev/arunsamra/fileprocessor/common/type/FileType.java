package dev.arunsamra.fileprocessor.common.type;

import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

@Getter
public enum FileType {
    PDF(".pdf"),
    JPEG(".jpeg"),
    JPG(".jpg");

    private final String extention;

    FileType(String extension) {
        this.extention = extension;
    }

    public static Optional<FileType> fromFileName(String fileName) {
        String normalized = fileName.toLowerCase(Locale.ROOT);

        return Arrays.stream(values())
                .filter(type -> normalized.endsWith(type.getExtention()))
                .findFirst();
    }
}
