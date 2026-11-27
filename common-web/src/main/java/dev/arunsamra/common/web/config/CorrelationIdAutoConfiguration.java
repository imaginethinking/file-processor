package dev.arunsamra.common.web.config;

import dev.arunsamra.common.web.correlation.CorrelationIdFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

@AutoConfiguration
public class CorrelationIdAutoConfiguration {

  @Bean
  public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilter() {
    FilterRegistrationBean<CorrelationIdFilter> registration = new FilterRegistrationBean<>();

    registration.setFilter(new CorrelationIdFilter());
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE);

    return registration;
  }
}
