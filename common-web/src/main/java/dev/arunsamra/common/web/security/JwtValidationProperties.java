package dev.arunsamra.common.web.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "security.jwt.validation")
public record JwtValidationProperties(
        String issuer,
        Resource publicKeyLocation
) {
    public JwtValidationProperties {
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalArgumentException("security.jwt.validation.issuer must be configured");
        }
    }
}
