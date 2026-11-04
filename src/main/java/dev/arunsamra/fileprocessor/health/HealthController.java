package dev.arunsamra.fileprocessor.health;

import dev.arunsamra.fileprocessor.health.dto.HealthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/v1/health")
public class HealthController {

    private final HealthService healthService;

    @GetMapping("/status")
    public ResponseEntity<HealthResponse> getStatus() {
        HealthResponse response = this.healthService.getStatus();
        return ResponseEntity.ok(response);
    }

}
