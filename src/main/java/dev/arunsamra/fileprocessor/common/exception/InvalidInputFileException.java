package dev.arunsamra.fileprocessor.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidInputFileException extends RestException {
    public InvalidInputFileException(String message, String fileName) {
        super(HttpStatus.BAD_REQUEST, message + " : " + fileName);
    }

    public InvalidInputFileException(String message, String fileName, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message + " : " + fileName, cause);
    }
}
