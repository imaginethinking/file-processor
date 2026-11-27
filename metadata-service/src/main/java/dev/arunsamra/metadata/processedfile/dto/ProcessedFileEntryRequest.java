package dev.arunsamra.metadata.processedfile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record ProcessedFileEntryRequest(
    @NotBlank String name, @NotBlank String type, @PositiveOrZero long size) {}
