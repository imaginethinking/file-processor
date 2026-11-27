package dev.arunsamra.common.web.correlation;

import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class CorrelationIdClientInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    String correlationId = MDC.get(CorrelationIdConstants.MDC_KEY);

    if (correlationId != null && !correlationId.isBlank()) {
      request.getHeaders().set(CorrelationIdConstants.HEADER_NAME, correlationId);
    }

    return execution.execute(request, body);
  }
}
