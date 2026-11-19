package dev.arunsamra.metadata.processedfile.service;

import dev.arunsamra.metadata.xml.service.MarshallingService;
import dev.arunsamra.metadata.processedfile.dto.ProcessedFileMetadataRequest;
import dev.arunsamra.metadata.processedfile.model.ProcessedFileEntry;
import dev.arunsamra.metadata.processedfile.model.ProcessedFileMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProcessedFileMetadataService {

    private final MarshallingService marshallingService;

    public byte[] create(ProcessedFileMetadataRequest request) {
        ProcessedFileMetadata metadata = createMetadata(request);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        marshallingService.marshal(metadata, outputStream);

        return outputStream.toByteArray();
    }

    private ProcessedFileMetadata createMetadata(ProcessedFileMetadataRequest request) {
        List<ProcessedFileEntry> files = request.files().stream()
                .map(file -> new ProcessedFileEntry(
                        file.name(),
                        file.type(),
                        file.size()
                ))
                .toList();

        return new ProcessedFileMetadata(
                request.originalFilename(),
                LocalDateTime.now(ZoneOffset.UTC).toString(),
                files.size(),
                files
        );
    }
}
