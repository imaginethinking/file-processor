package dev.arunsamra.apigateway.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.time.Duration;

@ConfigurationProperties("security.jwt")
public record JwtProperties(
        Resource keystoreLocation,
        String keystorePassword,
        String keyAlias,
        Duration tokenTtl
) {
}
