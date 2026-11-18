package dev.arunsamra.fileprocessor.messaging.event;

import java.time.ZonedDateTime;

public record FileProcessingFailedEvent(
        String sourceFileName,
        ZonedDateTime failedAt,
        String reason
) implements ProcessingEvent {
    @Override
    public ProcessingEventType type() {
        return ProcessingEventType.FILE_PROCESSING_FAILURE;
    }
}
