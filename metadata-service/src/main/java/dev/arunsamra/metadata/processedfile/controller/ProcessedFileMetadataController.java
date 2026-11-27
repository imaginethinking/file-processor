package dev.arunsamra.metadata.processedfile.controller;

import dev.arunsamra.metadata.processedfile.dto.ProcessedFileMetadataRequest;
import dev.arunsamra.metadata.processedfile.dto.ProcessedFileMetadataResponse;
import dev.arunsamra.metadata.processedfile.service.ProcessedFileMetadataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/metadata/processed-files")
public class ProcessedFileMetadataController {

  private final ProcessedFileMetadataService metadataService;

  @PostMapping(
      produces = MediaType.APPLICATION_XML_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<byte[]> create(@Valid @RequestBody ProcessedFileMetadataRequest request) {
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_XML)
        .body(metadataService.create(request));
  }

  @PostMapping(
      path = "/unmarshal",
      consumes = MediaType.APPLICATION_XML_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProcessedFileMetadataResponse> unmarshal(@RequestBody byte[] xml) {
    return ResponseEntity.ok(metadataService.read(xml));
  }
}
