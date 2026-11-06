package dev.arunsamra.fileprocessor.common.dto;

import dev.arunsamra.fileprocessor.processing.ProcessingStatus;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public record ApiErrorResponse(
        ProcessingStatus status,
        String error,
        String message,
        Map<String, List<String>> details
) {
    public static ApiErrorResponse of(HttpStatus httpStatus, String message) {
        return new ApiErrorResponse(ProcessingStatus.FAILED, httpStatus.getReasonPhrase(), message, null);
    }

    public static ApiErrorResponse of(HttpStatus httpStatus, String message, Map<String, List<String>> details) {
        return new ApiErrorResponse(ProcessingStatus.FAILED, httpStatus.getReasonPhrase(), message, details);
    }
}
