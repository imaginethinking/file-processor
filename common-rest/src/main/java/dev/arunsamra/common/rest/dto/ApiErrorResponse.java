package dev.arunsamra.common.rest.dto;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public record ApiErrorResponse(
        String status,
        String error,
        String message,
        Map<String, List<String>> details
) {
    private static final String FAILED = "FAILED";

    public static ApiErrorResponse of(HttpStatus httpStatus, String message) {
        return new ApiErrorResponse(FAILED, httpStatus.getReasonPhrase(), message, null);
    }

    public static ApiErrorResponse of(HttpStatus httpStatus, String message, Map<String, List<String>> details) {
        return new ApiErrorResponse(FAILED, httpStatus.getReasonPhrase(), message, details);
    }
}
