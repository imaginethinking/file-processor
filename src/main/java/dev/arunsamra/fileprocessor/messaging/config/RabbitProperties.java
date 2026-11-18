package dev.arunsamra.fileprocessor.messaging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq")
public record RabbitProperties(
        String exchange,
        String succeededQueue,
        String failedQueue,
        String succeededRoutingKey,
        String failedRoutingKey
) {
}
