package dev.arunsamra.fileprocessor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "file-processor")
public record FileProcessorProperties(
        Path inputDirectory,
        Path processedDirectory,
        Path encodedDirectory,
        Path errorDirectory
) {
}
