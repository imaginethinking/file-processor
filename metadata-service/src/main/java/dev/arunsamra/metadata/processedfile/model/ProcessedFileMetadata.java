package dev.arunsamra.metadata.processedfile.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
