package dev.arunsamra.fileprocessor.processing.dto;

import dev.arunsamra.fileprocessor.processing.ProcessingStatus;

public record ProcessingResultResponse(
        ProcessingStatus status,
        String sourceFile,
        String processedFile,
        String encodedFile
) {
}
