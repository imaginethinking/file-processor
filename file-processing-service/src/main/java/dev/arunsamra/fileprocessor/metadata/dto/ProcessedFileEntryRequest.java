package dev.arunsamra.fileprocessor.metadata.dto;

public record ProcessedFileEntryRequest(
        String name,
        String type,
        long size
) {
}
