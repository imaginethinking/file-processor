package dev.arunsamra.fileprocessor.config;

import dev.arunsamra.fileprocessor.common.type.FileType;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@Slf4j
@ConfigurationProperties(prefix = "file-processor")
public record AllowedFileTypesProperties(
        Set<FileType> allowedFileTypes
) {
    @PostConstruct
    private void logConfig() {
      log.info("Configured allowed file types: {}", allowedFileTypes);
    }
}
