package dev.arunsamra.fileprocessor.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@Slf4j
@ConfigurationProperties(prefix = "file-processor")
public record FileProcessorPathProperties(
        Path inputDirectory,
        Path processedDirectory,
        Path encodedDirectory,
        Path errorDirectory
) {
    @PostConstruct
    private void logConfig() {
        log.info("Configured input directory: {}", inputDirectory);
        log.info("Configured processed directory: {}", processedDirectory);
        log.info("Configured encoded directory: {}", encodedDirectory);
        log.info("Configured error directory: {}", errorDirectory);
    }
}
