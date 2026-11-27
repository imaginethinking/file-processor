package dev.arunsamra.apigateway.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class GatewayJwtFilter extends OncePerRequestFilter {

  private final JwtIssuer jwtIssuer;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authorization != null || request.getRequestURI().startsWith("/actuator/health")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = jwtIssuer.issue();
    String bearerToken = "Bearer " + token;
    log.debug("Issued JWT to request");

    request.setAttribute("gatewayIssuedBearerToken", bearerToken);

    HttpServletRequest wrappedRequest =
        new HttpServletRequestWrapper(request) {

          @Override
          public String getHeader(String name) {
            if (HttpHeaders.AUTHORIZATION.equalsIgnoreCase(name)) {
              return bearerToken;
            }

            return super.getHeader(name);
          }

          @Override
          public Enumeration<String> getHeaders(String name) {
            if (HttpHeaders.AUTHORIZATION.equalsIgnoreCase(name)) {
              return Collections.enumeration(Collections.singletonList(bearerToken));
            }

            return super.getHeaders(name);
          }
        };

    filterChain.doFilter(wrappedRequest, response);
  }
}
