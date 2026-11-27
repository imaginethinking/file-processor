package dev.arunsamra.apigateway.security;

import dev.arunsamra.common.web.security.JsonAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(
      HttpSecurity http, JwtIssuer jwtIssuer, ObjectMapper objectMapper) throws Exception {
    JsonAuthenticationEntryPoint authenticationEntryPoint =
        new JsonAuthenticationEntryPoint(objectMapper);

    return http.csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize ->
            authorize
                .requestMatchers("/actuator/health/**", "/api/v1/dev/token")
                .permitAll()
                .anyRequest()
                .authenticated())
        .oauth2ResourceServer(oauth2 ->
            oauth2
                .jwt(Customizer.withDefaults())
                .authenticationEntryPoint(authenticationEntryPoint))
        .exceptionHandling(
            exceptions -> exceptions.authenticationEntryPoint(authenticationEntryPoint))
        .addFilterBefore(new GatewayJwtFilter(jwtIssuer), BearerTokenAuthenticationFilter.class)
        .build();
  }
}
