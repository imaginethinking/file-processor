package dev.arunsamra.fileprocessor.health;

import dev.arunsamra.fileprocessor.health.dto.HealthResponse;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    public HealthResponse getStatus() {
        return new HealthResponse("Running", "Spring Boot application is running");
    }

}
