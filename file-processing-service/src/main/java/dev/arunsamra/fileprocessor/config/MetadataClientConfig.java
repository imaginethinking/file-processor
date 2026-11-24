package dev.arunsamra.fileprocessor.config;

import dev.arunsamra.common.web.correlation.CorrelationIdClientInterceptor;
import dev.arunsamra.fileprocessor.metadata.MetadataClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@ImportHttpServices(group = "metadata", types = MetadataClient.class)
public class MetadataClientConfig {

    @Bean
    RestClientHttpServiceGroupConfigurer metadataClientConfigurer(
            CorrelationIdClientInterceptor correlationIdClientInterceptor
    ) {
        return groups -> groups
                .filterByName("metadata")
                .forEachClient((group, builder) -> builder
                        .requestInterceptor(correlationIdClientInterceptor)
                        .requestInterceptor((request, body, execution) -> {
                            Authentication authentication =
                                    SecurityContextHolder.getContext().getAuthentication();

                            if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
                                request.getHeaders().setBearerAuth(
                                        jwtAuthentication.getToken().getTokenValue()
                                );
                            }

                            return execution.execute(request, body);
                        })
                );
    }
}