package dev.arunsamra.fileprocessor.processing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessingRequest {
    @NotBlank(message = "File name is required")
    String fileName;
}
