package dev.arunsamra.apigateway.config;

import dev.arunsamra.common.web.correlation.CorrelationIdConstants;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.AfterFilterFunctions.removeResponseHeader;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
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
                .filter((request, next) -> {
                    ServerRequest.Builder forwardedRequest = ServerRequest.from(request);

                    String bearerToken = (String) request.servletRequest()
                            .getAttribute("gatewayIssuedBearerToken");

                    if (bearerToken != null) {
                        forwardedRequest.header(
                                HttpHeaders.AUTHORIZATION,
                                bearerToken
                        );
                    }

                    String correlationId = MDC.get(CorrelationIdConstants.MDC_KEY);

                    if (correlationId != null && !correlationId.isBlank()) {
                        forwardedRequest.header(
                                CorrelationIdConstants.HEADER_NAME,
                                correlationId
                        );
                    }

                    return next.handle(forwardedRequest.build());
                })
                .after(removeResponseHeader(
                        CorrelationIdConstants.HEADER_NAME
                ))
                .build();
    }
}
