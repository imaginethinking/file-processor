package dev.arunsamra.fileprocessor.common.exception;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class ServiceValidationException extends RestException {
    public ServiceValidationException(String message, Map<String, List<String>> errors) {
        super(HttpStatus.BAD_REQUEST, message, errors);
    }
}
