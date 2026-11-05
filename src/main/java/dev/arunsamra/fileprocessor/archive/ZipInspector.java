package dev.arunsamra.fileprocessor.archive;

import dev.arunsamra.fileprocessor.common.exception.ZipFileException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
public class ZipInspector {

    public List<ZipEntry> inspect(Path zipPath) {

        try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {

            return zipFile.stream()
                    .map(ZipEntry.class::cast)
                    .toList();

        } catch (IOException exception) {
            throw new ZipFileException(
                    "Unable to read ZIP file: " + zipPath.getFileName().toString(),
                    exception
            );
        }
    }
}
