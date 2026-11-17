package dev.arunsamra.fileprocessor.processing.metadata.dto;

import java.util.List;

public record ProcessedFileMetadataResponse(
        String originalFilename,
        String processedAt,
        int count,
        List<ProcessedFileEntryResponse> files
) {
}
