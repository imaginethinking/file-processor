package dev.arunsamra.fileprocessor.messaging.publisher;

import dev.arunsamra.fileprocessor.messaging.config.RabbitProperties;
import dev.arunsamra.fileprocessor.messaging.event.ProcessingEvent;
import dev.arunsamra.fileprocessor.messaging.event.ProcessingEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitProcessingEventPublisher implements ProcessingEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties rabbitProperties;

    @Override
    public void publish(ProcessingEvent event) {
        rabbitTemplate.convertAndSend(
                rabbitProperties.exchange(),
                getRoutingKey(event.type()),
                event
        );
    }

    private String getRoutingKey(ProcessingEventType eventType) {
        return switch (eventType) {
            case FILE_PROCESSED -> rabbitProperties.succeededRoutingKey();
            case FILE_PROCESSING_FAILURE -> rabbitProperties.failedRoutingKey();
        };
    }
}
