package dev.arunsamra.fileprocessor.common.dto;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, List<String>> details
) {
    public static ApiErrorResponse of(HttpStatus httpStatus, String message, String path) {
        return new ApiErrorResponse(Instant.now(), httpStatus.value(), httpStatus.getReasonPhrase(), message, path, null);
    }

    public static ApiErrorResponse of(HttpStatus httpStatus, String message, String path, Map<String, List<String>> details) {
        return new ApiErrorResponse(Instant.now(), httpStatus.value(), httpStatus.getReasonPhrase(), message, path, details);
    }
}
