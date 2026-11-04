package dev.arunsamra.fileprocessor.processing;

import dev.arunsamra.fileprocessor.archive.ZipInspector;
import dev.arunsamra.fileprocessor.processing.dto.ProcessingRequest;
import dev.arunsamra.fileprocessor.processing.dto.ProcessingResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProcessingService {

    private final InputFileValidator inputFileValidator;
    private final ZipInspector zipInspector;

    public ResponseEntity<ProcessingResultResponse> process(ProcessingRequest request) {

        log.info("Starting processing for file: {}", request.getFileName());

        Path inputFile = this.inputFileValidator.validate(request.getFileName());

        List<ZipEntry> entries = zipInspector.inspect(inputFile);



        log.info("Completed processing for file: {}", request.getFileName());

        return null;
    }
}
