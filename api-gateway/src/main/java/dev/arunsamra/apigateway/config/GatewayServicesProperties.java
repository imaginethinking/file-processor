package dev.arunsamra.apigateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("gateway.services")
public record GatewayServicesProperties(String fileProcessing, String metadata) {}
