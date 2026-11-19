package dev.arunsamra.fileprocessor.metadata.dto;

import java.util.List;

public record ProcessedFileMetadataRequest(
        String originalFilename,
        List<ProcessedFileEntryRequest> files
) {
}
