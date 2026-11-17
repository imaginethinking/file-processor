package dev.arunsamra.fileprocessor.processing.metadata.entity;

import jakarta.xml.bind.annotation.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@XmlRootElement(name = "processedFileMetadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessedFileMetadata {

    private String originalFilename;
    private String processedAt;
    private int count;

    @XmlElementWrapper(name = "files")
    @XmlElement(name = "file")
    private List<ProcessedFileEntry> files;
}
