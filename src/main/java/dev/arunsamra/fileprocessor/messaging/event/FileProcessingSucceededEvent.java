package dev.arunsamra.fileprocessor.messaging.event;

import java.time.ZonedDateTime;

public record FileProcessingSucceededEvent(
        String sourceFileName,
        ZonedDateTime processedAt,
        int fileCount
) implements ProcessingEvent {
    @Override
    public ProcessingEventType type() {
        return ProcessingEventType.FILE_PROCESSED;
    }
}
