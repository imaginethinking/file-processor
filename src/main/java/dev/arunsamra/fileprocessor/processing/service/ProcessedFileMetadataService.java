package dev.arunsamra.fileprocessor.processing.service;

import dev.arunsamra.fileprocessor.archive.ZipEntryReader;
import dev.arunsamra.fileprocessor.config.FileProcessorPathProperties;
import dev.arunsamra.fileprocessor.processing.metadata.dto.ProcessedFileEntryResponse;
import dev.arunsamra.fileprocessor.processing.metadata.dto.ProcessedFileMetadataResponse;
import dev.arunsamra.fileprocessor.processing.metadata.entity.ProcessedFileMetadata;
import dev.arunsamra.fileprocessor.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProcessedFileMetadataService {

    private static final String METADATA_FILE_NAME = "metadata.xml";

    private final FileProcessorPathProperties pathProperties;
    private final ZipEntryReader zipEntryReader;
    private final XmlMapper xmlMapper;

    public ProcessedFileMetadataResponse getMetadata(String fileName) {

        Path processedZip = pathProperties.processedDirectory()
                .resolve(fileName);

        ProcessedFileMetadata metadata = zipEntryReader.read(
                processedZip,
                METADATA_FILE_NAME,
                inputStream -> xmlMapper.unmarshal(
                        inputStream,
                        ProcessedFileMetadata.class
                )
        );

        List<ProcessedFileEntryResponse> files = metadata.getFiles().stream()
                .map(file -> new ProcessedFileEntryResponse(
                        file.getName(),
                        file.getType(),
                        file.getSize()
                ))
                .toList();

        return new ProcessedFileMetadataResponse(
                metadata.getOriginalFilename(),
                metadata.getProcessedAt(),
                metadata.getCount(),
                files
        );
    }
}
