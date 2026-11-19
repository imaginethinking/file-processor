package dev.arunsamra.metadata.processedfile.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
