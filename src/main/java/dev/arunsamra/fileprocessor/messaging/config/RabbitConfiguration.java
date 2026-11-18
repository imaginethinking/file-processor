package dev.arunsamra.fileprocessor.messaging.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitConfiguration {

    private final RabbitProperties rabbitProperties;

    @Bean
    public TopicExchange processingExchange() {
        return new TopicExchange(rabbitProperties.exchange());
    }

    @Bean
    public Queue processingQueue() {
        return new Queue(rabbitProperties.succeededQueue());
    }

    @Bean
    public Binding processBinding(Queue processingQueue, TopicExchange processingExchange) {
        return BindingBuilder
                .bind(processingQueue)
                .to(processingExchange)
                .with(rabbitProperties.succeededRoutingKey());
    }
}
