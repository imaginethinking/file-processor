package dev.arunsamra.fileprocessor.processing.dto;

import java.util.List;

public record InputFilesResponse(
        List<String> files
) {
}
