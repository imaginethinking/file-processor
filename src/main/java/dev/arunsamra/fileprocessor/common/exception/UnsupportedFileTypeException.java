package dev.arunsamra.fileprocessor.common.exception;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class UnsupportedFileTypeException extends RestException {
    public UnsupportedFileTypeException(List<String> fileNames) {
        super(HttpStatus.BAD_REQUEST,
                "Unsupported file type found",
                Map.of("files", fileNames));
    }
}
