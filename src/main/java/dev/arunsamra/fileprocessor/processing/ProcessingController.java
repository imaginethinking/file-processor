package dev.arunsamra.fileprocessor.processing;

import dev.arunsamra.fileprocessor.processing.dto.ProcessingRequest;
import dev.arunsamra.fileprocessor.processing.dto.ProcessingResultResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/v1/files")
public class ProcessingController {

    private final ProcessingService processingService;

    @PostMapping
    public ResponseEntity<ProcessingResultResponse> process(@Valid @RequestBody ProcessingRequest request) {
        ProcessingResultResponse response = this.processingService.process(request);
        return ResponseEntity.ok(response);
    }
}
