package dev.arunsamra.fileprocessor.processing.dto;

import dev.arunsamra.fileprocessor.common.type.FileType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ProcessingRequest {
    @NotBlank(message = "File name is required")
    String fileName;

    @Nullable
    Set<FileType> includeFileTypes;
}
