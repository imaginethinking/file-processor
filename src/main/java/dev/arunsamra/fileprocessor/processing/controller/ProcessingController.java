package dev.arunsamra.fileprocessor.processing.controller;

import dev.arunsamra.fileprocessor.processing.service.ProcessingService;
import dev.arunsamra.fileprocessor.processing.dto.InputFilesQueryParams;
import dev.arunsamra.fileprocessor.processing.dto.InputFilesResponse;
import dev.arunsamra.fileprocessor.processing.dto.ProcessingRequest;
import dev.arunsamra.fileprocessor.processing.dto.ProcessingResultResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<InputFilesResponse> getInputFiles(@Valid @ModelAttribute InputFilesQueryParams params) {
        InputFilesResponse response = processingService.getInputFiles(params);

        return ResponseEntity.ok(response);
    }
}
