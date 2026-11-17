package dev.arunsamra.fileprocessor.processing.metadata.entity;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessedFileEntry {
    private String name;
    private String type;
    private long size;
}
