package dev.arunsamra.apigateway.security;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties("security.jwt")
public record JwtProperties(
    Resource keystoreLocation, String keystorePassword, String keyAlias, Duration tokenTtl) {}
