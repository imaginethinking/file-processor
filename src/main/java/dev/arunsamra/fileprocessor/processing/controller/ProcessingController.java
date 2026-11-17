package dev.arunsamra.fileprocessor.processing.controller;

import dev.arunsamra.fileprocessor.processing.dto.InputFilesQueryParams;
import dev.arunsamra.fileprocessor.processing.dto.InputFilesResponse;
import dev.arunsamra.fileprocessor.processing.dto.ProcessingRequest;
import dev.arunsamra.fileprocessor.processing.dto.ProcessingResultResponse;
import dev.arunsamra.fileprocessor.processing.metadata.dto.ProcessedFileMetadataResponse;
import dev.arunsamra.fileprocessor.processing.service.ProcessedFileMetadataService;
import dev.arunsamra.fileprocessor.processing.service.ProcessingService;
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
    private final ProcessedFileMetadataService processedFileMetadataService;

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

    @GetMapping("/{fileName}/metadata")
    public ResponseEntity<ProcessedFileMetadataResponse> getMetadata(@PathVariable String fileName) {
        ProcessedFileMetadataResponse response = processedFileMetadataService.getMetadata(fileName);

        return ResponseEntity.ok(response);
    }
}
