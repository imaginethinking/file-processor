package dev.arunsamra.common.web.config;

import dev.arunsamra.common.web.logging.MethodLoggingAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(ProceedingJoinPoint.class)
public class MethodLoggingAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  MethodLoggingAspect methodLoggingAspect() {
    return new MethodLoggingAspect();
  }
}
