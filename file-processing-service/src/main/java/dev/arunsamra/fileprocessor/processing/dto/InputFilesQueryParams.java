package dev.arunsamra.fileprocessor.processing.dto;

import dev.arunsamra.fileprocessor.common.type.FileType;
import jakarta.annotation.Nullable;

public record InputFilesQueryParams(
        @Nullable
        FileType fileType
) {
}
