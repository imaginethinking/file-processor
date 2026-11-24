package dev.arunsamra.common.web.config;

import dev.arunsamra.common.web.exception.CommonRestExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class RestExceptionHandlerAutoConfiguration {

    @Bean
    CommonRestExceptionHandler commonRestExceptionHandler() {
        return new CommonRestExceptionHandler();
    }

}
