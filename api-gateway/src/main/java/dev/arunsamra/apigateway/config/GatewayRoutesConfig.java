package dev.arunsamra.apigateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
@ConfigurationProperties
public class GatewayRoutesConfig {

    @Bean
    RouterFunction<ServerResponse> fileProcessingRoute(GatewayServicesProperties services) {
        return serviceRoute("file-processing-service", "files", services.fileProcessing());
    }

    @Bean
    RouterFunction<ServerResponse> metadataRoute(GatewayServicesProperties services) {
        return serviceRoute("metadata-service", "metadata", services.metadata());
    }

    private static RouterFunction<ServerResponse> serviceRoute(String id, String resource, String targetUrl) {
        return route(id)
                .route(path("/api/v1/" + resource + "/**"), http())
                .before(uri(targetUrl))
                .before(rewritePath("/api/v1/" + resource + "(?<segment>.*)", "/internal/v1/" + resource + "${segment}"))
                .build();
    }
}
