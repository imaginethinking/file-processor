package dev.arunsamra.fileprocessor.metadata.controller;

import dev.arunsamra.fileprocessor.metadata.MetadataFileReader;
import dev.arunsamra.fileprocessor.metadata.dto.ProcessedFileMetadataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/internal/v1/files")
public class MetadataController {

  private final MetadataFileReader metadataFileReader;

  @GetMapping("/{fileName}/metadata")
  public ResponseEntity<ProcessedFileMetadataResponse> getMetadata(@PathVariable String fileName) {
    return ResponseEntity.ok(metadataFileReader.read(fileName));
  }
}
