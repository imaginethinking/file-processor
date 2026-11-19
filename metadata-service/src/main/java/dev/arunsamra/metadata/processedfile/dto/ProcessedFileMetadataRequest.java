package dev.arunsamra.metadata.processedfile.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ProcessedFileMetadataRequest(
        @NotBlank
        String originalFilename,
        @NotEmpty
        @Valid
        List<ProcessedFileEntryRequest> files
) {
}
