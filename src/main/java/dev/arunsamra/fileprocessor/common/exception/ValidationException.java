package dev.arunsamra.fileprocessor.common.exception;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class ValidationException extends RestException {
    public ValidationException(Map<String, List<String>> errors) {
        super(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    public ValidationException(String message, Map<String, List<String>> errors) {
        super(HttpStatus.BAD_REQUEST, message, errors);
    }
}
