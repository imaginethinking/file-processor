package dev.arunsamra.fileprocessor.common.type;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Getter
public enum FileType {
    PDF(".pdf", List.of(new byte[]{0x25, 0x50, 0x44, 0x46, 0x2D})),
    JPEG(".jpeg", List.of(new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF})),
    JPG(".jpg", List.of(new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF})),
    ZIP(".zip", List.of(
            new byte[]{0x50, 0x4B, 0x03, 0x04},
            new byte[]{0x50, 0x4B, 0x05, 0x06},
            new byte[]{0x50, 0x4B, 0x07, 0x08})),
    TXT(".txt", List.of());

    private final String extension;
    private final List<byte[]> signatures;

    FileType(String extension, List<byte[]> signatures) {
        this.extension = extension;
        this.signatures = signatures;
    }

    public static Optional<FileType> fromFileName(String fileName) {
        String normalized = fileName.toLowerCase(Locale.ROOT);

        return Arrays.stream(values())
                .filter(type -> normalized.endsWith(type.getExtension()))
                .findFirst();
    }
}
