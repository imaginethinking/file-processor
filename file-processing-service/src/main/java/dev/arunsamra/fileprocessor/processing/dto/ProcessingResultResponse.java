package dev.arunsamra.fileprocessor.processing.dto;

public record ProcessingResultResponse(
        ProcessingStatus status,
        String sourceFile,
        String processedFile,
        String encodedFile
) {
}
