package dev.arunsamra.metadata.processedfile.model;

import jakarta.xml.bind.annotation.*;
import java.util.List;
import lombok.*;

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
