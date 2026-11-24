package dev.arunsamra.common.web.config;

import dev.arunsamra.common.web.correlation.CorrelationIdClientInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class CorrelationIdClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    CorrelationIdClientInterceptor correlationIdClientInterceptor() {
        return new CorrelationIdClientInterceptor();
    }
}