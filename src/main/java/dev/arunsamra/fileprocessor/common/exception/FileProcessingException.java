package dev.arunsamra.fileprocessor.common.exception;

import org.springframework.http.HttpStatus;

public class FileProcessingException extends RestException {
    public FileProcessingException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
    }
}
