package dev.arunsamra.fileprocessor.metadata;

import static org.junit.jupiter.api.Assertions.*;

import dev.arunsamra.fileprocessor.metadata.dto.ProcessedFileMetadataRequest;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    properties = {
      "spring.http.serviceclient.metadata.base-url=http://localhost:9099",
      "resilience4j.retry.instances.metadata-service.max-attempts=2",
      "resilience4j.retry.instances.metadata-service.wait-duration=10ms",
      "resilience4j.circuitbreaker.instances.metadata-service.minimum-number-of-calls=2",
      "resilience4j.circuitbreaker.instances.metadata-service.sliding-window-size=2",
      "resilience4j.circuitbreaker.instances.metadata-service.failure-rate-threshold=50"
    })
class MetadataClientResilienceTest {

  private MockWebServer mockWebServer;

  @Autowired private MetadataClient metadataClient;

  @Autowired private CircuitBreakerRegistry circuitBreakerRegistry;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start(9099);

    circuitBreaker().reset();
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.close();
  }

  @Test
  void shouldRetryWhenMetadataServiceRequestFails() {
    mockWebServer.enqueue(new MockResponse.Builder().code(500).build());

    mockWebServer.enqueue(
        new MockResponse.Builder()
            .code(200)
            .addHeader("Content-Type", "application/xml")
            .body("<processedFileMetadata/>")
            .build());

    byte[] response = metadataClient.createMetadata(request());

    assertArrayEquals("<processedFileMetadata/>".getBytes(StandardCharsets.UTF_8), response);
    assertEquals(2, mockWebServer.getRequestCount());
  }

  @Test
  void shouldOpenCircuitAfterRepeatedFailures() {
    mockWebServer.enqueue(new MockResponse.Builder().code(500).build());

    mockWebServer.enqueue(new MockResponse.Builder().code(500).build());

    assertThrows(Exception.class, () -> metadataClient.createMetadata(request()));

    assertThrows(Exception.class, () -> metadataClient.createMetadata(request()));

    assertEquals(CircuitBreaker.State.OPEN, circuitBreaker().getState());
  }

  @Test
  void shouldNotCallMetadataServiceWhenCircuitIsOpen() {
    circuitBreaker().transitionToOpenState();

    assertThrows(CallNotPermittedException.class, () -> metadataClient.createMetadata(request()));

    assertEquals(0, mockWebServer.getRequestCount());
  }

  private CircuitBreaker circuitBreaker() {
    return circuitBreakerRegistry.circuitBreaker("metadata-service");
  }

  private ProcessedFileMetadataRequest request() {
    return new ProcessedFileMetadataRequest("documents.zip", List.of());
  }
}
