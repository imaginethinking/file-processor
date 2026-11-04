package dev.arunsamra.fileprocessor.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@Getter
public class RestException extends RuntimeException {

    private final HttpStatus status;
    private final Map<String, List<String>> errors;

    protected RestException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.errors = null;
    }

    protected RestException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errors = null;
    }

    protected RestException(HttpStatus status, String message, Map<String, List<String>> errors) {
        super(message);
        this.status = status;
        this.errors = errors;
    }

    protected RestException(HttpStatus status, String message, Map<String, List<String>> errors, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.errors = errors;
    }
}
