package dev.arunsamra.metadata.processedfile.dto;

import java.util.List;

public record ProcessedFileMetadataResponse(
    String originalFilename,
    String processedAt,
    int count,
    List<ProcessedFileEntryResponse> files) {}
