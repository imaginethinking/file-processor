package dev.arunsamra.common.rest.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RestException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
