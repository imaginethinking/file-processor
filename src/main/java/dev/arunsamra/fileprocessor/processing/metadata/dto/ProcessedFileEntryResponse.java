package dev.arunsamra.fileprocessor.processing.metadata.dto;

public record ProcessedFileEntryResponse(
        String fileName,
        String fileType,
        long fileSize
) {
}
