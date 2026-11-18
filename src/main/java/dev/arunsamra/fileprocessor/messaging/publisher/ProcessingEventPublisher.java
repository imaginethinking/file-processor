package dev.arunsamra.fileprocessor.messaging.publisher;

import dev.arunsamra.fileprocessor.messaging.event.ProcessingEvent;

public interface ProcessingEventPublisher {

    void publish(ProcessingEvent event);
}
